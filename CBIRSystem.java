import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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
                    // Calculate intensity histogram
                    int[] intensityHistogram = Histograms.intensityMethod(queryImage);
                    histograms.put(imagePaths[currentPage * IMAGES_PER_PAGE], intensityHistogram);

                    // Calculate color code histogram
                    int[] colorCodeHistogram = Histograms.colorCodeMethod(queryImage);
                    histograms.put(imagePaths[currentPage * IMAGES_PER_PAGE], colorCodeHistogram);

                    // Now, we can sort the images based on a combined method
                    // Combine histograms logic - can average or calculate distances based on both
                    sortImagesCombined(intensityHistogram, colorCodeHistogram);
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

    // Assign 2: (Part ???) calculate distances for both methods & sort  images based on the combined distances
    private void sortImagesCombined(int[] intensityHistogram, int[] colorCodeHistogram) {
        List<Map.Entry<String, Double>> distanceList = new ArrayList<>();

        for (String imagePath : imagePaths) {
            BufferedImage image;
            try {
                image = ImageIO.read(new File("images/" + imagePath));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            // Calculate histograms for the current image
            int[] imageIntensityHistogram = Histograms.intensityMethod(image);
            int[] imageColorCodeHistogram = Histograms.colorCodeMethod(image);

            // Calculate distances for both histograms
            double intensityDistance = Histograms.manhattanDistance(intensityHistogram, imageIntensityHistogram, Histograms.INTENSITY_NUM_BINS);
            double colorCodeDistance = Histograms.manhattanDistance(colorCodeHistogram, imageColorCodeHistogram, Histograms.COLORCODE_NUM_BINS);

            // Combine distances (you can adjust the combination method)
            double combinedDistance = intensityDistance + colorCodeDistance; // Example: summing distances

            distanceList.add(new AbstractMap.SimpleEntry<>(imagePath, combinedDistance));
        }

        // Sort the list based on the combined distance
        distanceList.sort(Entry.comparingByValue());
        imagePaths = distanceList.stream().map(Entry::getKey).toArray(String[]::new);
        displayImages();
    }

    private void loadImages() {
        File imagesDir = new File("images");
        if (imagesDir.exists() && imagesDir.isDirectory()) {
            imagePaths = imagesDir.list((dir, name) -> name.endsWith(".jpg"));
        }
    }

    private void displayImages() {
        imageGridPanel.removeAll();
        imageCheckboxes.clear();
        int startIndex = currentPage * IMAGES_PER_PAGE;
        int endIndex = Math.min(startIndex + IMAGES_PER_PAGE, imagePaths.length);

        pageLabel.setText("Page: " + (currentPage + 1));

        for (int i = startIndex; i < endIndex; i++) {
            JPanel imagePanel = new JPanel();
            imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
            imagePanel.setBorder(new EmptyBorder(5, 5, 5, 5));

            JLabel imageLabel = new JLabel(resizeImage("images/" + imagePaths[i], 80, 80));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel filenameLabel = new JLabel(imagePaths[i]);
            filenameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            filenameLabel.setFont(new Font("Serif", Font.PLAIN, 10));

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

            imagePanel.add(imageLabel);
            imagePanel.add(filenameLabel);

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
