import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Roi_Extractor_ implements PlugIn {

    @Override
    public void run(String arg) {
        // Selecionar diretório de entrada
        JFileChooser inputChooser = new JFileChooser();
        inputChooser.setDialogTitle("Selecione o diretório de entrada");
        inputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (inputChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
        File inputDir = inputChooser.getSelectedFile();

        // Selecionar diretório de saída
        JFileChooser outputChooser = new JFileChooser();
        outputChooser.setDialogTitle("Selecione o diretório de saída");
        outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (outputChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
        File outputDir = outputChooser.getSelectedFile();

        // Processar cada imagem no diretório de entrada
        File[] files = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".tif") || 
                                                         name.toLowerCase().endsWith(".gif") || 
                                                         name.toLowerCase().endsWith(".svg") || 
                                                         name.toLowerCase().endsWith(".png") || 
                                                         name.toLowerCase().endsWith(".jpg") ||
                                                         name.toLowerCase().endsWith(".jpeg"));
        if (files == null || files.length == 0) {
            IJ.showMessage("Nenhuma imagem encontrada no diretório de entrada.");
            return;
        }

        for (File file : files) {
            IJ.log("Processando imagem: " + file.getName());
            processImage(file, outputDir);
        }

        IJ.showMessage("Extração de ROIs concluída!");
    }

    private void processImage(File file, File outputDir) {
        // Abrir a imagem
        ImagePlus img = IJ.openImage(file.getAbsolutePath());
        if (img == null) {
            IJ.log("Erro ao abrir imagem: " + file.getName());
            return;
        }

        // Converter para 8-bit (necessário para threshold)
        IJ.run(img, "8-bit", "");

        // Aplica um threshold (Limiar) automático
        IJ.setAutoThreshold(img, "Default");

        // Converte a imagem para binário
        IJ.run(img, "Convert to Mask", "");

        // Preenche buracos internos na imagem 
        IJ.run(img, "Fill Holes", "");

        // Identificar partículas (ROIs) automaticamente
        // Definir tamanho minimo da area em pixels de forma a ignorar ruidos 
        // Testando... -> (32*32)=1024
        IJ.run(img, "Analyze Particles...", "size=1024-Infinity add");

        // Obter o RoiManager
        RoiManager roiManager = RoiManager.getInstance();
        if (roiManager == null) roiManager = new RoiManager();

        Roi[] rois = roiManager.getRoisAsArray();

        // Salvar cada ROI como imagem individual
        for (int i = 0; i < rois.length; i++) {
            img.setRoi(rois[i]);
            ImagePlus roiImage = new ImagePlus("ROI_" + i, img.getProcessor().crop());

            // Expressao regular para nomear a imagem de saida
            File outputFile = new File(outputDir, file.getName().replaceAll("\\.\\w+$", "") + "_ROI_" + (i + 1) + ".png"); 
            try {
                ImageIO.write(roiImage.getBufferedImage(), "PNG", outputFile);
                IJ.log("ROI salva: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                IJ.log("Erro ao salvar ROI: " + e.getMessage());
            }
        }
        
        // Limpar RoiManager após processamento
        roiManager.reset();
        img.close();
    }
}