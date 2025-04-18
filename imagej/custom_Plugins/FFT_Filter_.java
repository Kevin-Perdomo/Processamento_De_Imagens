import ij.*;
import ij.plugin.PlugIn;
import ij.process.*;
import ij.gui.*;
import java.awt.*;

public class FFT_Filter_ implements PlugIn {

    @Override
    public void run(String arg) {
        try {
            // Obtem imagem atual
            ImagePlus imp = IJ.getImage();
            if (imp == null || imp.getType() != ImagePlus.GRAY8) {
                IJ.error("Por favor, abra uma imagem de 8 bits.");
                return;
            }

            // Pega entrada do usuário
            GenericDialog gd = new GenericDialog("Filtro FFT");
            gd.addChoice("Tipo de filtro:", new String[]{"Passa-alta", "Passa-baixa"}, "Passa-baixa");
            gd.addNumericField("Raio do filtro:", 30, 0);
            gd.showDialog();
            if (gd.wasCanceled()) return;

            String tipoFiltro = gd.getNextChoice();
            int raio = (int) gd.getNextNumber();

            // Etapa 1: Aplica FFT
            IJ.log("1. Aplicando FFT...");
            IJ.run(imp, "FFT", "");
            ImagePlus fft = IJ.getImage();
            fft.setTitle("1 - FFT");
            fft.show();

            // Etapa 2: Seleciona e preenche centro com base no tipo de filtro
            IJ.log("2. Aplicando " + tipoFiltro + " com raio " + raio + "...");
            int w = fft.getWidth();
            int h = fft.getHeight();
            int cx = w / 2;
            int cy = h / 2;

            // Desenha círculo no centro
            IJ.run("Select None");
            IJ.makeOval(cx - raio, cy - raio, 2 * raio, 2 * raio);

            if (tipoFiltro.equals("Passa-baixa")) {
                // Preenche fora do círculo
                IJ.run(fft, "Make Inverse", "");
            }

            // Preenche pixels selecionados com preto
            IJ.setForegroundColor(0, 0, 0);
            IJ.run(fft, "Fill", "");
            IJ.run("Select None");
            fft.updateAndDraw();

            // Etapa 3: Aplica FFT inversa
            IJ.log("3. Aplicando FFT inversa...");
            IJ.run(fft, "Inverse FFT", "");
            ImagePlus inversa = IJ.getImage();
            inversa.setTitle("3 - Inversa");
            inversa.show();

            IJ.log("Filtro FFT finalizado com sucesso.");

        } catch (Exception e) {
            IJ.log("Erro durante o filtro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
