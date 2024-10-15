public class Normalization {
    // PART 2: Assign 2 (e) Feature Normalization
    public double[][] normalizeFeatures(double[][] featureMatrix) {
        // get averages of each value in the FAFeatureMatrix
        int numRows = featureMatrix.length; // total # images
        int numCols = featureMatrix[0].length; // total # of features
        double[][] normalizedMatrix = new double[numRows][numCols];

        // Calculate averages and standard deviations for each feature
        double[] averages = new double[numCols];
        double[] stdDevs = new double[numCols];

        // (e) Calculate averages
        for (int j = 0; j < numCols; j++) {
            double sum = 0;
            for (int i = 0; i < numRows; i++) {
                sum += featureMatrix[i][j];
            }
            averages[j] = sum / numRows;
        }

        // (f) Calculate standard deviations (STDEV)
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

        return normalizedMatrix; // Return the normalized feature matrix
    }


    // PART 3: GAUSSIAN NORMALIZATION
    // (value - average / STDEV)
    //  Values are the values from the FA Feature Vector
    public static double[][] gaussianNormalization(double[][] featureMatrix) {
        int numRows = featureMatrix.length; // total images
        int numCols = featureMatrix[0].length; // total number of features
        double[][] normalizedMatrix = new double[numRows][numCols];

        for (int j = 0; j < numCols; j++) {
            double sum = 0;
            double sumSq = 0;

            // 1. Calculate the average
            for (int i = 0; i < numRows; i++) {
                sum += featureMatrix[i][j];
            }
            double average = sum / numRows;

            // 2. Calculate the variance
            for (int i = 0; i < numRows; i++) {
                double diff = featureMatrix[i][j] - average;
                sumSq += diff * diff; // Squared difference
            }
            double variance = sumSq / (numRows - 1); // Sample variance
            double stdev = Math.sqrt(variance); // Standard deviation

            // 3. Normalize the feature values
            for (int i = 0; i < numRows; i++) {
                if (stdev != 0) { // Avoid division by zero
                    double normalizedValue = (featureMatrix[i][j] - average) / stdev; // Gaussian normalization
                    normalizedMatrix[i][j] = normalizedValue;
                } else {
                    normalizedMatrix[i][j] = 0; // or you could choose to retain the original value
                }
            }
        }
        return normalizedMatrix;
    }
}
