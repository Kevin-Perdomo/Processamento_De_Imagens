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
        // Diálogo para selecionar o filtro
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

        // Obtém a imagem atual
        ImagePlus imp = IJ.getImage();
        if (imp == null) {
            IJ.showMessage("Erro", "Nenhuma imagem aberta.");
            return;
        }

        // Verifica se a imagem está em tons de cinza
        if (imp.getType() != ImagePlus.GRAY8) {
            IJ.showMessage("Erro", "A imagem precisa estar em tons de cinza (8 bits).");
            return;
        }

        // Processa a imagem
        ImageProcessor ip = imp.getProcessor();
        ImageProcessor ipCopy = ip.duplicate(); // Duplicando a imagem original para não modificar a original

        // Aplica o filtro escolhido
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

        // Exibe a imagem original
        imp.show();
    }

    private void aplicarPassaBaixaMedia(ImageProcessor ip) {
        int[][] kernel = {
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        };

        // Aplica a convolução com divisor fixo de 9 (média)
        aplicarConvolucao(ip, kernel, 9);
    }

    private void aplicarPassaAlta(ImageProcessor ip) {
        int[][] kernel = {
            {1, -2, 1},
            {-2, 5, -2}, 
            {1, -2, 1} 
        };

        // Aplica a convolução com divisor fixo de 1
        aplicarConvolucao(ip, kernel, 1); 
    }

    private void aplicarDeteccaoBordas(ImageProcessor ip) {
        int[][] kernel = {
                {1, 0, -1}, 
                {1, 0, -1}, 
                {1, 0, -1} 
        };

        // Aplica a convolução com divisor fixo de 1
        aplicarConvolucao(ip, kernel, 1);
    }

    private void aplicarConvolucao(ImageProcessor ip, int[][] kernel, int divisor) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        ImageProcessor ipCopy = ip.duplicate();
        
        // Aplica a convolução, ignorando as bordas da imagem
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int soma = 0;
                // Aplica o kernel 3x3
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        soma += ipCopy.getPixel(x + i, y + j) * kernel[i + 1][j + 1];
                    }
                }
                ip.putPixel(x, y, soma / divisor); 
            }
        }
    }

    private void mostrarImagemFiltrada(ImageProcessor ip, String nomeImagem) {
        // Cria uma nova imagem para mostrar a imagem filtrada
        ImagePlus impFiltered = new ImagePlus(nomeImagem, ip);
        impFiltered.show();
    }
}
