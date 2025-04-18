import ij.*;
import ij.plugin.PlugIn;
import ij.process.*;
import ij.gui.*;
import ij.WindowManager;

public class FFT_Filter_ implements PlugIn {

    @Override
    public void run(String arg) {
        try {
            // Obter a imagem original
            ImagePlus imp = IJ.getImage();
            if (imp == null || imp.getType() != ImagePlus.GRAY8) {
                IJ.error("Por favor, abra uma imagem de 8 bits.");
                return;
            }

            // Interface de usuário
            GenericDialog gd = new GenericDialog("Filtro FFT");
            gd.addChoice("Tipo de filtro:", new String[]{"Passa-alta", "Passa-baixa"}, "Passa-baixa");
            gd.addNumericField("Raio do filtro:", 30, 0);
            gd.showDialog();
            if (gd.wasCanceled()) return;

            String tipoFiltro = gd.getNextChoice();
            int raio = (int) gd.getNextNumber();

            // Etapa 1 - Aplicar FFT
            IJ.log("1. Aplicando Fast Fourier Transform...");
            IJ.run(imp, "FFT", "");
            ImagePlus fftImage = IJ.getImage(); // FFT com 2 canais
            fftImage.setTitle("3 - FFT com filtro aplicado");
            fftImage.show(); // Exibe a imagem pós-FFT
            IJ.log("Imagem após Fast Fourier Transform exibida.");

            // Duplicar a imagem após a FFT para garantir que a instância original não seja perdida
            ImagePlus fftImageDup = fftImage.duplicate();
            fftImageDup.setTitle("2 - Imagem Pós-FFT (sem filtro)");

            // Etapa 2 - Aplicar filtro diretamente na imagem FFT duplicada
            IJ.log("2. Aplicando " + tipoFiltro + " com raio " + raio + "...");
            WindowManager.setCurrentWindow(fftImageDup.getWindow());

            // Exibe a imagem pós-FFT duplicada (sem o filtro ainda aplicado)
            fftImageDup.updateAndDraw();  // Atualiza a imagem para garantir que ela esteja visível

            // Fazendo as modificações do filtro na imagem FFT duplicada
            int w = fftImageDup.getWidth();
            int h = fftImageDup.getHeight();
            int cx = w / 2;
            int cy = h / 2;

            IJ.run("Select None");
            IJ.makeOval(cx - raio, cy - raio, 2 * raio, 2 * raio);

            if (tipoFiltro.equals("Passa-baixa")) {
                IJ.run("Make Inverse", "");
            }

            IJ.setForegroundColor(0, 0, 0);
            IJ.run("Fill", "");
            IJ.run("Select None");

            // Duplicando a imagem de FFT para manipulação sem perder a referência original
            ImagePlus filteredFFT = fftImageDup.duplicate();
            filteredFFT.setTitle("1 - Imagem Pós-FFT");

            // Etapa 3 - Mostrar imagem com filtro aplicado
            IJ.log("3. Imagem com filtro aplicado exibida.");
            filteredFFT.show(); // Exibe a imagem com o filtro aplicado

            // Etapa 4 - FFT Inversa
            IJ.log("4. Aplicando Inverse Fast Fourier Transform...");
            ImagePlus fftImageToInverse = WindowManager.getImage(fftImage.getID()); // Usando ID para pegar a imagem original
            WindowManager.setCurrentWindow(fftImageToInverse.getWindow());
            IJ.run("Inverse FFT", "");

            ImagePlus reconstruida = IJ.getImage();
            reconstruida.setTitle("4 - Imagem reconstruída");
            reconstruida.show();

            // Final
            IJ.log("Filtro FFT finalizado com sucesso.");

        } catch (Exception e) {
            IJ.log("Erro durante o filtro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
