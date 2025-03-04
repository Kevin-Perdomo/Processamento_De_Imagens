import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Filtros_Lineares_ implements PlugIn{
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
		
		ImagePlus imagemPassaBaixaMedia  = imagemOriginal.duplicate();
		imagemPassaBaixaMedia.setTitle("PassaBaixaMedia");     
		ImageProcessor processadorPassaBaixaMedia = imagemPassaBaixaMedia.getProcessor();
		
		ImagePlus imagemPassaAlta = imagemOriginal.duplicate();
		imagemPassaAlta.setTitle("PassaAlta");
		ImageProcessor processadorPassaAlta = imagemPassaAlta.getProcessor();
		
		ImagePlus imagemBorda = imagemOriginal.duplicate();
		imagemBorda.setTitle("Borda");
		ImageProcessor processadorBorda = imagemBorda.getProcessor();
		
		
		int x,y;
		for ( x = 1; x < larguraImagem-1; x++) {	
			for ( y = 1; y < alturaImagem-1; y++) {
				passaBaixaMedia(processadorOriginal, processadorPassaBaixaMedia, x, y);
				passaAlta(processadorOriginal, processadorPassaAlta, x, y);
				borda(processadorOriginal, processadorBorda, x, y);
			}
		}
		
		imagemPassaBaixaMedia.setProcessor(processadorPassaBaixaMedia);
		imagemPassaAlta.setProcessor(processadorPassaAlta);
		imagemBorda.setProcessor(processadorBorda);
		
		imagemPassaBaixaMedia.show();
		imagemPassaAlta.show();
		imagemBorda.show();
			
	}
	
	public void passaBaixaMedia(ImageProcessor processadorOriginal, ImageProcessor processadorPassaBaixaMedia, int x, int y) {
		
		int media = 0;
		
		for(int a = x-1; a <= x+1; a++) {	
			for (int b = y-1; b <= y+1; b++) {
				media = media + processadorOriginal.getPixel(a, b);
			}
		}
		
		media = media/9;
		
		if(media>255) {
			media = 255;
		}else {
			if(media<0) {
				media = 0;
			}
		}
		
		processadorPassaBaixaMedia.putPixel(x, y, (int)media);
		
	}
		
	public void passaAlta(ImageProcessor processadorOriginal, ImageProcessor processadorPassaAlta, int x, int y) {
		int[][] mascara = {{1,-2,1},{-2,5,-2},{1,-2,1}};
		
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
	
		processadorPassaAlta.putPixel(x, y, soma);
		
	}
	
	public void borda(ImageProcessor processadorOriginal, ImageProcessor processadorBorda,  int x, int y) {
	
		int soma = 0;
		int[][] mascara = {{1,0,-1},{1,0,-1},{1,0,-1}};
		
		int r=0, s=0;
		
		for(int a = x-1; a <= x+1; a++) {	
			for (int b = y-1; b <= y+1; b++) {
				soma = soma + (processadorOriginal.getPixel(a, b)*(mascara[r][s]));
				r++;
			}
			s++;
			r=0;
		}
			
		if(soma>=255) {
			soma = 255;
		}else {
			if(soma<0) {
				soma = 0;
			}
		}
		
		processadorBorda.putPixel(x, y, soma);
		
	}
}