// Importação das classes necessárias da biblioteca ImageJ

import ij.plugin.PlugIn;            // Interface para criar plugins no ImageJ
import ij.ImagePlus;                // Representa uma imagem no ImageJ
import ij.IJ;                       // Utilitário para interagir com o ImageJ
import ij.process.ImageProcessor;   // Classe para manipular pixels da imagem

// Classe que implementa o plugin para converter imagem RGB em três imagens em escala de cinza (uma para cada canal)
public class Converter_RGB_to_8bits_ implements PlugIn {

    // Método principal do plugin, chamado quando o plugin é executado
    public void run(String arg) {
        // Obtém a imagem atualmente aberta no ImageJ
        ImagePlus imagem = IJ.getImage();
        
        // Verifica se a imagem é do tipo RGB (colorida)
        if (imagem.getType() == ImagePlus.COLOR_RGB) {
            gerar_imagens(imagem); // Se for RGB, chama o método para gerar as imagens em escala de cinza
        } else {
            IJ.error("Imagem não é Colorida (RGB)"); // Exibe um erro se a imagem não for RGB
        }
    }   
    
    // Método para gerar imagens em escala de cinza a partir dos canais R, G e B
    public void gerar_imagens(ImagePlus imagem) {
        // Obtém o processador da imagem para manipular os pixels
        ImageProcessor processador = imagem.getProcessor();
        
        // Obtém a largura e a altura da imagem original
        int largura_imagem = imagem.getWidth();
        int altura_imagem = imagem.getHeight();
        
        // Cria três novas imagens em escala de cinza (8-bit) para cada canal: vermelho, verde e azul
        ImagePlus redImage = IJ.createImage("Red_Cinza", "8-bit", largura_imagem, altura_imagem, 1);
        ImagePlus greenImage = IJ.createImage("Green_Cinza", "8-bit", largura_imagem, altura_imagem, 1);
        ImagePlus blueImage = IJ.createImage("Blue_Cinza", "8-bit", largura_imagem, altura_imagem, 1);    
        
        // Obtém os processadores das novas imagens para manipular os pixels
        ImageProcessor novaImagemRed = redImage.getProcessor();
        ImageProcessor novaImagemGreen = greenImage.getProcessor();
        ImageProcessor novaImagemBlue = blueImage.getProcessor();
        
        // Criação de um array para armazenar o valor RGB de cada pixel e uma variável para o valor em escala de cinza
        int valorPixel[] = {0, 0, 0};
        int novoValorPixel;
        
        // Loop para percorrer cada pixel da imagem original
        for (int x = 0; x < largura_imagem; x++) {    
            for (int y = 0; y < altura_imagem; y++) {
                
                // Obtém os valores RGB do pixel na posição (x, y)
                valorPixel = processador.getPixel(x, y, valorPixel);
                
                // Canal Vermelho: usa o valor do componente vermelho e salva na imagem correspondente
                novoValorPixel = valorPixel[0];
                novaImagemRed.putPixel(x, y, novoValorPixel);
                
                // Canal Verde: usa o valor do componente verde e salva na imagem correspondente
                novoValorPixel = valorPixel[1];
                novaImagemGreen.putPixel(x, y, novoValorPixel);
                
                // Canal Azul: usa o valor do componente azul e salva na imagem correspondente
                novoValorPixel = valorPixel[2];
                novaImagemBlue.putPixel(x, y, novoValorPixel);
            }
        }
        
        // Define os processadores das imagens para exibição
        redImage.setProcessor(novaImagemRed);
        greenImage.setProcessor(novaImagemGreen);
        blueImage.setProcessor(novaImagemBlue);
        
        // Exibe as três imagens em escala de cinza
        redImage.show();
        greenImage.show();
        blueImage.show();
    }    
}
