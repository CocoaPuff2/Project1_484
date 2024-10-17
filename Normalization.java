public class Normalization {

    // PART 2: Assign 2 (e) Feature Normalization
    public double[] calculateAverages(double[][] featureMatrix) {
        int numRows = featureMatrix.length; // total # images
        int numCols = featureMatrix[0].length; // total # of features
        double[] averages = new double[numCols];

        // Calculate averages of each column in the feature matrix
        for (int j = 0; j < numCols; j++) {
            double sum = 0;
            for (int i = 0; i < numRows; i++) {
                sum += featureMatrix[i][j];
            }
            averages[j] = sum / numRows;
        }

        return averages; // averages for each col
    }

    public double[] calculateStandardDeviations(double[][] featureMatrix, double[] averages) {
        int numRows = featureMatrix.length; // total # images
        int numCols = featureMatrix[0].length; // total # of features
        double[] stdDevs = new double[numCols];

        // Calculate standard deviations for each column in the feature matrix
        for (int j = 0; j < numCols; j++) {
            double sumSquaredDiffs = 0;
            for (int i = 0; i < numRows; i++) {
                double diff = featureMatrix[i][j] - averages[j];
                sumSquaredDiffs += diff * diff;
            }
            stdDevs[j] = Math.sqrt(sumSquaredDiffs / numRows);
        }

        return stdDevs; // stdev for each col
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
