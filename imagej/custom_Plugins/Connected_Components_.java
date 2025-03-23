import ij.*;
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

        // Criar uma cópia da imagem original para não modificar a original
        ImagePlus clonedImg = IJ.createImage("Labeled Components", "RGB", width, height, 1);
        ImageProcessor clonedIp = clonedImg.getProcessor();

        // Matriz para armazenar os rótulos
        int[][] labels = new int[width][height];
        int label = 1;

        // Fila FIFO para busca em largura (BFS)
        Queue<Point> queue = new LinkedList<>();

        // Percorre todos os pixels da imagem
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // Se o pixel é parte de um componente e ainda não foi rotulado
                if (ip.getPixel(x, y) != 0 && labels[x][y] == 0) {
                    labels[x][y] = label; // Atribui o rótulo
                    queue.add(new Point(x, y)); // Adiciona à fila

                    // Executa o algoritmo BFS para rotular os pixels conectados
                    while (!queue.isEmpty()) {
                        Point p = queue.poll(); // Remove o próximo ponto da fila
                        int px = p.x;
                        int py = p.y;

                        // Vizinhos em 4 direções (Norte, Sul, Leste, Oeste)
                        int[][] neighbors = {
                            {px + 1, py}, // Direita
                            {px - 1, py}, // Esquerda
                            {px, py + 1}, // Abaixo
                            {px, py - 1}  // Acima
                        };

                        for (int[] neighbor : neighbors) {
                            int nx = neighbor[0];
                            int ny = neighbor[1];

                            // Verifica se está dentro dos limites da imagem
                            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                                // Se o pixel vizinho ainda não foi rotulado e pertence ao mesmo componente
                                if (labels[nx][ny] == 0 && ip.getPixel(nx, ny) == ip.getPixel(px, py)) {
                                    labels[nx][ny] = label; // Atribui o mesmo rótulo
                                    queue.add(new Point(nx, ny)); // Adiciona o vizinho à fila
                                }
                            }
                        }
                    }

                    label++; // Incrementa o rótulo para o próximo componente
                }
            }
        }

        // Gerar cores aleatórias para os componentes
        Random rand = new Random();
        Color[] colors = new Color[label];
        for (int i = 1; i < label; i++) {
            colors[i] = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)); // RGB aleatório
        }

        // Preencher a imagem com os componentes coloridos
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (labels[x][y] != 0) {
                    clonedIp.setColor(colors[labels[x][y]]);
                    clonedIp.drawPixel(x, y);
                }
            }
        }

        // Mostrar a imagem rotulada como uma nova imagem colorida
        clonedImg.show();

        IJ.log("Número total de componentes conexos: " + (label - 1));
    }
}
