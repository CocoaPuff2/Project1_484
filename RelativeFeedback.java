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
    // use sep array for the weights
    // then use the updated weights for the new iterations of the WMD
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
        double minNonZeroStdev = Double.MAX_VALUE;

        //  standard deviation for each column
        for (int j = 0; j < numFeatures; j++) {
            double average = 0.0;

            // average of current feature
            for (int i = 0; i < subFeatureMatrix.length; i++) {
                average += subFeatureMatrix[i][j];
            }
            average /= subFeatureMatrix.length;

            double sumSqDiff = 0.0;

            // calculate the sum of squared differences from the average for each feature
            for (int i = 0; i < subFeatureMatrix.length; i++) {
                sumSqDiff += Math.pow(subFeatureMatrix[i][j] - average, 2);
            }


            stdevs[j] = Math.sqrt(sumSqDiff / (subFeatureMatrix.length - 1));

            // special cases: minimum non-zero stdev
            if (stdevs[j] != 0) {
                minNonZeroStdev = Math.min(minNonZeroStdev, stdevs[j]);
            }
            // Calculate initial weight as 1 / STDEV
            updatedWeights[j] = (stdevs[j] != 0) ? (1.0 / stdevs[j]) : Double.MAX_VALUE;
            // totalWeight += updatedWeights[j];
        }

        // Recompute the weights based on standard deviation
        for (int j = 0; j < numFeatures; j++) {
            if (stdevs[j] == 0) { // Special case: stdev  ==  zero
                double average = 0.0;

                // Calculate the average again to check for special conditions
                for (int i = 0; i < subFeatureMatrix.length; i++) {
                    average += subFeatureMatrix[i][j];
                }
                average /= subFeatureMatrix.length;

                if (average != 0) {
                    // If the average is non-zero and STDEV is 0, set weight to 0.5 * minNonZeroStdev
                    updatedWeights[j] = 0.5 * minNonZeroStdev;
                } else {
                    // If the average is zero, set the weight to 0
                    updatedWeights[j] = 0.0;
                }
            } else {
                // Regular case: weight = 1 / STDEV
                updatedWeights[j] = 1.0 / stdevs[j];
            }

            // Add the updated weight to the totalWeight for normalization later
            totalWeight += updatedWeights[j];
        }

        // Normalize the updated weights (so they sum up to 1)
        for (int j = 0; j < numFeatures; j++) {
            updatedWeights[j] /= totalWeight; // Divide each weight by the total sum of weights
        }

        // Return the final normalized weights
        return updatedWeights;
    }

}
