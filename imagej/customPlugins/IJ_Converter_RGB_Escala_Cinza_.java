import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;

public class IJ_Converter_RGB_Escala_Cinza_ implements PlugIn, DialogListener {
	public void run(String arg) {
		apresentarInterfaceGrafica();
	}
	
	public void apresentarInterfaceGrafica() {
		GenericDialog interfaceGrafica = new GenericDialog("Conversão RGB para Escala Cinza");
		interfaceGrafica.addDialogListener(this);
		
		String[] estrategia = {"Media Aritmetica", "Luminance Analogica","Luminance Digital"}; 
		
		interfaceGrafica.addMessage("Escolha a Imagem a ser convertida para escala de cinza");
		interfaceGrafica.addImageChoice("Imagem: ","");
		interfaceGrafica.addRadioButtonGroup("Escolha o metodo de conversão", estrategia, 1, 3, estrategia[0]);
		interfaceGrafica.addCheckbox("Criar nova imagem", true);
		interfaceGrafica.showDialog();
		
		if (interfaceGrafica.wasCanceled()) {
			IJ.showMessage("PlugIn cancelado!");
		}else {
			if (interfaceGrafica.wasOKed()) {
				ImagePlus imagem = interfaceGrafica.getNextImage();
				boolean criarNovaImagem = interfaceGrafica.getNextBoolean();
				
				if( imagem.getType() != ImagePlus.COLOR_RGB)	{
					IJ.showMessage("Imagem Selecionada não é RGB!");
				}
				else {
					String metodoConversaoSelecionado = interfaceGrafica.getNextRadioButton();
					
					if(metodoConversaoSelecionado == estrategia[0]) {
						
						Media_Aritmetica(imagem, criarNovaImagem);
						
					}else {
						if(metodoConversaoSelecionado == estrategia[1]) {
							
							Luminance_Analogico(imagem, criarNovaImagem);
							
						}else {
							if(metodoConversaoSelecionado == estrategia[2]) { 
							
								Luminance_Digital(imagem, criarNovaImagem);
							}
														
						}
					}
				}
			}
		};
	}
	

	@Override
	public boolean dialogItemChanged(GenericDialog interfaceGrafica, AWTEvent e) {
		
		if (interfaceGrafica.wasCanceled()) return false;
        
		return true;
        
    }
	
	public void Media_Aritmetica(ImagePlus imagem, boolean criarNovaImagem)
	{
		
		ImageProcessor processador = imagem.getProcessor();
		int largura_imagem = imagem.getWidth(), altura_imagem = processador.getHeight();
		
		if(criarNovaImagem == true) {
			
			ImagePlus image = IJ.createImage("Escala_Cinza", "8-Bit", largura_imagem, altura_imagem, 1);
			
			ImageProcessor novaImagem = image.getProcessor();
			
			
			//Criando variavel para representar pixel 8-Bits
			int valorPixel[] = {0,0,0}, novoValorPixel;	
			
			for (int x = 0; x < largura_imagem; x++) {	
				for (int y = 0; y < altura_imagem; y++) {
					
					valorPixel = processador.getPixel(x, y, valorPixel);
					
					//Media
					novoValorPixel = ((valorPixel[0]+valorPixel[1]+valorPixel[2])/3);
					novaImagem.putPixel(x, y, novoValorPixel);			
					
				}
				
				//Set imagemPlus
				image.setProcessor(novaImagem);
				image.show();
				
			}	
				
		}else
		{	
			
			//Criando variavel para representar pixel 8-Bits
			int valorPixel[] = {0,0,0}, novoValorPixel[]= {0,0,0};	
			
			for (int x = 0; x < largura_imagem; x++) {	
				for (int y = 0; y < altura_imagem; y++) {
					
					valorPixel = processador.getPixel(x, y, valorPixel);
					
					//Media
					novoValorPixel[0] = ((valorPixel[0]+valorPixel[1]+valorPixel[2])/3);
					novoValorPixel[1] = novoValorPixel[0];
					novoValorPixel[2] = novoValorPixel[0];
					processador.putPixel(x, y, novoValorPixel);			
					
				}
				
				//Set imagemPlus
				imagem.setProcessor(processador);
				imagem.show();
				
			}	
		}	
	}
	
	public void Luminance_Analogico(ImagePlus imagem, boolean criarNovaImagem)
	{
		ImageProcessor processador = imagem.getProcessor();
		int largura_imagem = imagem.getWidth(), altura_imagem = processador.getHeight();
		
		if(criarNovaImagem == true) {
			
			ImagePlus image = IJ.createImage("Escala_Cinza", "8-Bit", largura_imagem, altura_imagem, 1);
			
			ImageProcessor novaImagem = image.getProcessor();
			
			
			//Criando variavel para representar pixel 8-Bits
			int valorPixel[] = {0,0,0}, novoValorPixel;	
			
			for (int x = 0; x < largura_imagem; x++) {	
				for (int y = 0; y < altura_imagem; y++) {
					
					valorPixel = processador.getPixel(x, y, valorPixel);
					
					//Media
					novoValorPixel = (int)((0.299*valorPixel[0])+(0.587*valorPixel[1])+(0.114*valorPixel[2]));
					novaImagem.putPixel(x, y, novoValorPixel);			
					
				}
				
				//Set imagemPlus
				image.setProcessor(novaImagem);
				image.show();
				
			}	
				
		}else
		{	
			
			//Criando variavel para representar pixel 8-Bits
			int valorPixel[] = {0,0,0}, novoValorPixel[]= {0,0,0};	
			
			for (int x = 0; x < largura_imagem; x++) {	
				for (int y = 0; y < altura_imagem; y++) {
					
					valorPixel = processador.getPixel(x, y, valorPixel);
					
					//Media
					novoValorPixel[0] = (int)((0.299*valorPixel[0])+(0.587*valorPixel[1])+(0.114*valorPixel[2]));
					novoValorPixel[1] = novoValorPixel[0];
					novoValorPixel[2] = novoValorPixel[0];
					processador.putPixel(x, y, novoValorPixel);			
					
				}
				
				//Set imagemPlus
				imagem.setProcessor(processador);
				imagem.show();
				
			}	
		}	
	}
	
	public void Luminance_Digital(ImagePlus imagem, boolean criarNovaImagem)
	{
		
		ImageProcessor processador = imagem.getProcessor();
		int largura_imagem = imagem.getWidth(), altura_imagem = processador.getHeight();
		
		if(criarNovaImagem == true) {
			
			ImagePlus image = IJ.createImage("Escala_Cinza", "8-Bit", largura_imagem, altura_imagem, 1);
			
			ImageProcessor novaImagem = image.getProcessor();
			
			
			//Criando variavel para representar pixel 8-Bits
			int valorPixel[] = {0,0,0}, novoValorPixel;	
			
			for (int x = 0; x < largura_imagem; x++) {	
				for (int y = 0; y < altura_imagem; y++) {
					
					valorPixel = processador.getPixel(x, y, valorPixel);
					
					//Media
					novoValorPixel = (int)((0.2125*valorPixel[0])+(0.7154*valorPixel[1])+(0.072*valorPixel[2]));
					novaImagem.putPixel(x, y, novoValorPixel);			
					
				}
				
				//Set imagemPlus
				image.setProcessor(novaImagem);
				image.show();
		
			}	
				
		}else {			
			//Criando variavel para representar pixel 8-Bits
			int valorPixel[] = {0,0,0}, novoValorPixel[]= {0,0,0};	
			
			for (int x = 0; x < largura_imagem; x++) {	
				for (int y = 0; y < altura_imagem; y++) {
					
					valorPixel = processador.getPixel(x, y, valorPixel);
					
					//Media
					novoValorPixel[0] = (int)((0.2125*valorPixel[0])+(0.7154*valorPixel[1])+(0.072*valorPixel[2]));
					novoValorPixel[1] = novoValorPixel[0];
					novoValorPixel[2] = novoValorPixel[0];
					processador.putPixel(x, y, novoValorPixel);			
					
				}
				
				//Set imagemPlus
				imagem.setProcessor(processador);
				imagem.show();
				
			}	
		}						
	}
}