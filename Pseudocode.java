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
                Feature Vector/Matrix A: features of HistoA (100 x 25) and Feature Vector B is features of Histob (100 x 64)
        d. Concatenation: Combine Vector A and B to get FA Feature Vector

   Part 2: Feature Normalization
        e. Get averages of each value in FA vector (add all col values / 100)
        f. Get STDEV of each value in FA Feature vector
            square root ( ∑ (x - average ^ 2)) / N - 1)). N is the number of cols
            CODE: Use built in STDEV function?

   Part 3:  Gaussian Normalization
        g.  (value - average / STDEV). Values are the values from the FA Feature Vector
            ** Use this GN matrix for RF **

   Part 4:
        h. Initial weight: 1 / N (N is the number of columns)
        i. Weighted Manhattan Distance: D(i, j): ∑ Wi | fi(i)  +  fj(j) |
                    where  Wi =  1 / N. Initially, N = 89 for Assign 2
        j. Enable users to pick relevant images. Get rows from GN and extract x rows
            ex: x (3) relevant images? -->  Extract the x (3) images rows so it’s (3 x 89).
        k. Recompute weights:
            1. Calculate STDEV of each col
            2. Initial Weight = 1 / STDEV for each col
            3. Normalized Weight = (initial weight) / summation of ALL weights

     */
}
