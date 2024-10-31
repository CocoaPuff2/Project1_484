import java.util.Arrays;
public class RelativeFeedback {

    // Part 4: (h) and (i) Weighted Manhattan Distance
    public static double[] calculateInitialWeights(int numFeatures) {
        double[] initialWeights = new double[numFeatures];
         Arrays.fill(initialWeights, 1.0 / numFeatures);
        System.out.println("Initial Weights: " + Arrays.toString(initialWeights));
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
                throw new IllegalArgumentException("Index " + index + " is OUT of bounds.");
            }

            relevantImages[i] = normalizedFeatures[index]; // Copy selected row
        }

        return relevantImages; // return extracted rows (x x 89)
    }

    public static double[] recomputeWeights(double[][] subFeatureMatrix) {

        if (subFeatureMatrix.length == 0) {
            System.out.println("No relevant images selected. Exiting weight recomputation.");
            return new double[0]; // Return an empty weight array or handle this case accordingly
        }
        int numFeatures = subFeatureMatrix[0].length;
        double[] stdevs = new double[numFeatures];
        double[] updatedWeights = new double[numFeatures];
        double totalWeight = 0.0;
        double minNonZeroStdev = Double.MAX_VALUE;

        // Step 1: Get STDEV for each relevant image col
        for (int j = 0; j < numFeatures; j++) {
            double average = 0.0;

            // average of current feature
            for (int i = 0; i < subFeatureMatrix.length; i++) {
                average += subFeatureMatrix[i][j];
            }
            average /= subFeatureMatrix.length; // average calculation

            double sumSqDiff = 0.0;

            // calculate: sum of squared differences from the average for each feature
            for (int i = 0; i < subFeatureMatrix.length; i++) {
                sumSqDiff += Math.pow(subFeatureMatrix[i][j] - average, 2);
            }

            // STDEV calculation
            stdevs[j] = Math.sqrt(sumSqDiff / (subFeatureMatrix.length - 1));

            // special cases: minimum non-zero stdev
            if (stdevs[j] != 0) {
                minNonZeroStdev = Math.min(minNonZeroStdev, stdevs[j]);
            }
        }

        // PRINT: recomputed standard deviations (from step 1 of recompute weights)
        // System.out.println("Recomputed Standard Deviations FOR TEST: " + Arrays.toString(stdevs));

        // Step 2: Updated Weights
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
                // Step 2: Regular case: weight = 1 / STDEV
                updatedWeights[j] = 1.0 / stdevs[j];
            }
        }

        // PRINT: Initial (Updated) Weight (happens inside recompute weights)
        // System.out.println("Initial (Updated) Weights FOR TEST: " + Arrays.toString(updatedWeights));

        // Step 3: Normalize the Weights

        for (double weight : updatedWeights) {
            totalWeight += weight; // Sum all weights
        }

        for (int j = 0; j < numFeatures; j++) {
            updatedWeights[j] /= totalWeight; // Normalize each weight
        }

        // PRINT: Normalized Weight (happens inside recompute weights)
        // System.out.println("Normalized Weights: " + Arrays.toString(updatedWeights));

        // Return the final normalized weights
        return updatedWeights;
    }

}
