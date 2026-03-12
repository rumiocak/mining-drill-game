import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    /**
     * Reads the attributes of valuable items from a file and creates a list of Valuable objects.
     * This method reads data from a text file containing attributes of valuable items, such as name, value, and weight.
     * It iterates through each line of the file, skipping the first line with column headers.
     * For each line, it parses the data and creates a Valuable object with the corresponding attributes.
     * Valuable objects are created using images from a predefined list of valuable images.
     * If the number of items in the file exceeds the number of available images, it stops adding new items.
     *
     * @return A list of Valuable objects read from the file.
     */
    public static List<Valuable> readValuablesFromFile() {
        List<Valuable> valuables = new ArrayList<>();

        List<Image> valuableImages = new ArrayList<>();
        valuableImages.add(new Image("/assets/underground/valuable_ironium.png"));
        valuableImages.add(new Image("/assets/underground/valuable_bronzium.png"));
        valuableImages.add(new Image("/assets/underground/valuable_silverium.png"));
        valuableImages.add(new Image("/assets/underground/valuable_goldium.png"));
        valuableImages.add(new Image("/assets/underground/valuable_platinum.png"));
        valuableImages.add(new Image("/assets/underground/valuable_einsteinium.png"));
        valuableImages.add(new Image("/assets/underground/valuable_emerald.png"));
        valuableImages.add(new Image("/assets/underground/valuable_ruby.png"));
        valuableImages.add(new Image("/assets/underground/valuable_diamond.png"));
        valuableImages.add(new Image("/assets/underground/valuable_amazonite.png"));

        // Load the file as a resource using the class loader
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FileManager.class.getResourceAsStream("/assets/atributes_of_valuables.txt")))) {
            String line;
            boolean isFirstLine = true;
            int imageIndex = 0;
            while ((line = reader.readLine()) != null) {
                // Skip the first line as it contains column headers
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split("\t");
                String name = parts[0];
                int value = Integer.parseInt(parts[1]);
                int weight = Integer.parseInt(parts[2]);

                // Ensure the valuableImages list remains within bounds
                if (imageIndex >= valuableImages.size()) {
                    break;
                }

                Image image = valuableImages.get(imageIndex);
                imageIndex++;

                valuables.add(new Valuable(name, image, weight, value));
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return valuables;
    }

    /**
     * Loads and returns an array of images representing different frames of the flying machine animation.
     *
     * @return An array of Image objects containing frames of the flying machine animation.
     */
    public static Image[] loadFlyingMachineImages() {
        Image[] flyingMachineImages = new Image[3];
        for (int i = 0; i < 3; i++) {
            int imageNumber = i + 25;
            flyingMachineImages[i] = new Image("/assets/drill/drill_" + imageNumber + ".png");
        }

        return flyingMachineImages;
    }

    /**
     * Shifts the content of the given image to the left by the specified amount.
     *
     * @param image The image whose content is to be shifted.
     * @param shiftAmount The amount by which to shift the content to the left.
     * @return An Image object with the content shifted to the left.
     */
    public static Image shiftImageContentLeft(Image image, double shiftAmount) {
        // Get the dimensions of the original image
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Create a writable image with the same dimensions as the original image
        WritableImage writableImage = new WritableImage(width, height);

        // Get the pixel reader and writer for the original image
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        // Copy pixels from the original image to the writable image with the specified shift direction
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Calculate the new X coordinate after shifting
                int newX = (int) (x + shiftAmount);

                // Ensure the new X coordinate stays within bounds
                if (newX >= 0 && newX < width) {
                    // Read the color of the pixel at (newX, y) from the original image
                    Color color = pixelReader.getColor(newX, y);
                    // Write the color to the pixel at (x, y) in the writable image
                    pixelWriter.setColor(x, y, color);
                }
            }
        }

        return writableImage;
    }
}
