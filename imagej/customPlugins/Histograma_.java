import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Histograma_ implements  PlugIn, DialogListener{
	public void run(String arg) {
		ImagePlus imagem = IJ.getImage();
		
		if (imagem.getType() == ImagePlus.GRAY8) {
			IG(imagem);	
		}
		else {
			IJ.error("Imagem não é 8 Bits");
		}
		
	}
	
	public void IG(ImagePlus imagem) {

		ImagePlus imagemCopia = imagem.duplicate();
		imagemCopia.show();
		
		String[] estrategia = {"Expansão", "Equalização"};
		GenericDialog interfaceGrafica = new GenericDialog("Histograma");
		interfaceGrafica.addDialogListener(this);
		interfaceGrafica.addRadioButtonGroup("Selecione um metodo: ", estrategia, 1, 2, estrategia[0]);
		
		interfaceGrafica.showDialog();		
		
		if (interfaceGrafica.wasCanceled()) {
			IJ.showMessage("PlugIn cancelado!");
		}
		else {
			if (interfaceGrafica.wasOKed()) {
				if(interfaceGrafica.getNextRadioButton() == estrategia[0]) {
					expansao();
				}else {
					equalizacao();	
				}
		        IJ.showMessage("Plugin encerrado com sucesso!");
			}
		}
	}
	
	@Override
	public boolean dialogItemChanged(GenericDialog interfaceGrafica, AWTEvent e) {
		if (interfaceGrafica.wasCanceled()) return false;
        return true;
	}
	
	public void expansao() {
		int idImagens[] = WindowManager.getIDList();
		ImagePlus imagem = WindowManager.getImage(idImagens[1]);
		ImageProcessor processador = imagem.getProcessor();
		
		int larguraImagem = imagem.getWidth(), alturaImagem = imagem.getHeight();
		int vetorTons[] = new int[256], valorPixel, pixelHigh=255, pixelLow=0;
		
		
		//zerar vetor
		for(int i = 0; i<256; i++){
			vetorTons[i] = 0;
		}
		
		//contar numero de pixels para cada tom 
		for (int x = 0; x < larguraImagem; x++) {	
			for (int y = 0; y < alturaImagem; y++) {
				valorPixel = processador.getPixel(x, y);
				vetorTons[valorPixel] ++;
			}
		}
		
		//encontrar valor low
		for(int i = 0; i<256; i++){
			if(vetorTons[i]>0)
			{
				pixelLow = i;
				i=256;	
			}
		}
		
		//encontrar valor high
				for(int i = 255; i>-1; i--){
					if(vetorTons[i]>0)
					{
						pixelHigh = i;
						i=-1;
					}
				}
		
		//calcular valor pixel
		for (int x = 0; x < larguraImagem; x++) {	
			for (int y = 0; y < alturaImagem; y++) {	
			
				valorPixel = processador.getPixel(x, y);
				valorPixel = (255*(valorPixel - pixelLow))/(pixelHigh-pixelLow);
				processador.putPixel(x, y, valorPixel);
			}
		}
		
		//set
		imagem.setProcessor(processador);
		imagem.show();
	}
	
	
	public void equalizacao(){
		int idImagens[] = WindowManager.getIDList();
		ImagePlus imagem = WindowManager.getImage(idImagens[1]);
		ImageProcessor processador = imagem.getProcessor();
		
		int larguraImagem = imagem.getWidth(), alturaImagem = imagem.getHeight(), vetorTons[] = new int[256], 
				vetorSK[] = new int[256], valorPixel, areaImagem = larguraImagem * alturaImagem;
		
		Double vetorP[] = new Double[256], vetorPA[] = new Double[256];
		
		//zerar vetores
		for(int i = 0; i<256; i++){
			vetorTons[i] = 0;
			vetorPA[i] = 0.0;
		}
		
		//contar numero de pixels para cada tom 
		for (int x = 0; x < larguraImagem; x++) {	
			for (int y = 0; y < alturaImagem; y++) {
				valorPixel = processador.getPixel(x, y);
				vetorTons[valorPixel] ++;
			}
		}
		
		//calcular pr(rk)
		for(int i = 0; i<256; i++){
			vetorP[i] = vetorTons[i] * 1.0 / areaImagem; 
		}
		
		//Calcular Sk
		for (int x = 0; x < 256; x++) {	
			for (int y = 0; y < x; y++) {
				vetorPA[x] = vetorPA[x] + vetorP[y];
			}
			
			//arredondar Sk
			vetorPA[x] = vetorPA[x] * 255;
			vetorSK[x] = (int) (vetorPA[x] -(vetorPA[x]%1));
			
			if(vetorPA[x]%1 > 0)
			{
				vetorSK[x]++;
			}			
		}
		
		//Set image
		for (int x = 0; x < larguraImagem; x++) {	
			for (int y = 0; y < alturaImagem; y++) {
				valorPixel = processador.getPixel(x, y);
				
				processador.putPixel(x, y, vetorSK[valorPixel]);
			}
		}
		
		imagem.setProcessor(processador);
		imagem.show();
	}
}