import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.*;
import ij.plugin.filter.*;

public class RGB_Split_ implements PlugInFilter {

    ImagePlus imp;

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_RGB; // This plugin accepts RGB images
    }

    public void run(ImageProcessor ip) {
        int width = imp.getWidth();
        int height = imp.getHeight();

        // Create new ImageProcessors for the red, green, and blue channels
        ImageProcessor redIP = new ByteProcessor(width, height);
        ImageProcessor greenIP = new ByteProcessor(width, height);
        ImageProcessor blueIP = new ByteProcessor(width, height);

        // Get the pixel data as an array of integers (each int represents an RGB pixel)
        int[] pixels = (int[]) ip.getPixels();

        // Iterate through the pixels and extract the RGB components
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x; // Calculate the index in the pixel array
                int rgb = pixels[index];

                // Extract the red, green, and blue components using bitwise operations
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Set the corresponding pixel values in the new ImageProcessors
                redIP.set(x, y, red);
                greenIP.set(x, y, green);
                blueIP.set(x, y, blue);
            }
        }

        // Create new ImagePlus objects from the ImageProcessors
        ImagePlus redImp = new ImagePlus("Red Channel", redIP);
        ImagePlus greenImp = new ImagePlus("Green Channel", greenIP);
        ImagePlus blueImp = new ImagePlus("Blue Channel", blueIP);

        // Display the new images
        redImp.show();
        greenImp.show();
        blueImp.show();
    }
}