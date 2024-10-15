public class RelativeFeedback {

    // Part 4: (i) Weighted Manhattan Distance
    public static double weightedManhattanDistance(double[] featureVectorA, double[] featureVectorB) {
        if (featureVectorA.length != featureVectorB.length) {
            throw new IllegalArgumentException("Feature vectors should have same length.");
        }

        double sum = 0.0;
        int N = featureVectorA.length;
        double weight = 1.0 / N; // initial weight

        for (int i = 0; i < N; i++) {
            //  D(i, j): ∑ Wi |fi(i) + fj(j)|
            sum += weight * Math.abs(featureVectorA[i] - featureVectorB[i]);
        }

        return sum;
    }


}
