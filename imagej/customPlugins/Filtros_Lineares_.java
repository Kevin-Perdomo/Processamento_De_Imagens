import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Filtros_Lineares_ implements PlugIn {
    
    private JRadioButton rbPassaBaixa, rbPassaAlta, rbBorda;
    private ButtonGroup grupoFiltros;
    private JFrame frame;

    public void run(String arg) {
        SwingUtilities.invokeLater(() -> criarInterface());
    }

    private void criarInterface() {
    frame = new JFrame("Filtros Lineares");
    frame.setSize(280, 200);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

    JLabel label = new JLabel("Escolha um filtro:");
    label.setAlignmentX(Component.LEFT_ALIGNMENT);
    frame.add(label);

    rbPassaBaixa = new JRadioButton("Filtro Passa-Baixa (Média)");
    rbPassaAlta = new JRadioButton("Filtro Passa-Alta");
    rbBorda = new JRadioButton("Filtro de Borda");

    grupoFiltros = new ButtonGroup();
    grupoFiltros.add(rbPassaBaixa);
    grupoFiltros.add(rbPassaAlta);
    grupoFiltros.add(rbBorda);

    frame.add(rbPassaBaixa);
    frame.add(rbPassaAlta);
    frame.add(rbBorda);

    JButton btnAplicar = new JButton("Aplicar Filtro");
    btnAplicar.setAlignmentX(Component.LEFT_ALIGNMENT);
    btnAplicar.addActionListener(e -> aplicarFiltro());
    frame.add(btnAplicar);

    JButton btnFechar = new JButton("      Fechar      ");
    btnFechar.setAlignmentX(Component.LEFT_ALIGNMENT);
    btnFechar.addActionListener(e -> frame.dispose());
    frame.add(btnFechar);

    frame.setVisible(true);

    }

    private void aplicarFiltro() {
        ImagePlus imagem = IJ.getImage();

        if (imagem == null) {
            IJ.error("Nenhuma imagem aberta!");
            return;
        }

        if (imagem.getType() != ImagePlus.GRAY8) {
            IJ.error("A imagem precisa estar em 8 bits (tons de cinza).");
            return;
        }

        if (rbPassaBaixa.isSelected()) {
            processarImagem(imagem, "PassaBaixaMedia");
        } else if (rbPassaAlta.isSelected()) {
            processarImagem(imagem, "PassaAlta");
        } else if (rbBorda.isSelected()) {
            processarImagem(imagem, "Borda");
        } else {
            IJ.error("Selecione um filtro antes de aplicar.");
        }
    }

    private void processarImagem(ImagePlus imagemOriginal, String filtro) {
        ImageProcessor processadorOriginal = imagemOriginal.getProcessor();
        ImagePlus imagemProcessada = imagemOriginal.duplicate();
        imagemProcessada.setTitle(filtro);
        ImageProcessor processadorNovo = imagemProcessada.getProcessor();

        int largura = imagemOriginal.getWidth();
        int altura = imagemOriginal.getHeight();

        for (int x = 1; x < largura - 1; x++) {
            for (int y = 1; y < altura - 1; y++) {
                if (filtro.equals("PassaBaixaMedia")) {
                    passaBaixaMedia(processadorOriginal, processadorNovo, x, y);
                } else if (filtro.equals("PassaAlta")) {
                    passaAlta(processadorOriginal, processadorNovo, x, y);
                } else if (filtro.equals("Borda")) {
                    borda(processadorOriginal, processadorNovo, x, y);
                }
            }
        }

        imagemProcessada.setProcessor(processadorNovo);
        imagemProcessada.show();
    }

    private void passaBaixaMedia(ImageProcessor processadorOriginal, ImageProcessor processadorNovo, int x, int y) {
        int soma = 0;

        for (int a = x - 1; a <= x + 1; a++) {
            for (int b = y - 1; b <= y + 1; b++) {
                soma += processadorOriginal.getPixel(a, b);
            }
        }

        int media = soma / 9;
        processadorNovo.putPixel(x, y, Math.min(Math.max(media, 0), 255));
    }

    private void passaAlta(ImageProcessor processadorOriginal, ImageProcessor processadorNovo, int x, int y) {
        int[][] mascara = { 
            {1, -2, 1},
            {-2, 5, -2}, 
            {1, -2, 1} 
        };

        aplicarMascara(processadorOriginal, processadorNovo, x, y, mascara);
    }

    private void borda(ImageProcessor processadorOriginal, ImageProcessor processadorNovo, int x, int y) {
        int[][] mascara = { 
            {1, 0, -1}, 
            {1, 0, -1}, 
            {1, 0, -1} 
        };

        aplicarMascara(processadorOriginal, processadorNovo, x, y, mascara);
    }

    private void aplicarMascara(ImageProcessor processadorOriginal, ImageProcessor processadorNovo, int x, int y, int[][] mascara) {
        int soma = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                soma += processadorOriginal.getPixel(x + i, y + j) * mascara[i + 1][j + 1];
            }
        }

        processadorNovo.putPixel(x, y, Math.min(Math.max(soma, 0), 255));
    }
}