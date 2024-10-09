import java.awt.image.BufferedImage;
import java.util.Arrays;

// calculates color histograms for an image
public class Histograms {

    static final int INTENSITY_NUM_BINS = 25;
    static final int INTENSITY_BIN_SIZE = 10;
     static final int COLORCODE_NUM_BINS = 64;

    // Calculate Intensity Histogram of an image
    public static int[] intensityMethod(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] histogram = new int[INTENSITY_NUM_BINS + 1];
        histogram[0] = width * height; // store total pixels / image size

        Arrays.fill(histogram, 1, histogram.length, 0); // Filling from index 1

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // rgb value of pixel at "[0,0]"
                int rgb = image.getRGB(x, y);
                // Needed to google for this part for syntax to get the r, g, and b values
                // SHIFT r value right (by 16 bits) to get intensity of red pixel
                int r = (rgb >> 16) & 0xff; // 0xFF = only get 8 bits
                // SHIFT g value right (by 8 bits) to get intensity of green pixel
                int g = (rgb >> 8) & 0xff;
                // MASK rgb value to get intensity of blue pixel
                // mask --> isolate just the section that we need
                int b = rgb & 0xff;

                // Calculate the intensity value (I)
                double intensity = (0.299 * r) + (0.587 * g) + (0.114 * b);

                // put I value in corresponding bin (ensure in bounds)
                int binIndex = (int) (intensity / INTENSITY_BIN_SIZE);
               //  int binIndex = Math.min((int) (intensity / INTENSITY_BIN_SIZE), INTENSITY_NUM_BINS);
                if (binIndex >= INTENSITY_NUM_BINS) {
                    binIndex = INTENSITY_NUM_BINS - 1; // Cap at the last bin
                }
                histogram[binIndex + 1]++; //  Increment count in the corresponding bin
            }
        }

        // process each pixel and assign to the bins
        return histogram;
    }

    // Calculate color-code histograms of an image
    public static int[] colorCodeMethod(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] histogram = new int[COLORCODE_NUM_BINS + 1]; // Initialize histogram for color codes
        histogram[0] = width * height;

        Arrays.fill(histogram, 1, histogram.length, 0); // Filling default values of 0


        // Calculate histogram based on the color code method
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                // extract red, green, and blue values of the pixel
                int red = (rgb >> 16) & 0xFF;  // Extract the red component
                int green = (rgb >> 8) & 0xFF; // Extract the green component
                int blue = rgb & 0xFF;         // Extract the blue component

                int red2Bits = red >> 6;    // Reduce red value to 2 bits (0-3)
                int green2Bits = green >> 6; // Reduce green value to 2 bits (0-3)
                int blue2Bits = blue >> 6;   // Reduce blue value to 2 bits (0-3)

                // Combine into single color code
                int colorCode = (red2Bits << 4) | (green2Bits << 2) | blue2Bits;

                histogram[colorCode + 1]++;
            }

        }

        return histogram;
    }


    public static double manhattanDistance(int[] histo1, int[] histo2, int numBins) {
        double distance = 0.0;
        // Get the total number of pixels from the histograms
        double totalPixelsHisto1 = histo1[0];
        double totalPixelsHisto2 = histo2[0];

        // for each bin in histogram...
        for (int i = 1; i <= numBins; i++) {
            // calculate distance between bins (absolute value of the distance) then summation
            // Asked ChatGPT for some help,
            double normalizedHisto1 = totalPixelsHisto1 > 0 ? (double) histo1[i] / totalPixelsHisto1 : 0;
            double normalizedHisto2 = totalPixelsHisto2 > 0 ? (double) histo2[i] / totalPixelsHisto2 : 0;

            distance += Math.abs(normalizedHisto1 - normalizedHisto2);
        }
        return distance;
    }
}
