import ij.*;
import ij.plugin.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class Filtros_Nao_Lineares_ implements PlugIn {
    private String filtroSelecionado = "Sobel";

    @Override
    public void run(String arg) {
        ImagePlus imp = IJ.getImage();
        if (imp == null) {
            IJ.showMessage("Erro", "Nenhuma imagem aberta.");
            return;
        }
        
        if (imp.getType() != ImagePlus.GRAY8) {
            IJ.showMessage("Erro", "A imagem precisa estar em tons de cinza (8 bits).");
            return;
        }

        GenericDialog gd = new GenericDialog("Filtros");
        gd.addRadioButtonGroup("Escolha o filtro:", new String[]{"Sobel", "Mediana"}, 2, 1, filtroSelecionado);
        gd.showDialog();
        if (gd.wasCanceled()) return;
        
        filtroSelecionado = gd.getNextRadioButton();
        ImageProcessor ip = imp.getProcessor().duplicate();
        
        if (filtroSelecionado.equals("Sobel")) {
            aplicarSobel(ip);
        } else {
            aplicarMediana(ip);
        }
    }

    private void aplicarSobel(ImageProcessor ip) {
        int[][] kernelVertical = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] kernelHorizontal = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};
        
        ImageProcessor gx = ip.duplicate();
        ImageProcessor gy = ip.duplicate();
        aplicarConvolucao(gx, kernelVertical);
        aplicarConvolucao(gy, kernelHorizontal);
        
        ImageProcessor g = ip.duplicate();
        int width = ip.getWidth();
        int height = ip.getHeight();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int sobelX = gx.getPixel(x, y);
                int sobelY = gy.getPixel(x, y);
                int resultado = (int) Math.sqrt(sobelX * sobelX + sobelY * sobelY);
                g.putPixel(x, y, resultado);
            }
        }
        
        new ImagePlus("Sobel - Vertical", gx).show();
        new ImagePlus("Sobel - Horizontal", gy).show();
        new ImagePlus("Sobel - Resultado", g).show();
    }

    private void aplicarConvolucao(ImageProcessor ip, int[][] kernel) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        ImageProcessor temp = ip.duplicate();
        
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int soma = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        soma += temp.getPixel(x + i, y + j) * kernel[i + 1][j + 1];
                    }
                }
                ip.putPixel(x, y, soma);
            }
        }
    }

    private void aplicarMediana(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        ImageProcessor temp = ip.duplicate();
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int[] vizinhanca = new int[9];
                int index = 0;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        vizinhanca[index++] = temp.getPixel(x + i, y + j);
                    }
                }
                Arrays.sort(vizinhanca);
                ip.putPixel(x, y, vizinhanca[4]);
            }
        }
        new ImagePlus("Filtro de Mediana", ip).show();
    }
}