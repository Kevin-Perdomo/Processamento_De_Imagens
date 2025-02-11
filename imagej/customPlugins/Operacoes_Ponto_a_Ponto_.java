import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Operacoes_Ponto_a_Ponto_ implements PlugIn, DialogListener {
	public void run(String arg) {
		ImagePlus imagem = IJ.getImage();
		
		if (imagem.getType() == ImagePlus.COLOR_RGB) {
			IG(imagem);	
		}
		else {
			IJ.error("Imagem não é RGB");
		}
		
	}
	
	public void IG(ImagePlus imagem) {

		ImagePlus imagemCopia = imagem.duplicate();
		imagemCopia.show();
		
		GenericDialog interfaceGrafica = new GenericDialog("Operações Ponto a Ponto");
		interfaceGrafica.addDialogListener(this);
		interfaceGrafica.addSlider("Brilho", -255, 255, 0, 1);
		interfaceGrafica.addSlider("Contraste", -255, 255, 0, 1);
		interfaceGrafica.addSlider("Solarização", 0, 255, 0, 1);
		interfaceGrafica.addSlider("Dessaturação", 0, 1, 1, 0.1);
		
		interfaceGrafica.showDialog();		
		
		
		if (interfaceGrafica.wasCanceled()) {
			IJ.showMessage("PlugIn cancelado!");
		}
		else {
			if (interfaceGrafica.wasOKed()) {
		        IJ.showMessage("Plugin encerrado com sucesso!");
			}
		}
	}
	
	@Override
	public boolean dialogItemChanged(GenericDialog interfaceGrafica, AWTEvent e) {
		if (interfaceGrafica.wasCanceled()) return false;		
		processarImagem(interfaceGrafica);
        return true;	
	}
	
	public void processarImagem(GenericDialog interfaceGrafica){
		int idImagens[] = WindowManager.getIDList();
		ImagePlus imagem = WindowManager.getImage(idImagens[0]), novaImagem = WindowManager.getImage(idImagens[1]);
		ImageProcessor processador = imagem.getProcessor(), novoProcessador = novaImagem.getProcessor();

		int brilho = (int)interfaceGrafica.getNextNumber(), contraste = (int)interfaceGrafica.getNextNumber(),
		solarizacao = (int)interfaceGrafica.getNextNumber(), largura_imagem = imagem.getWidth(), 
		altura_imagem = imagem.getHeight(), media;
		
		int valorPixel[] = {0,0,0}, novoValorPixel[] = {0,0,0};
		
		Double saturacao = interfaceGrafica.getNextNumber();
		float fatorC = fatorContraste(contraste);
		
		
		for (int x = 0; x < largura_imagem; x++) {	
			for (int y = 0; y < altura_imagem; y++) {
				
				fatorC = (259f *(contraste + 255f ))/(255f*(259f-contraste));
				media = (int)((valorPixel[0]+valorPixel[1]+valorPixel[2])/3.0);
				
				processador.getPixel(x, y, valorPixel);
				novoValorPixel[0] = calcularPixel(valorPixel[0], brilho, contraste, saturacao, solarizacao, media, fatorC);
				novoValorPixel[1] = calcularPixel(valorPixel[1], brilho, contraste, saturacao, solarizacao, media, fatorC);
				novoValorPixel[2] = calcularPixel(valorPixel[2], brilho, contraste, saturacao, solarizacao, media, fatorC);
				novoProcessador.putPixel(x, y, novoValorPixel);
			}
		}
		
		novaImagem.setProcessor(novoProcessador);
		novaImagem.show();
		imagem.setProcessor(processador);
		imagem.show();
	}
	
	public int calcularPixel(int valorPixel, int brilho, int contraste, Double saturacao, int solarizacao, int media, float fatorC) {
		
		valorPixel = calcularBrilho(valorPixel, brilho);
		valorPixel = calcularContraste(valorPixel, fatorC);
		valorPixel = calcularDessaturacao(valorPixel, saturacao, media);
		valorPixel = checarValorMaxMin(valorPixel);
		valorPixel = calcularSolarizacao(valorPixel, solarizacao);
		
		return valorPixel;	
	}
	
	public float fatorContraste(int contraste) {
		float fator = (259f*(contraste + 255f))/(255f*(259f-contraste));
		return fator;
	}

	public int calcularBrilho(int valorPixel, int brilho) {
		valorPixel = valorPixel + brilho;
		return valorPixel;
	}
	
	public int calcularContraste(int valorPixel, float fatorC ) {
		valorPixel = (int)((valorPixel-128) * fatorC + 128);
		return valorPixel;
	}
	
	public int calcularDessaturacao(int valorPixel, Double saturacao, int media){
		if(saturacao<1) {
			valorPixel = (int)(media - ((valorPixel - media) * saturacao));			
		}
		return valorPixel;
	}
	
	
	public int calcularSolarizacao(int valorPixel, int solarizacao) {
		if(valorPixel < solarizacao) {
			valorPixel = 255 - valorPixel;
		}
		return valorPixel;
	}
	
	public int checarValorMaxMin(int valorPixel) {
		if(valorPixel >= 255){
			valorPixel = 255;
		}else {
			if(valorPixel <= 0) {
				valorPixel = 0;
			}
		}
		return valorPixel;
	}
	
	
}