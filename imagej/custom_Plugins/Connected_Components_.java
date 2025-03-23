import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import java.awt.*;
import java.util.*;

public class Connected_Components_ implements PlugInFilter {

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_8G; // Suporta imagens em escala de cinza
    }

    @Override
    public void run(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();

        // 🔎 Verificar se a imagem é binária
        if (!isBinary(ip)) {
            IJ.error("Erro", "A imagem de entrada precisa ser binária (valores 0 ou 255)");
            return;
        }

        // 🎯 Exibir menu para o usuário escolher o tipo de saída
        String[] options = {"Tons de Cinza", "Colorida (RGB)"};
        GenericDialog gd = new GenericDialog("Escolha o tipo de saída");
        gd.addChoice("Tipo de saída:", options, options[0]);
        gd.showDialog();

        if (gd.wasCanceled()) return;

        boolean useColor = gd.getNextChoice().equals("Colorida (RGB)");

        // Criar uma cópia da imagem para rotulagem
        ImagePlus clonedImg = IJ.createImage("Labeled Components", useColor ? "RGB" : "8-bit", width, height, 1);
        ImageProcessor clonedIp = clonedImg.getProcessor();

        int[][] labels = new int[width][height];
        int label = 1;

        Queue<Point> queue = new LinkedList<>();

        // 🔗 Executar o algoritmo BFS para rotular componentes conexos
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (ip.getPixel(x, y) == 255 && labels[x][y] == 0) {
                    labels[x][y] = label;
                    queue.add(new Point(x, y));

                    while (!queue.isEmpty()) {
                        Point p = queue.poll();
                        int px = p.x;
                        int py = p.y;

                        int[][] neighbors = {
                            {px + 1, py}, {px - 1, py},
                            {px, py + 1}, {px, py - 1}
                        };

                        for (int[] neighbor : neighbors) {
                            int nx = neighbor[0];
                            int ny = neighbor[1];

                            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                                if (labels[nx][ny] == 0 && ip.getPixel(nx, ny) == 255) {
                                    labels[nx][ny] = label;
                                    queue.add(new Point(nx, ny));
                                }
                            }
                        }
                    }

                    label++;
                }
            }
        }

        // 🎨 Atribuir cores únicas para cada componente
        if (useColor) {
            // Para evitar repetição, usamos um Set de cores já usadas
            Set<Color> usedColors = new HashSet<>();
            Color[] colors = new Color[label];
            Random rand = new Random();

            for (int i = 1; i < label; i++) {
                Color newColor;
                do {
                    newColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
                } while (usedColors.contains(newColor)); // Evitar cores repetidas

                usedColors.add(newColor);
                colors[i] = newColor;
            }

            // Atribuir as cores à imagem
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (labels[x][y] != 0) {
                        clonedIp.setColor(colors[labels[x][y]]);
                        clonedIp.drawPixel(x, y);
                    }
                }
            }
        } else {
            // 🖤 Tons de cinza (escala entre 50 e 255)
            int step = 255 / label;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (labels[x][y] != 0) {
                        int grayValue = 50 + (labels[x][y] * step) % 205; // Evita valores muito escuros
                        clonedIp.putPixel(x, y, grayValue);
                    }
                }
            }
        }

        // Mostrar imagem rotulada
        clonedImg.show();

        IJ.log("Número total de componentes conexos: " + (label - 1));
    }

    // 📏 Função para verificar se a imagem é binária (0 ou 255)
    private boolean isBinary(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = ip.getPixel(x, y);
                if (pixel != 0 && pixel != 255) {
                    return false;
                }
            }
        }
        return true;
    }
}