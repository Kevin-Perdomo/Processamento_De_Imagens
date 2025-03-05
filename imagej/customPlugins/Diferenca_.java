import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Diferenca_ implements PlugIn {
    public void run(String arg) {
        // Obtém a lista de imagens abertas
        int[] listaImagens = WindowManager.getIDList();
        if (listaImagens == null || listaImagens.length < 2) {
            IJ.showMessage("Erro", "É necessário abrir pelo menos duas imagens.");
            return;
        }

        // Obtém as duas imagens
        ImagePlus img1 = WindowManager.getImage(listaImagens[0]);
        ImagePlus img2 = WindowManager.getImage(listaImagens[1]);

        // Verifica se as imagens possuem as mesmas dimensões
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            IJ.showMessage("Erro", "As imagens devem ter o mesmo tamanho.");
            return;
        }

        // Chama o método para calcular a diferença
        calcularDiferenca(img1, img2);
    }

    private void calcularDiferenca(ImagePlus img1, ImagePlus img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();

        ImageProcessor proc1 = img1.getProcessor();
        ImageProcessor proc2 = img2.getProcessor();
        ByteProcessor diffProcessor = new ByteProcessor(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int g1 = proc1.getPixel(x, y);
                int g2 = proc2.getPixel(x, y);
                
                // Cálculo da diferença normalizada
                int diff = Math.abs(g1 - g2);
                diffProcessor.putPixel(x, y, diff);
            }
        }

        // Exibe a imagem de diferença
        new ImagePlus("Diferença de Imagens", diffProcessor).show();
    }
}
