import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class CBIRSystem extends JFrame {
    private Map<String, int[]> histograms = new HashMap<>();
    private BufferedImage queryImage;
    private static final int IMAGES_PER_PAGE = 20;
    private static final int MAX_PAGES = 5;
    private int currentPage = 0;
    private JLabel pageLabel;
    private JPanel queryImagePanel;
    private JPanel imageGridPanel;
    private JButton previousButton;
    private JButton nextButton;
    private String[] imagePaths;
    private JLabel queryImageLabel;
    private String selectedMethod = "Intensity";
    static final int INTENSITY_NUM_BINS = 25;
    static final int COLORCODE_NUM_BINS = 64;
    private JCheckBox relevanceCheckbox;
    private List<JCheckBox> imageCheckboxes = new ArrayList<>();

    public CBIRSystem() {
        setTitle("Content-Based Image Retrieval System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton intensityButton = new JButton("Intensity Method");
        JButton colorCodeButton = new JButton("Color Code Method");
        JButton combinedButton = new JButton("Intensity + Color Code Method");
        JButton submitButton = new JButton("Submit");

        int buttonWidth = 150;
        int buttonHeight = 30;
        intensityButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        colorCodeButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        combinedButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        submitButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        intensityButton.addActionListener(e -> selectedMethod = "Intensity Method");
        colorCodeButton.addActionListener(e -> selectedMethod = "Color Code Method");
        combinedButton.addActionListener(e -> selectedMethod = "Intensity + Color Code Method");

        relevanceCheckbox = new JCheckBox("Relevance");
        relevanceCheckbox.addActionListener(e -> {
            displayImages();
        });

        submitButton.addActionListener(e -> {
            if (queryImage != null) {
                if (selectedMethod.equals("Intensity Method")) {
                    int[] histogram = Histograms.intensityMethod(queryImage);
                    histograms.put(imagePaths[currentPage * IMAGES_PER_PAGE], histogram);
                    sortImages(histogram);
                } else if (selectedMethod.equals("Color Code Method")) {
                    int[] histogram = Histograms.colorCodeMethod(queryImage);
                    histograms.put(imagePaths[currentPage * IMAGES_PER_PAGE], histogram);
                    sortImages(histogram);
                } else if (selectedMethod.equals("Intensity + Color Code Method")) {
                    // loads all the images
                    BufferedImage[] allImages = new BufferedImage[imagePaths.length];

                    //
                    for (int i = 0; i < imagePaths.length; i++) {
                        try {
                            allImages[i] = ImageIO.read(new File("images/" + imagePaths[i]));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    double[][] FAFeatureMatrix = Histograms.createFAFeatureMatrix(allImages);

                    // todo:  delete later
                    /*
                    System.out.println("FAFeatureMatrix:");
                    for (int i = 0; i < FAFeatureMatrix.length; i++) {
                        for (int j = 0; j < FAFeatureMatrix[i].length; j++) {
                            System.out.printf("%.4f ", FAFeatureMatrix[i][j]);
                        }
                        System.out.println();
                    }

                     */

                    // Part 2: Normalization: (of the FAFeatureMatrix)
                    // e. averages
                    // f. STDEV
                    Normalization normalization = new Normalization();
                    double[] averages = normalization.calculateAverages(FAFeatureMatrix);
                    double[] stdDevs = normalization.calculateStandardDeviations(FAFeatureMatrix, averages);

                    /*
                    System.out.println("Averages:");
                    for (double avg : averages) {
                        System.out.printf("%.4f ", avg);
                    }
                    System.out.println();

                    // Print standard deviations
                    System.out.println("Standard Deviations:");
                    for (double stdDev : stdDevs) {
                        System.out.printf("%.4f ", stdDev);
                    }
                    System.out.println();

                     */

                    double[][] GNnormalizedMatrix = Normalization.gaussianNormalization(FAFeatureMatrix, averages, stdDevs);

                    // TODO: Note, GN method may need adjustments

                    System.out.println("GNnormalizedMatrix:");


                    // initialize weights for weighted Manhattan distance calculation
                    double[] initialWeights = RelativeFeedback.calculateInitialWeights(GNnormalizedMatrix[0].length);

                    // check if relevance feedback is enabled
                    if (relevanceCheckbox.isSelected()) {
                        List<Integer> selectedIndices = new ArrayList<>();
                        for (int i = 0; i < imageCheckboxes.size(); i++) {
                            if (imageCheckboxes.get(i).isSelected()) {
                                selectedIndices.add(currentPage * IMAGES_PER_PAGE + i);
                            }
                        }

                        // extract the relevant images
                        double [][] subFeatureMatrix = RelativeFeedback.extractRelevantImages(GNnormalizedMatrix, selectedIndices.stream().mapToInt(i -> i).toArray());

                        // Recompute weights based on the relevant images
                        initialWeights = RelativeFeedback.recomputeWeights(subFeatureMatrix);


                    }

                    // create an array with image index and image path
                    String[][] imageIndexAndPath = new String[imagePaths.length][2]; // 2D array for image index and path
                    for (int i = 0; i < imagePaths.length; i++) {
                        imageIndexAndPath[i][0] = String.valueOf(i + 1); // Store index starting from 1
                        imageIndexAndPath[i][1] = imagePaths[i];         // Store the corresponding image path
                    }

                    List<Map.Entry<String, Double>> distanceList = new ArrayList<>();
                    int queryImageIndex = 0;

                    for (int i = 0; i < GNnormalizedMatrix.length; i++) {
                        // Calculate the weighted Manhattan distance between the query image and the current image
                        double distance = RelativeFeedback.weightedManhattanDistance(
                                GNnormalizedMatrix[queryImageIndex], // The query image (assumed to be the first)
                                GNnormalizedMatrix[i],               // The current image being compared
                                initialWeights                       // Initial weights for each feature
                        );

                        // Add the image path and distance to the distanceList
                        distanceList.add(new AbstractMap.SimpleEntry<>(imagePaths[i], distance));
                    }

                    // Sort the distance list by the computed distances (ascending order)
                    distanceList.sort(Comparator.comparingDouble(Map.Entry::getValue));

                    // Update the imagePaths to reflect the new sorted order based on similarity (closest to query image first)
                    imagePaths = distanceList.stream().map(Map.Entry::getKey).toArray(String[]::new);


                    // Display the images in the new order of similarity
                    displayImages();

                }
            }
        });

        topPanel.add(intensityButton);
        topPanel.add(colorCodeButton);
        topPanel.add(combinedButton);
        topPanel.add(relevanceCheckbox);
        topPanel.add(submitButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        loadImages();

        imageGridPanel = new JPanel(new GridLayout(0, 5));
        imageGridPanel.setBorder(LineBorder.createBlackLineBorder());
        mainPanel.add(imageGridPanel, BorderLayout.WEST);

        queryImagePanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Query Image", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Serif", Font.BOLD, 30));

        queryImageLabel = new JLabel();
        queryImageLabel.setPreferredSize(new Dimension(300, 200));
        queryImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        queryImagePanel.add(headerLabel, BorderLayout.NORTH);
        queryImagePanel.add(queryImageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        JButton closeButton = new JButton("Close");
        resetButton.setFont(new Font("Serif", Font.PLAIN, 15));
        closeButton.setFont(new Font("Serif", Font.PLAIN, 15));

        resetButton.addActionListener(e -> {
            queryImageLabel.setIcon(null);
            queryImage = null;
        });

        closeButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(resetButton);
        buttonPanel.add(closeButton);
        queryImagePanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(queryImagePanel, BorderLayout.EAST);

        JPanel navigationPanel = new JPanel();
        pageLabel = new JLabel("Page: " + (currentPage + 1));
        pageLabel.setFont(new Font("Serif", Font.PLAIN, 17));

        previousButton = new JButton("Previous");
        previousButton.setFont(new Font("Serif", Font.PLAIN, 15));
        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Serif", Font.PLAIN, 15));

        previousButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                displayImages();
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage < (Math.min(MAX_PAGES, (imagePaths.length + IMAGES_PER_PAGE - 1) / IMAGES_PER_PAGE) - 1)) {
                currentPage++;
                displayImages();
            }
        });

        navigationPanel.add(previousButton);
        navigationPanel.add(pageLabel);
        navigationPanel.add(nextButton);
        mainPanel.add(navigationPanel, BorderLayout.SOUTH);

        displayImages();
        setVisible(true);
    }

    private void loadImages() {
        File imagesDir = new File("images");
        if (imagesDir.exists() && imagesDir.isDirectory()) {
            imagePaths = imagesDir.list((dir, name) -> name.endsWith(".jpg"));
        }
    }

    private void displayImages() {
        // cleanup for previous images and checkboxes
        imageGridPanel.removeAll();
        imageCheckboxes.clear();

        int startIndex = currentPage * IMAGES_PER_PAGE;
        int endIndex = Math.min(startIndex + IMAGES_PER_PAGE, imagePaths.length);

        pageLabel.setText("Page: " + (currentPage + 1));

        for (int i = startIndex; i < endIndex; i++) {
            JPanel imagePanel = new JPanel();
            imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
            imagePanel.setBorder(new EmptyBorder(5, 5, 5, 5));

            // image with filename
            JLabel imageLabel = new JLabel(resizeImage("images/" + imagePaths[i], 80, 80));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel filenameLabel = new JLabel(imagePaths[i]);
            filenameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            filenameLabel.setFont(new Font("Serif", Font.PLAIN, 10));

            // mouseclick events for the query image
            final String imagePath = imagePaths[i];
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    queryImageLabel.setIcon(resizeImage("images/" + imagePath, 300, 200));
                    try {
                        queryImage = ImageIO.read(new File("images/" + imagePath));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // adds images + filenames to GUI
            imagePanel.add(imageLabel);
            imagePanel.add(filenameLabel);

            // if relevant feedback is checked, show the checkboxes
            if (relevanceCheckbox.isSelected()) {
                JCheckBox imageCheckbox = new JCheckBox("Relevant");
                imageCheckboxes.add(imageCheckbox);
                imagePanel.add(imageCheckbox);
            }

            imageGridPanel.add(imagePanel);
        }

        imageGridPanel.revalidate();
        imageGridPanel.repaint();
    }

    private ImageIcon resizeImage(String path, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sortImages(int[] queryHistogram) {
        List<Map.Entry<String, Double>> distanceList = new ArrayList<>();

        for (String imagePath : imagePaths) {
            BufferedImage image;
            try {
                image = ImageIO.read(new File("images/" + imagePath));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            int[] imageHistogram;
            int numBins;

            // todo add new method here (?)
            if (selectedMethod.equals("Intensity Method")) {
                imageHistogram = Histograms.intensityMethod(image);
                numBins = INTENSITY_NUM_BINS;
            } else {
                imageHistogram = Histograms.colorCodeMethod(image);
                numBins = COLORCODE_NUM_BINS;
            }

            double distance = Histograms.manhattanDistance(queryHistogram, imageHistogram, numBins);
            distanceList.add(new AbstractMap.SimpleEntry<>(imagePath, distance));
        }

        distanceList.sort(Entry.comparingByValue());
        imagePaths = distanceList.stream().map(Entry::getKey).toArray(String[]::new);
        displayImages();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CBIRSystem::new);
    }
}
