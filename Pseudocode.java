public class Pseudocode {

/* Pseudocode for Assignment 2:

    GOAL 1: Intensity + Color Code method
    GOAL 2: RF (link high level concepts w/ low level features)

    # of cols = # of features
    # of rows = # of images


   Part 1: Combined Feature Vector
        a. Get intensity method result (for all (100) images) → 25 histogram bins (put into HistoA)
        b. Get color code method result (for all (100) images) → 64 histogram bins (HistoB)
        c. Get the features for HistoA and HistoB (each histogram count value of an image  / size of that image).
               Get features:  each col / corresponding image size
               ** each row has corresponding image size

                Feature Vector/Matrix A: features of HistoA (100 x 25) and Feature Vector B is features of Histob (100 x 64)
        d. Concatenation: Combine Vector A and B to get FA Feature Vector

   Part 2: Feature Normalization
        e. Get averages of each value in FA vector (add all col values / 100)
        f. Get STDEV of each value in FA Feature vector
            square root ( ∑ (x - average ^ 2)) / N - 1)). N is the number of ROWS (image count)
            CODE: Use built in STDEV function??

   Part 3:  Gaussian Normalization
        g.  Gaussian Normalized Value: (feature value - average / STDEV). Values are the feature values from the FA Feature Vector
            ** Use this GN matrix for RF **

   Part 4:
        h. Initial weight: 1 / N (N is the number of columns) (1/89)

        i. Weighted Manhattan Distance: D(i, j): ∑ Wi * | fi(i)  -  fj(j) |
                    where  Wi =  1 / N. Initially, N = 89 for Assign 2.
                    N here is the # of features

        j. Enable users to pick relevant images.
            Get/extract rows from GN Feature Matrix for the relevant images the user picked
            into SubFeatureMatrix
           ex: 3 relevant images? -->  Extract the 3 images rows so it’s (3 x 89).

            Which feature more important? -->   cols that have the smallest variance
                                                smaller variance = bigger weight

       --> k. Recompute weights: (**Do STDEV based on SubFeatureMatrix)

            1. Calculate STDEV of each col (for each col of the relevant images)
            2. Initial (Updated) Weight = 1 / STDEV for each col (each col has own weight, the whole col weight changes)

                IF STDEV = 0
                    IF mean is NOT 0 --> 0.5 × min(non-zero standard deviations);
                    If mean IS 0 --> Weight = 0

                if STDEV smaller --> HIGHER WEIGHT
                if STDEV bigger --> LOWER WEIGHT

            3. Normalized Weight = (updated weight) / summation of ALL weights

             ****** Do the WMD with the GNMatrix (all the 100 images) ******

                --> NOW use this weight for the WeightedMD: (Steps i-k repeated)
                    D(i, j): ∑ Wi * | fi(i) - fj(j) |
                    where  Wi =  Normalized Weight

              ** display ALL images based on new relevancy **

    ---> if user doesn't pick as relevant, there is no weight (0)

1. when we recompute the weights and do the weighted manhattan distance formula AGAIN after the
users have selected relevant images, we have to do the calculations for the entire GNNormalizedMatrix

2. After the users pick relevant images, then we need to display ALL the images from the database,
not just relevant ones


     */


}
