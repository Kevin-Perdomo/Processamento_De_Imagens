// Importação das bibliotecas necessárias do ImageJ

import ij.plugin.PlugIn;            // Interface para criar plugins no ImageJ
import ij.ImagePlus;                // Representa uma imagem no ImageJ
import ij.WindowManager;            // Gerencia as janelas de imagem no ImageJ
import ij.IJ;                       // Utilitário para interagir com o ImageJ
import ij.process.ImageProcessor;   // Classe para manipular pixels da imagem

// Classe principal do plugin, que implementa a interface PlugIn do ImageJ
public class Converter_8bits_to_RGB_ implements PlugIn {
    
    // Método principal que é chamado quando o plugin é executado
    public void run(String arg) {
        
        // Verifica se há exatamente 3 imagens abertas no ImageJ
        if(WindowManager.getWindowCount() == 3) {
            
            // Obtém os IDs das imagens abertas
            int idImagens[] = WindowManager.getIDList();
            
            // Recupera as três imagens abertas (assumindo que sejam R, G, B)
            ImagePlus imagemRed = WindowManager.getImage(idImagens[0]);
            ImagePlus imagemGreen = WindowManager.getImage(idImagens[1]);
            ImagePlus imagemBlue = WindowManager.getImage(idImagens[2]);
            
            // Verifica se todas as imagens são em escala de cinza (8 bits)
            if(imagemRed.getType() == ImagePlus.GRAY8 && 
               imagemGreen.getType() == ImagePlus.GRAY8 && 
               imagemBlue.getType() == ImagePlus.GRAY8) {
                
                // Chama o método para gerar a imagem RGB combinando os canais
                gerar_imagem(imagemRed, imagemGreen, imagemBlue);
                
            } else {
                // Exibe um erro caso as imagens não sejam 8 bits
                IJ.error("Imagens não são 8-Bits");
            }
        } else {
            // Exibe um erro caso não haja exatamente 3 imagens abertas
            IJ.error("Deve haver 3 imagens abertas!");
        }
    }   
    
    // Método responsável por gerar a imagem RGB combinando os canais
    public void gerar_imagem(ImagePlus imagemRed, ImagePlus imagemGreen, ImagePlus imagemBlue) {
        
        // Obtém os processadores de imagem para acessar os pixels de cada canal
        ImageProcessor processadorRed = imagemRed.getProcessor();
        ImageProcessor processadorGreen = imagemGreen.getProcessor();
        ImageProcessor processadorBlue = imagemBlue.getProcessor();
        
        // Obtém as dimensões da imagem (assumindo que todas têm o mesmo tamanho)
        int largura_imagem = imagemRed.getWidth(), altura_imagem = imagemRed.getHeight();
        
        // Cria uma nova imagem RGB com as mesmas dimensões
        ImagePlus novaImagem = IJ.createImage("RGB", "RGB", largura_imagem, altura_imagem, 1);
        ImageProcessor novoProcessador = novaImagem.getProcessor();
        
        // Arrays para armazenar os valores dos pixels
        int valorPixel[] = {0};           // Para ler o valor de um pixel de um canal
        int novoValorPixel[] = {0, 0, 0}; // Para armazenar os valores R, G e B
        
        // Percorre cada pixel da imagem
        for (int x = 0; x < largura_imagem; x++) {    
            for (int y = 0; y < altura_imagem; y++) {
                
                // Lê o valor do pixel do canal vermelho e armazena na posição 0 (R)
                valorPixel = processadorRed.getPixel(x, y, valorPixel);
                novoValorPixel[0] = valorPixel[0];
                
                // Lê o valor do pixel do canal verde e armazena na posição 1 (G)
                valorPixel = processadorGreen.getPixel(x, y, valorPixel);
                novoValorPixel[1] = valorPixel[0];
                
                // Lê o valor do pixel do canal azul e armazena na posição 2 (B)
                valorPixel = processadorBlue.getPixel(x, y, valorPixel);
                novoValorPixel[2] = valorPixel[0];
                
                // Define o pixel na nova imagem RGB usando os valores R, G e B
                novoProcessador.putPixel(x, y, novoValorPixel);
            }
        }
        
        // Atualiza o processador da nova imagem com os dados dos pixels combinados
        novaImagem.setProcessor(novoProcessador);
        
        // Exibe a nova imagem RGB no ImageJ
        novaImagem.show();            
    }
}
