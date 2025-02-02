import ij.*;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Inverter_Imagem_Nivel_Pixel_ implements PlugIn {
	public void run(String arg) {
		ImagePlus image = IJ.getImage();
		split_image(image, image.getWidth(), image.getHeight());
	}	
	

	public void split_image(ImagePlus image, int width, int height){
		ImageProcessor processor = image.getProcessor();
		int valorPixel[] = {0,0,0};

		//IJ.error("inside split_image");
		
		for(int channel = 0; channel < 2; channel++){
			//IJ.error("inside for loop");
			
			ImageProcessor newProcessor = new ByteProcessor(width, height);
			System.err.println(width);
			System.err.println(height);
			//IJ.error("after processor");
			
			for(int i = 0; i < width; i++){
				for(int j = 0; j < height; i++){
					valorPixel = processor.getPixel(i, j, valorPixel);
					newProcessor.set(i, j, valorPixel[channel]);
				}
			}
			IJ.error("before new image");
			IJ.error("antes");
			IJ.error("depois");
			
		}
	}

	public void inverter_imagem(ImagePlus imagem)
	{
		ImageProcessor processador = imagem.getProcessor();
		
		if (imagem.getType() == ImagePlus.GRAY8) {
			inverter_pixels_8bits(processador, imagem.getWidth(), imagem.getHeight());
			IJ.log("Imagem Cinza");
		}
		else {
			if (imagem.getType() == ImagePlus.COLOR_RGB) {
				inverter_pixels_RGB(processador, imagem.getWidth(), imagem.getHeight());
				IJ.log("Imagem Colorida");
			}
			else {
				IJ.error("Imagem não suportada");
			}
		}	
		imagem.updateAndDraw();
	}	
	
	public void inverter_pixels_8bits(ImageProcessor processador, int largura_imagem, int altura_imagem)
	{
		int x, y, valorPixel;
		IJ.log(String.valueOf(largura_imagem));
		IJ.log(String.valueOf(altura_imagem));
		for (x = 0; x < largura_imagem; x++) {
			for (y = 0; y < altura_imagem; y++) {
				valorPixel = processador.getPixel(x, y);
				processador.putPixel(x, y, 255 - valorPixel);
			}	
		}
	}
	
	public void inverter_pixels_RGB(ImageProcessor processador, int largura_imagem, int altura_imagem)
	{
		int x, y, valorPixel[] = {0,0,0};
		
		for (x = 0; x < largura_imagem; x++) {
			for (y = 0; y < altura_imagem; y++) {
				valorPixel = processador.getPixel(x, y, valorPixel);
				valorPixel[0] = 255 - valorPixel[0];
				valorPixel[1] = 255 - valorPixel[1];
				valorPixel[2] = 255 - valorPixel[2];
				processador.putPixel(x, y, valorPixel);
			}	
		}
	}
}
