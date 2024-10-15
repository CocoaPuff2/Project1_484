public class Normalization {

    // PART 2: Assign 2 (e) Feature Normalization
    public double[][] normalizeFeatures(double[][] featureMatrix) {
        int numRows = featureMatrix.length; // total # images
        int numCols = featureMatrix[0].length; // total # of features
        double[][] normalizedMatrix = new double[numRows][numCols];

        // Calculate averages and standard deviations for each feature
        double[] averages = new double[numCols];
        double[] stdDevs = new double[numCols];

        // (e) Calculate averages of each value in the FAFeatureMatrix
        for (int j = 0; j < numCols; j++) {
            double sum = 0;
            for (int i = 0; i < numRows; i++) {
                sum += featureMatrix[i][j];
            }
            averages[j] = sum / numRows;
        }

        // (f) Calculate standard deviations (STDEV) in FAFeature vector
        for (int j = 0; j < numCols; j++) {
            double sumSquaredDiffs = 0;
            for (int i = 0; i < numRows; i++) {
                double diff = featureMatrix[i][j] - averages[j];
                sumSquaredDiffs += diff * diff;
            }
            stdDevs[j] = Math.sqrt(sumSquaredDiffs / numRows);
        }


        /*
        // (g) Normalize features
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                normalizedMatrix[i][j] = (stdDevs[j] > 0) ? (featureMatrix[i][j] - averages[j]) / stdDevs[j] : 0; // Avoid division by zero
            }
        }

         */

        return new double[][]{averages, stdDevs};  // Return the normalized feature matrix
    }


    // PART 3: GAUSSIAN NORMALIZATION
    public static double[][] gaussianNormalization(double[][] featureMatrix, double[] averages, double[] stdDevs) {
        int numImages = featureMatrix.length; // total images
        int numFeatures = featureMatrix[0].length; // total number of features

        // ** Will use this matrix from now on **
        double[][] GNnormalizedMatrix = new double[numImages][numFeatures];

        // (value - average / STDEV) --> Values are the values from the FA Feature Vector
        for (int i = 0; i < numImages; i++) {
            for (int j = 0; j < numFeatures; j++) {
               //  (value - average / STDEV)
               GNnormalizedMatrix[i][j] = (featureMatrix[i][j] - averages[j]) / stdDevs[j];
            }
        }

        return GNnormalizedMatrix;
    }
}
