import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Operacoes_Morfologicas_Binarias_ implements PlugIn {

    public void run(String arg) {
        ImagePlus imagem = WindowManager.getCurrentImage();
        if (imagem == null) {
            IJ.showMessage("Erro", "Nenhuma imagem aberta.");
            return;
        }

        GenericDialog gd = new GenericDialog("Operações Morfológicas");
        gd.addRadioButtonGroup("Escolha a operação:", 
            new String[]{"Dilatação", "Erosão", "Abertura", "Fechamento", "Borda"}, 
            5, 1, "Dilatação");
        gd.showDialog();

        if (gd.wasCanceled()) return;

        String operacao = gd.getNextRadioButton();
        ImageProcessor processor = imagem.getProcessor().convertToByteProcessor();

        switch (operacao) {
            case "Dilatação":
                processar(processor, "dilatacao");
                break;
            case "Erosão":
                processar(processor, "erosao");
                break;
            case "Abertura":
                processar(processor, "erosao"); // Aplica erosão primeiro
                processar(processor, "dilatacao"); // Depois a dilatação
                break;
            case "Fechamento":
                processar(processor, "dilatacao"); // Aplica dilatação primeiro
                processar(processor, "erosao"); // Depois a erosão
                break;
            case "Borda":
                ByteProcessor copia = (ByteProcessor) processor.duplicate();
                processar(copia, "erosao");
                subtrairImagens(processor, copia);
                break;
        }

        new ImagePlus("Resultado - " + operacao, processor).show();
    }

    private void processar(ImageProcessor img, String tipo) {
        int width = img.getWidth();
        int height = img.getHeight();
        ByteProcessor resultado = new ByteProcessor(width, height);
        ByteProcessor copia = (ByteProcessor) img.duplicate();

        int[][] elementoEstruturante = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int valor = aplicarOperacao(copia, x, y, elementoEstruturante, tipo);
                resultado.putPixel(x, y, valor);
            }
        }

        img.setPixels(resultado.getPixels());
    }

    private int aplicarOperacao(ByteProcessor img, int x, int y, int[][] se, String tipo) {
        switch (tipo) {
            case "dilatacao":
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (se[dy + 1][dx + 1] == 1 && img.getPixel(x + dx, y + dy) == 255) {
                            return 255;
                        }
                    }
                }
                return 0;

            case "erosao":
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (se[dy + 1][dx + 1] == 1 && img.getPixel(x + dx, y + dy) == 0) {
                            return 0;
                        }
                    }
                }
                return 255;
        }
        return img.getPixel(x, y);
    }

    private void subtrairImagens(ImageProcessor original, ImageProcessor erodida) {
        int width = original.getWidth();
        int height = original.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int corOriginal = original.getPixel(x, y);
                int corErodida = erodida.getPixel(x, y);
                original.putPixel(x, y, Math.max(0, corOriginal - corErodida));
            }
        }
    }
}