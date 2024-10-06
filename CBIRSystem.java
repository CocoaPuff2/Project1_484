import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
// import java.util.HashMap;
import java.util.List;
// import java.util.Map;
import java.util.*;

public class CBIRSystem extends JFrame {
    private Map<String, int[]> histograms = new HashMap<>();
    private BufferedImage queryImage; // BufferedImage (hold the loaded image)
    private static final int IMAGES_PER_PAGE = 20; // max images per page
    private static final int MAX_PAGES = 5; // max pages
    private int currentPage = 0;
    private JLabel pageLabel; // Display page number
    private JPanel queryImagePanel; // Panel --> Display the selected image (right side)
    private JPanel imageGridPanel; // Display the grid of images (left side)
    private JButton previousButton;
    private JButton nextButton;
    private String[] imagePaths; // (Array) image file paths
    private JLabel queryImageLabel; // Label --> Display the selected/query image
    private String selectedMethod = "Intensity"; // Default histogram method
    static final int INTENSITY_NUM_BINS = 25;
    static final int COLORCODE_NUM_BINS = 64;

    public CBIRSystem() {
        setTitle("Content-Based Image Retrieval System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panel (holds the main content)
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Create dropdown menu for method selection (intensity or color code)
        JPanel methodPanel = new JPanel();
        String[] methods = {"Intensity Method", "Color Code Method"};
        JComboBox<String> methodComboBox = new JComboBox<>(methods);
        methodComboBox.setFont(new Font("Serif", Font.BOLD, 18));
        methodComboBox.addActionListener(e -> {
            selectedMethod = (String) methodComboBox.getSelectedItem(); // Update the selected method
        });
        methodPanel.add(methodComboBox); // add dropdown
        mainPanel.add(methodPanel, BorderLayout.NORTH); // add method panel to main panel

        // Load images from the "images" directory
        loadImages();

        // LEFT SIDE: Create image grid panel (with a black border)
        imageGridPanel = new JPanel(new GridLayout(0, 5)); // 5 images per row
        imageGridPanel.setBorder(LineBorder.createBlackLineBorder());
        mainPanel.add(imageGridPanel, BorderLayout.WEST);

        // RIGHT SIDE: Create selected image panel (with header)
        queryImagePanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Query Image", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Serif", Font.BOLD, 30));

        queryImageLabel = new JLabel();
        queryImageLabel.setPreferredSize(new Dimension(300, 200)); // Sets preferred size for selected image
        queryImageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center selected image
        queryImagePanel.add(headerLabel, BorderLayout.NORTH);
        queryImagePanel.add(queryImageLabel, BorderLayout.CENTER);

        // Create a panel for the reset and close buttons
        JPanel buttonPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        JButton closeButton = new JButton("Close");
        resetButton.setFont(new Font("Serif", Font.PLAIN, 15));
        closeButton.setFont(new Font("Serif", Font.PLAIN, 15));

        // Action --> Reset button
        resetButton.addActionListener(e -> {
            queryImageLabel.setIcon(null); // Clear the selected image
            queryImage = null; // Reset the query image
        });

        // Action --> Close button
        closeButton.addActionListener(e -> System.exit(0));

        // Add buttons to the button panel
        buttonPanel.add(resetButton);
        buttonPanel.add(closeButton);

        // Add button panel to the right side
        queryImagePanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(queryImagePanel, BorderLayout.EAST);

        // Navigation panel (page control)
        JPanel navigationPanel = new JPanel();
        pageLabel = new JLabel("Page: " + (currentPage + 1));
        pageLabel.setFont(new Font("Serif", Font.PLAIN, 17));

        previousButton = new JButton("Previous");
        previousButton.setFont(new Font("Serif", Font.PLAIN, 15));
        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Serif", Font.PLAIN, 15));

        // Action --> Previous button
        previousButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                displayImages();
            }
        });

        // Action --> Next button
        nextButton.addActionListener(e -> {
            if (currentPage < (Math.min(MAX_PAGES, (imagePaths.length + IMAGES_PER_PAGE - 1) / IMAGES_PER_PAGE) - 1)) {
                currentPage++;
                displayImages();
            }
        });

        // Add buttons and page label to the navigation panel
        navigationPanel.add(previousButton);
        navigationPanel.add(pageLabel);
        navigationPanel.add(nextButton);
        mainPanel.add(navigationPanel, BorderLayout.SOUTH);

        // Display the first page of images
        displayImages();
        setVisible(true);
    }

    // Load image file paths from the "images" directory
    private void loadImages() {
        File imagesDir = new File("images"); // Path to the images folder
        if (imagesDir.exists() && imagesDir.isDirectory()) {
            imagePaths = imagesDir.list((dir, name) -> name.endsWith(".jpg"));
        }
    }

    // Display the images for the current page in the image grid panel
    private void displayImages() {
        imageGridPanel.removeAll(); // Clear current images
        int startIndex = currentPage * IMAGES_PER_PAGE;
        int endIndex = Math.min(startIndex + IMAGES_PER_PAGE, imagePaths.length);

        // Update page label to reflect the current page number
        pageLabel.setText("Page: " + (currentPage + 1));

        // Load images into the grid layout
        for (int i = startIndex; i < endIndex; i++) {
            // Create a panel to hold each image and its filename
            JPanel imagePanel = new JPanel();
            imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS)); // Vertical layout
            imagePanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Add spacing around each image panel

            JLabel imageLabel = new JLabel(resizeImage("images/" + imagePaths[i], 80, 80)); // Resize to a smaller size
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the grid images
            JLabel filenameLabel = new JLabel(imagePaths[i]); // Label for the image filename
            filenameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            filenameLabel.setFont(new Font("Serif", Font.PLAIN, 10)); // Set small font size

            // Add a mouse listener to select the query image
            final String imagePath = imagePaths[i]; // Make image path final for use in inner class
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Set the selected image as the query image
                    queryImageLabel.setIcon(resizeImage("images/" + imagePath, 300, 200));

                    try {
                        // Load the query image
                        queryImage = ImageIO.read(new File("images/" + imagePath));

                        // Calculate the histogram based on the selected method
                        int[] histogram;
                        if (selectedMethod.equals("Intensity Method")) {
                            histogram = Histograms.intensityMethod(queryImage);
                            System.out.println("Intensity Histogram: " + Arrays.toString(histogram));

                        } else {
                            histogram = Histograms.colorCodeMethod(queryImage);
                            System.out.println("CC Histogram: " + Arrays.toString(histogram));

                        }

                        // Store the histogram of the query image
                        histograms.put(imagePath, histogram);
                        // Sort the images based on distance from the query image
                        sortImages(histogram);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // Add components to the image panel
            imagePanel.add(imageLabel); // Add the image to the image panel
            imagePanel.add(filenameLabel); // Add the filename label below the image
            imageGridPanel.add(imagePanel); // Add the image panel to the grid
        }

        imageGridPanel.revalidate(); // Refresh grid panel
        imageGridPanel.repaint();
    }

    // Sort DISTANCE array and then reload grid images
    private void sortImages(int[] queryHistogram) {
        // Create a map to hold image paths and their corresponding distances
        Map<String, Double> distanceMap = new HashMap<>();

        for (String imagePath : imagePaths) {
            // Calculate the histogram for the current image
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File("images/" + imagePath));
            } catch (Exception e) {
                e.printStackTrace();
                continue; // Skip this image if there's an error
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

            // Calculate the Manhattan distance
            double distance = Histograms.manhattanDistance(queryHistogram, imageHistogram, numBins);
            distanceMap.put(imagePath, distance);
        }

        // Sort the images by distance
        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(distanceMap.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue()); // Sort by distance

        // Update imagePaths to reflect the sorted order
        imagePaths = new String[sortedEntries.size()];
        for (int i = 0; i < sortedEntries.size(); i++) {
            imagePaths[i] = sortedEntries.get(i).getKey();
        }

        // Redisplay the images in the grid based on the new sorted order
        displayImages();
    }

    // Method that resizes images while maintaining aspect ratio
    private Icon resizeImage(String imagePath, int width, int height) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage(); // Get the original image
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH); // Scale the image
        return new ImageIcon(scaledImg); // Return the resized image as an Icon
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CBIRSystem::new); // Start the application
    }
}
