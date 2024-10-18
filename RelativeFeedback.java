import java.util.Arrays;
public class RelativeFeedback {

    // Part 4: (h) and (i) Weighted Manhattan Distance
    public static double[] calculateInitialWeights(int numFeatures) {
        double[] initialWeights = new double[numFeatures];
        Arrays.fill(initialWeights, 1.0 / numFeatures);
        return initialWeights;
    }


    public static double weightedManhattanDistance(double[] feature1, double[] feature2, double[] weights) {
        double distance = 0.0;
        for (int i = 0; i < feature1.length; i++) {
            distance += weights[i] * Math.abs(feature1[i] - feature2[i]);
        }
        return distance;
    }


    // Part 4: (j) and (k) Pick relevant images and Recompute weights:
    public static double[][] extractRelevantImages(double[][] normalizedFeatures, int[] selectedIndices) {

        // keeps track of relevant images
        double[][] relevantImages = new double[selectedIndices.length][normalizedFeatures[0].length];
        // Extract the first x rows (from the images the user picks as relevant)
        for (int i  = 0; i < selectedIndices.length; i++) {
            int index = selectedIndices[i];
            // Check if the index is valid
            if (index < 0 || index >= normalizedFeatures.length) {
                throw new IllegalArgumentException("Index " + index + " is out of bounds.");
            }

            relevantImages[i] = normalizedFeatures[index]; // Copy the selected row
        }

        return relevantImages; // Returns the extracted rows (x x 89)
    }

    public static double[] recomputeWeights(double[][] subFeatureMatrix) {
        int numFeatures = subFeatureMatrix[0].length;
        double[] stdevs = new double[numFeatures];
        double[] updatedWeights = new double[numFeatures];
        double totalWeight = 0.0;

        //  standard deviation for each column
        for (int j = 0; j < numFeatures; j++) {
            double mean = 0.0;
            for (int i = 0; i < subFeatureMatrix.length; i++) {
                mean += subFeatureMatrix[i][j];
            }
            mean /= subFeatureMatrix.length;

            double sumSqDiff = 0.0;
            for (int i = 0; i < subFeatureMatrix.length; i++) {
                sumSqDiff += Math.pow(subFeatureMatrix[i][j] - mean, 2);
            }
            stdevs[j] = Math.sqrt(sumSqDiff / (subFeatureMatrix.length - 1));

            // Calculate initial weight as 1 / STDEV (if STDEV is 0, set weight to a high value)
            updatedWeights[j] = (stdevs[j] != 0) ? (1.0 / stdevs[j]) : Double.MAX_VALUE;
            totalWeight += updatedWeights[j];
        }

        // Normalize weights
        for (int j = 0; j < numFeatures; j++) {
            updatedWeights[j] /= totalWeight;
        }

        return updatedWeights; // Return the normalized weights
    }

}
