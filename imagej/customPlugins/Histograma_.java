import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.gui.Plot;

// https://pt.wikipedia.org/wiki/Equaliza%C3%A7%C3%A3o_de_histograma

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
        
        // Mostrar histograma da imagem original
        mostrarHistograma(imagem, "Histograma Original");

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
                    expansao(imagemCopia);
                }else {
                    equalizacao(imagemCopia);    
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
    
    public void expansao(ImagePlus imagem) {
        int idImagens[] = WindowManager.getIDList();
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
        
        // Mostrar histograma da imagem modificada
        mostrarHistograma(imagem, "Histograma Modificado");
    }
    
    
    public void equalizacao(ImagePlus imagem){
        int idImagens[] = WindowManager.getIDList();
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
        
        //calcular Pr(Distribuição de Probabilidade dos níveis de cinza)
        for(int i = 0; i<256; i++){
            vetorP[i] = vetorTons[i] * 1.0 / areaImagem; 
        }
        
        //calcular Sk(Função de distribuição acumulada - CDF)
        vetorPA[0] = vetorP[0] * 255;
        vetorSK[0] = (int) (vetorPA[0] - (vetorPA[0] % 1)); // Truncamento
        for (int x = 1; x < 256; x++) {

            vetorPA[x] = vetorPA[x-1] + (vetorP[x] * 255);// 256-1
            vetorSK[x] = (int) (vetorPA[x] - (vetorPA[x] % 1));// Truncamento
         
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
        
        // Mostrar histograma da imagem modificada
        mostrarHistograma(imagem, "Histograma Modificado");
    }

    private void mostrarHistograma(ImagePlus imagem, String titulo) {
        ImageStatistics stats = imagem.getStatistics();
        int[] histogram = stats.histogram;
        
        // Criar um gráfico de histograma
        double[] xValues = new double[histogram.length];
        double[] yValues = new double[histogram.length];
        for (int i = 0; i < histogram.length; i++) {
            xValues[i] = i;
            yValues[i] = histogram[i];
        }
        
        Plot plot = new Plot(titulo, "Intensidade", "Frequência", xValues, yValues);
        plot.show();
    }
}