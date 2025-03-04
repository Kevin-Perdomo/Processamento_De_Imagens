import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Filtros_Nao_Lineares_ implements PlugIn{
	public void run(String arg) {
		ImagePlus imagem = IJ.getImage();
		
		if (imagem.getType() == ImagePlus.GRAY8) {
			processarImagem(imagem);
		}
		else {
			IJ.error("Imagem não é 8_Bits");
		}	
	}
	
public void processarImagem(ImagePlus imagemOriginal) {
		
		ImageProcessor processadorOriginal = imagemOriginal.getProcessor();
		
		int larguraImagem = imagemOriginal.getWidth(), alturaImagem = imagemOriginal.getHeight();
		
		ImagePlus imagemVertical  = imagemOriginal.duplicate();
		imagemVertical.setTitle("Vertical");     
		ImageProcessor processadorVertical = imagemVertical.getProcessor();
		
		ImagePlus imagemHorizontal = imagemOriginal.duplicate();
		imagemHorizontal.setTitle("Horizontal");
		ImageProcessor processadorHorizontal = imagemHorizontal.getProcessor();
		
		ImagePlus imagemFusao = imagemOriginal.duplicate();
		imagemFusao.setTitle("Fusao");
		ImageProcessor processadorFusao = imagemFusao.getProcessor();
		
		
		int x,y;
		for ( x = 1; x < larguraImagem-1; x++) {	
			for ( y = 1; y < alturaImagem-1; y++) {
				vertical(processadorOriginal, processadorVertical, x, y);
				horizontal(processadorOriginal, processadorHorizontal, x, y);
				fusao(processadorVertical, processadorHorizontal, processadorFusao, x, y);
			}
		}
		
		imagemVertical.setProcessor(processadorVertical);
		imagemHorizontal.setProcessor(processadorHorizontal);
		imagemFusao.setProcessor(processadorFusao);
		
		imagemVertical.show();
		imagemHorizontal.show();
		imagemFusao.show();
			
	}
	
	public void vertical(ImageProcessor processadorOriginal, ImageProcessor processadorVertical, int x, int y) {
		int[][] mascara = {{-1,0,1},{-2,0,2},{-1,0,1}};
		
		int r = 0, s=0;
		int soma = 0;
		
		for(int a = x-1; a <= x+1; a++) {	
			for (int b = y-1; b <= y+1; b++) {
				soma = soma + (processadorOriginal.getPixel(a, b)*(mascara[r][s]));
				r++;
			}
			s++;
			r=0;
		}
		
		
		if(soma>255) {
			soma = 255;
		}else {
			if(soma<0) {
				soma = 0;
			}
		}
		
		processadorVertical.putPixel(x, y, soma);
		
	}
		
	public void horizontal(ImageProcessor processadorOriginal, ImageProcessor processadorHorizontal, int x, int y) {
		int[][] mascara = {{1,2,1},{0,0,0},{-1,-2,-1}};
		
		int r = 0, s=0;
		int soma = 0;
		
		for(int a = x-1; a <= x+1; a++) {	
			for (int b = y-1; b <= y+1; b++) {
				soma = soma + (processadorOriginal.getPixel(a, b)*(mascara[r][s]));
				r++;
			}
			s++;
			r=0;
		}
		
		
		if(soma>255) {
			soma = 255;
		}else {
			if(soma<0) {
				soma = 0;
			}
		}
	
		processadorHorizontal.putPixel(x, y, soma);
		
	}
	
	public void fusao(ImageProcessor processadorVertical, ImageProcessor processadorHorizontal, ImageProcessor processadorFusao, int x, int y) {
	
		double resultado = 0;
		
	//	resultado = Math.sqrt((processadorVertical.getPixel(a, b)*processadorVertical.getPixel(a, b))
	//			+(processadorHorizontal.getPixel(a, b)*processadorHorizontal.getPixel(a, b)));
		
		for(int a = x-1; a <= x+1; a++) {	
			for (int b = y-1; b <= y+1; b++) {
				
				resultado = Math.sqrt((processadorVertical.getPixel(a, b)*processadorVertical.getPixel(a, b))
						+(processadorHorizontal.getPixel(a, b)*processadorHorizontal.getPixel(a, b)));
			}
		}
		
		if(resultado>=255) {
			resultado = 255;
		}else {
			if(resultado<0) {
				resultado = 0;
			}
		}
		
		processadorFusao.putPixel(x, y, (int)resultado);
		
	}
}