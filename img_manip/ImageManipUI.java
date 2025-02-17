import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * TO DO :
 * Add adjustable grid presets
 * Add convolution presets
 * Add titles
 * Add Download convolution button
 */

public class ImageManipUI extends JFrame {

    JPanel mainPanel;

    JPanel combinedPanel;

    JPanel imagePanel;

    JPanel kernelPanel;

    JPanel buttonPanel;

    JLabel titleLabel;

    JLabel sourceImageLabel;

    JLabel productImageLabel;

    JButton chooseFileButton;

    JButton convolutionButton;

    JTable kernelTable;

    BufferedImage sourceImage;

    double[][] kernelData;

    public ImageManipUI() {

        // Set main frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 360);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        initializeImagePanel();
        initializeButtonsPanel();

        combinePanels();

        mainPanel.add(combinedPanel);
        add(mainPanel);

        setVisible(true);

    }

    private void combinePanels() {
        combinedPanel = new JPanel();
        combinedPanel.setLayout(new BorderLayout(20, 20));

        combinedPanel.add(imagePanel, BorderLayout.WEST);
        combinedPanel.add(kernelPanel, BorderLayout.CENTER);
        combinedPanel.add(buttonPanel, BorderLayout.EAST);

    }

    /**
     * Initializes kernel table UI.
     * 
     * @param kernel
     */
    private void initializeKernelTable(double[][] kernel) {
        kernelPanel = new JPanel(new BorderLayout());
        kernelPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        int width = kernel[0].length;
        int height = kernel.length;

        kernelTable = new JTable(width, height);

        kernelTable.setBounds(40, 40, 270, 270);
        kernelTable.setBorder(new LineBorder(Color.BLACK, 1));

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                kernelTable.setValueAt(kernel[i][j], i, j);
            }
            kernelTable.setRowHeight(kernelTable.getHeight() / height);
        }

        kernelPanel.add(kernelTable);
    }

    /**
     * Sets source image panel.
     */
    private void initializeImagePanel() {
        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));

        double[][] kernel = new double[3][3];

        for (int i = 0; i < kernel.length; i++) {
            for (int k = 0; k < kernel[0].length; k++) {
                kernel[i][k] = i - 1;
            }
        }
        // for testing please move
        initializeKernelTable(kernel);
        updateKernelData();

        // take source image and convolute
        try {
            BufferedImage sourceBufferedImage = ImageIO.read(new File("SampleImages/happyTiktok.png"));
            sourceImageLabel = new JLabel(new ImageIcon(sourceBufferedImage));
            BufferedImage productBufferedImage = convoluteImage(sourceBufferedImage,
                    kernelData);
            productImageLabel = new JLabel(new ImageIcon(productBufferedImage));

        } catch (Exception e) {
            System.err.print(e);
        }

        imagePanel.add(sourceImageLabel);
        imagePanel.add(Box.createVerticalGlue());
        imagePanel.add(productImageLabel);

    }

    /**
     * Initializes buttons panel
     * 
     */
    private void initializeButtonsPanel() {
        buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        chooseFileButton = new JButton("Select File");
        convolutionButton = new JButton("Convolution");

        // Get file from user
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            // Sets filter to only get image files
            fileChooser.addChoosableFileFilter(
                    new FileNameExtensionFilter("Image Files", ImageIO.getReaderFileSuffixes()));
            fileChooser.setAcceptAllFileFilterUsed(false);
            int r = fileChooser.showOpenDialog(fileChooser);

            if (r == JFileChooser.APPROVE_OPTION) {
                try {
                    File sourceImageFile = fileChooser.getSelectedFile();
                    sourceImage = ImageIO.read(sourceImageFile);
                    updateSourceImagePanel();
                } catch (Exception err) {
                    System.out.println(err);
                }
            }
        });

        // Set convolution button
        convolutionButton.addActionListener(e -> {
            if (sourceImage == null) {
                try {
                    updateKernelData();
                    productImageLabel.setIcon(new ImageIcon(resizeImage(
                            convoluteImage(ImageIO.read(new File("SampleImages/happyTiktok.png")), kernelData))));

                } catch (Exception err) {
                    System.err.println(err);
                }
                // JOptionPane.showMessageDialog(this, "Select an image file first.", "Error",
                // JOptionPane.ERROR_MESSAGE);
            } else {
                updateKernelData();
                BufferedImage productImage = convoluteImage(sourceImage, kernelData);

                // Display productImage
                productImageLabel.setIcon(new ImageIcon(resizeImage(productImage)));
            }

        });

        buttonPanel.add(new JLabel("Controls:"));
        buttonPanel.add(chooseFileButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(convolutionButton);
        buttonPanel.add(new JLabel("Convolution Presets:"));
    }

    private void updateSourceImagePanel() {
        try {
            ImageIcon sourceImageIcon = new ImageIcon(resizeImage(sourceImage));

            sourceImageLabel.setIcon(sourceImageIcon);
        } catch (Exception err) {
            System.out.println(err);
        }
    }

    /**
     * Gets kernel data from Java swing table and sets it to class field kernelData.
     * 
     */
    private void updateKernelData() {
        double[][] kern = new double[kernelTable.getRowCount()][kernelTable.getColumnCount()];
        System.out.println("rows: " + kernelTable.getRowCount() + " cols: " + kernelTable.getColumnCount());
        for (int i = 0; i < kern.length; i++) {
            for (int j = 0; j < kern[0].length; j++) {
                try {
                    if (kernelTable.getModel().getValueAt(i, j) instanceof String)
                        kern[i][j] = Double.parseDouble((String) kernelTable.getModel().getValueAt(i, j));
                    if (kernelTable.getModel().getValueAt(i, j) instanceof Number)
                        kern[i][j] = (Double) kernelTable.getModel().getValueAt(i, j);

                } catch (Exception err) {
                    // DO SOMETHING
                }
                System.out.print(kern[i][j] + ", ");

            }
            System.out.println();
        }
        kernelData = kern;
    }

    /**
     * Resizes image to 150x150 keeping aspect ratio
     * 
     * @param originalImage
     * @return
     */
    private BufferedImage resizeImage(BufferedImage originalImage) {

        Dimension newImageDimension = getAspectRatio(originalImage, new Dimension(120, 120));
        BufferedImage resizedImage = new BufferedImage((int) newImageDimension.getWidth(),
                (int) newImageDimension.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();

        g.drawImage(originalImage, 0, 0, (int) newImageDimension.getWidth(),
                (int) newImageDimension.getHeight(), null);
        g.dispose();

        return resizedImage;
    }

    /**
     * Helper method to resizeImage which calculates the width and height to keep
     * aspect ratio given the boundary.
     * 
     * @param resizableImage
     * @param boundary
     * @return
     */
    private Dimension getAspectRatio(BufferedImage resizableImage, Dimension boundary) {
        double newWidth = boundary.getWidth() / resizableImage.getWidth();
        double newHeight = boundary.getHeight() / resizableImage.getHeight();
        double ratio = Math.min(newWidth, newHeight);

        return new Dimension((int) (resizableImage.getWidth() * ratio), (int) (resizableImage.getHeight() * ratio));
    }

    /**
     * Takes in a BufferedImage and a set kernel and performs a convolution
     * operation with the kernel.
     * Function converts the BufferedImage into a 2d array of Color for easier
     * processing.
     * 
     * @param sourceImage
     * @param kernel
     * @return BufferedImage created from the convoluted RGB values of sourceImage.
     */
    public static BufferedImage convoluteImage(BufferedImage sourceImage, double[][] kernel) {
        // BufferedImage of the convoluted image after processing.
        BufferedImage productImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        // RGB 2d array of the source image.
        Color[][] sourceImageRGB = new Color[sourceImage.getWidth()][sourceImage.getHeight()];

        // Converts source image to 2d Color array.
        for (int i = 0; i < sourceImage.getHeight(); i++) {
            for (int k = 0; k < sourceImage.getWidth(); k++) {
                Color setColor = new Color(sourceImage.getRGB(k, i));
                sourceImageRGB[k][i] = setColor;
            }
        }

        // Product RGB values.
        Color[][] convolutedRGBValues = new Color[sourceImageRGB.length][sourceImageRGB[0].length];

        // Goes across each pixel and returns the convoluted pixel value. Sets it to the
        // 2d array.
        for (int i = 0; i < convolutedRGBValues[0].length; i++) {
            for (int j = 0; j < convolutedRGBValues.length; j++) {
                convolutedRGBValues[j][i] = convolutionAt(sourceImageRGB, kernel, j, i);
            }
        }

        // Convert product RGB values to a BufferedImage
        for (int i = 0; i < sourceImage.getHeight(); i++) {
            for (int k = 0; k < sourceImage.getWidth(); k++) {
                productImage.setRGB(k, i, convolutedRGBValues[k][i].getRGB());
            }
        }
        return productImage;
    }

    /**
     * Returns convoluted color given the kernel at the specified index.
     * Helper method for convolution()
     * 
     * @param img
     * @param x
     * @param y
     * @return
     */
    public static Color convolutionAt(Color[][] img, double[][] kernel, int x, int y) {
        double redValue = 0;
        double greenValue = 0;
        double blueValue = 0;

        // For readability
        int kernelWidth = kernel.length;
        int kernelHeight = kernel[0].length;

        // calculate sum of all RGB
        for (int i = kernelHeight / 2 * -1; i <= kernelHeight / 2; i++) { // height
            for (int j = kernelWidth / 2 * -1; j <= kernelWidth / 2; j++) { // width
                // do not compute out of bounds
                if (i + y >= 0 && j + x >= 0 && i + y < img[0].length && j + x < img.length) {

                    redValue += kernel[i + kernelWidth / 2][j + kernelHeight / 2] * img[j + x][i + y].getRed();
                    blueValue += kernel[i + kernelWidth / 2][j + kernelHeight / 2] * img[j + x][i + y].getBlue();
                    greenValue += kernel[i + kernelWidth / 2][j + kernelHeight / 2] * img[j + x][i + y].getGreen();
                }

            }
        }

        // Move RGB values to 0 - 255
        redValue = redValue / 2 + 127;
        greenValue = greenValue / 2 + 127;
        blueValue = blueValue / 2 + 127;

        // Clamp values
        if (redValue < 0)
            redValue = 0;
        if (blueValue < 0)
            blueValue = 0;
        if (greenValue < 0)
            greenValue = 0;
        if (redValue > 255)
            redValue = 255;
        if (greenValue > 255)
            greenValue = 255;
        if (blueValue > 255)
            blueValue = 255;

        return new Color((int) redValue, (int) greenValue, (int) blueValue);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageManipUI::new);
    }
}
