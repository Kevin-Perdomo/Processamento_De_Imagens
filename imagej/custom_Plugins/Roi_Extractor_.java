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
        // Abrir a imagem original em cores
        ImagePlus originalImg = IJ.openImage(file.getAbsolutePath());
        if (originalImg == null) {
            IJ.log("Erro ao abrir imagem: " + file.getName());
            return;
        }

        // Converter para 8-bit (para processamento de threshold)
        ImagePlus img = originalImg.duplicate(); // Duplicar para manter a original em RGB
        IJ.run(img, "8-bit", "");

        // Aplicar threshold automático
        IJ.setAutoThreshold(img, "Default");

        // Converter para binário (máscara)
        IJ.run(img, "Convert to Mask", "");

        // Preencher buracos internos
        IJ.run(img, "Fill Holes", "");

        // Detectar partículas (ROIs) com tamanho mínimo de 1024 pixels
        IJ.run(img, "Analyze Particles...", "size=1024-Infinity add");

        // Obter o RoiManager
        RoiManager roiManager = RoiManager.getInstance();
        if (roiManager == null) roiManager = new RoiManager();

        Roi[] rois = roiManager.getRoisAsArray();

        // Salvar cada ROI como imagem RGB
        for (int i = 0; i < rois.length; i++) {
            // Definir ROI na imagem original (em cores)
            originalImg.setRoi(rois[i]);
            ImageProcessor processor = originalImg.getProcessor().crop(); // Recortar da imagem original em cores
            ImagePlus roiImage = new ImagePlus("ROI_" + i, processor);

            // Nome do arquivo de saída
            File outputFile = new File(outputDir, file.getName().replaceAll("\\.\\w+$", "") + "_ROI_" + (i + 1) + ".png");
            try {
                ImageIO.write(roiImage.getBufferedImage(), "PNG", outputFile);
                IJ.log("ROI salva em RGB: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                IJ.log("Erro ao salvar ROI: " + e.getMessage());
            }
        }

        // Limpar RoiManager após o processamento
        roiManager.reset();
        originalImg.close();
        img.close();
    }
}