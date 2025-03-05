import ij.*;
import ij.plugin.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.awt.event.*;

public class Filtros_Lineares_ implements PlugIn {

    private String filtroSelecionado = "Passa-Baixa (Média)";

    @Override
    public void run(String arg) {
        GenericDialog gd = new GenericDialog("Filtros");
        gd.addRadioButtonGroup("Escolha o filtro:", new String[]{
                "Passa-Baixa (Média)",
                "Passa-Alta",
                "Detecção de Bordas"
        }, 3, 1, filtroSelecionado);

        gd.addDialogListener(new DialogListener() {
            public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
                filtroSelecionado = gd.getNextRadioButton();
                return true;
            }
        });

        gd.showDialog();
        if (gd.wasCanceled()) {
            return;
        }

        ImagePlus imp = IJ.getImage();
        if (imp == null) {
            IJ.showMessage("Erro", "Nenhuma imagem aberta.");
            return;
        }

        // Converter RGB para 8 bits
        if (imp.getType() == ImagePlus.COLOR_RGB) {
            IJ.run(imp, "8-bit", "");
        }

        ImageProcessor ip = imp.getProcessor();
        ImageProcessor ipCopy = ip.duplicate(); // Duplicando a imagem original

        // Aplica o filtro selecionado
        if (filtroSelecionado.equals("Passa-Baixa (Média)")) {
            aplicarPassaBaixaMedia(ipCopy);
            mostrarImagemFiltrada(ipCopy, "Imagem Passa-Baixa");
        } else if (filtroSelecionado.equals("Passa-Alta")) {
            aplicarPassaAlta(ipCopy);
            mostrarImagemFiltrada(ipCopy, "Imagem Passa-Alta");
        } else if (filtroSelecionado.equals("Detecção de Bordas")) {
            aplicarDeteccaoBordas(ipCopy);
            mostrarImagemFiltrada(ipCopy, "Imagem Detecção de Bordas");
        }

        // Mostrar a imagem original
        imp.show();
    }

    private void aplicarPassaBaixaMedia(ImageProcessor ip) {
        int[][] kernel = {
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        };

        aplicarConvolucao(ip, kernel);  // Média, divisor calculado automaticamente
    }

    private void aplicarPassaAlta(ImageProcessor ip) {
        int[][] kernel = {
            {1, -2, 1},
            {-2, 5, -2}, 
            {1, -2, 1} 
        };

        aplicarConvolucao(ip, kernel);  // Passa-alta, divisor calculado automaticamente
    }

    private void aplicarDeteccaoBordas(ImageProcessor ip) {
        int[][] kernel = {
                {1, 0, -1}, 
                {1, 0, -1}, 
                {1, 0, -1} 
        };

        aplicarConvolucao(ip, kernel);  // Detecção de bordas, divisor calculado automaticamente
    }

    private void aplicarConvolucao(ImageProcessor ip, int[][] kernel) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        ImageProcessor ipCopy = ip.duplicate();
        
        // Cálculo do divisor (somatório dos elementos do kernel)
        int divisor = 0;
        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel[i].length; j++) {
                divisor += kernel[i][j];
            }
        }
        
        // Previne divisão por zero (caso o kernel tenha todos os valores igual a 0)
        if (divisor == 0) divisor = 1;

        // Convolução: começamos de 1 e vamos até -1 para evitar as bordas
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int soma = 0;
                // Aplicando o kernel 3x3
                for (int kx = -1; kx <= 1; kx++) {
                    for (int ky = -1; ky <= 1; ky++) {
                        soma += ipCopy.getPixel(x + kx, y + ky) * kernel[kx + 1][ky + 1];
                    }
                }
                ip.putPixel(x, y, soma / divisor);
            }
        }
    }

    private void mostrarImagemFiltrada(ImageProcessor ip, String nomeImagem) {
        // Criar uma nova imagem para mostrar a imagem filtrada com o nome apropriado
        ImagePlus impFiltered = new ImagePlus(nomeImagem, ip);
        impFiltered.show();
    }
}