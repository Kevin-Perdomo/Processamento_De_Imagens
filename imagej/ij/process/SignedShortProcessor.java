package ij.process;
import java.util.*;
import java.awt.*;
import java.awt.image.*;


/** An extended ShortProcessor that supports signed 16-bit images (experimental). */
public class SignedShortProcessor extends ShortProcessor {

	/** Creates a blank SignedShortProcessor with the specified dimensions. */
	public SignedShortProcessor(int width, int height) {
		super(width, height);
	}
	
	public SignedShortProcessor(ShortProcessor ip) {
		super(ip.getWidth(), ip.getHeight());
		for (int i=0; i<getPixelCount(); i++)
			set(i,ip.get(i));
	}

	@Override
	public void findMinAndMax() {
		if (fixedScale || pixels==null)
			return;
		int size = width*height;
		int value;
		int min = pixels[0];
		int max = pixels[0];
		for (int i=1; i<size; i++) {
			value = pixels[i];
			if (value<min)
				min = value;
			else if (value>max)
				max = value;
		}
		this.min = min;
		this.max = max;
		minMaxSet = true;
	}
	
	/** Create an 8-bit AWT image by scaling pixels in the range min-max to 0-255. */
	@Override
	public Image createImage() {
		if (!minMaxSet)
			findMinAndMax();
		boolean firstTime = pixels8==null;
		boolean thresholding = minThreshold!=NO_THRESHOLD && lutUpdateMode<NO_LUT_UPDATE;
		//ij.IJ.log("createImage: "+firstTime+"  "+lutAnimation+"  "+thresholding);
		if (firstTime || !lutAnimation)
			create8BitImage(thresholding&&lutUpdateMode==RED_LUT);
		if (cm==null)
			makeDefaultColorModel();
		if (thresholding) {
			int t1 = (int)minThreshold;
			int t2 = (int)maxThreshold;
			int size = width*height;
			int value;
			if (lutUpdateMode==BLACK_AND_WHITE_LUT) {
				for (int i=0; i<size; i++) {
					value = (pixels[i]);
					if (value>=t1 && value<=t2)
						pixels8[i] = (byte)255;
					else
						pixels8[i] = (byte)0;
				}
			} else { // threshold red
				for (int i=0; i<size; i++) {
					value = (pixels[i]);
					if (value>=t1 && value<=t2)
						pixels8[i] = (byte)255;
				}
			}
		}
		return createBufferedImage();
	}
	
	// create 8-bit image by linearly scaling from 16-bits to 8-bits
	private byte[] create8BitImage(boolean thresholding) {
		int size = width*height;
		if (pixels8==null)
			pixels8 = new byte[size];
		int value;
		int min2=(int)getMin(), max2=(int)getMax();
		int maxValue = 255;
		double scale = 256.0/(max2-min2+1);
		if (thresholding) {
			maxValue = 254;
			scale = 255.0/(max2-min2+1);
		}
		for (int i=0; i<size; i++) {
			value = (pixels[i])-min2;
			if (value<0) value = 0;
			value = (int)(value*scale+0.5);
			if (value>maxValue) value = maxValue;
			pixels8[i] = (byte)value;
		}
		return pixels8;
	}

	@Override
	public int getPixel(int x, int y) {
		if (x>=0 && x<width && y>=0 && y<height)
			return pixels[y*width+x];
		else
			return 0;
	}

	@Override
	public final int get(int x, int y) {
		return pixels[y*width+x];
	}
	
	protected void process(int op, double value) { //wsr
		int v1, v2;
		double range = getMax()-getMin();
		int min2 = (int)getMin();
		int max2 = (int)getMax();
		int fgColor2 = fgColor;
		int intValue = (int)value;
		
		for (int y=roiY; y<(roiY+roiHeight); y++) {
			int i = y * width + roiX;
			for (int x=roiX; x<(roiX+roiWidth); x++) {
				v1 = (pixels[i]);
				switch(op) {
					case INVERT:
						v2 = max2 - (v1 - min2);
						break;
					case FILL:
						v2 = fgColor2;
						break;
					case SET:
						v2 = intValue;
						break;
					case ADD:
						v2 = v1 + intValue;
						break;
					case MULT:
						v2 = (int)Math.round(v1*value);
						break;
					case AND:
						v2 = v1 & intValue;
						break;
					case OR:
						v2 = v1 | intValue;
						break;
					case XOR:
						v2 = v1 ^ intValue;
						break;
					case GAMMA:
						if (range<=0.0 || v1==min2)
							v2 = v1;
						else					
							v2 = (int)(Math.exp(value*Math.log((v1-min2)/range))*range+min2);
						break;
					case LOG:
						if (v1<=0)
							v2 = 0;
						else 
							v2 = (int)(Math.log(v1)*(max2/Math.log(max2)));
						break;
					case EXP:
						v2 = (int)(Math.exp(v1*(Math.log(max2)/max2)));
						break;
					case SQR:
						double d1 = v1;
						v2 = (int)(d1*d1);
						break;
					case SQRT:
						v2 = (int)Math.sqrt(v1);
						break;
					case ABS:
						v2 = (int)Math.abs(v1);
						break;
					case MINIMUM:
						if (v1<value)
							v2 = intValue;
						else
							v2 = v1;
						break;
					case MAXIMUM:
						if (v1>value)
							v2 = intValue;
						else
							v2 = v1;
						break;
					 default:
					 	v2 = v1;
				}
				if (v2 < -32768)
					v2 = -32768;
				if (v2 > 32767)
					v2 = 32767;
				pixels[i++] = (short)v2;
			}
		}
    }
	
}


