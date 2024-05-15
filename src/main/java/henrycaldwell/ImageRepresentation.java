package henrycaldwell;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Manages an image's pixel nodes for editing operations such as seam removal.
 */
public class ImageRepresentation {
    // The current image being manipulated.
    private BufferedImage image;
    // The root node of the pixel node 'grid'. Acts as the entry point to traverse the 'grid'.
    private PixelNode root;
    // Counter for edits made to help manage saved image files.
    private int editCounter;

    /**
     * Initializes with an image, setting up nodes and calculating their energies.
     * @param image Image to manipulate.
     */
    public ImageRepresentation(BufferedImage image) {
        this.image = image;
        this.root = initializeNodes(image);
        calculateEnergyForNodes();
    }

    /**
     * Constructs a 'grid' of pixel nodes from an image.
     * @param image Image to convert into nodes.
     * @return The root node of the 'grid'.
     */
    private PixelNode initializeNodes(BufferedImage image) {
        PixelNode firstNode = null;
        PixelNode aboveRowFirstNode = null;

        for (int y = 0; y < image.getHeight(); y++) {
            PixelNode prevNode = null;
            PixelNode rowFirstNode = null;

            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                PixelNode currentNode = new PixelNode(color);
                currentNode.brightness = br(color);

                if (x == 0) {
                    rowFirstNode = currentNode;
                    if (y == 0) {
                        firstNode = currentNode;
                    } else {
                        aboveRowFirstNode.down = currentNode;
                        currentNode.up = aboveRowFirstNode;
                    }
                } else {
                    prevNode.right = currentNode;
                    currentNode.left = prevNode;
                }

                if (y > 0 && x > 0) {
                    PixelNode aboveNode = getAboveNode(aboveRowFirstNode, x);
                    aboveNode.down = currentNode;
                    currentNode.up = aboveNode;
                }

                prevNode = currentNode;
            }

            aboveRowFirstNode = rowFirstNode;
        }

        return firstNode;
    }

    /**
     * Finds the node directly above a specified node in the 'grid'.
     * @param root Starting node for the search.
     * @param x Horizontal position of the target node.
     * @return Node above the specified position.
     */
    private PixelNode getAboveNode(PixelNode root, int x) {
        PixelNode node = root;

        for (int i = 0; i < x; i++) {
            node = node.right;
        }

        return node;
    }

    /**
     * Updates energy values for all nodes based on their surroundings.
     */
    public void calculateEnergyForNodes() {
        PixelNode rowStart = root;

        while (rowStart != null) {
            PixelNode node = rowStart;

            while (node != null) {
                node.energy = calculateEnergy(node);
                node = node.right;
            }

            rowStart = rowStart.down;
        }
    }

    /**
     * Calculates energy for a node considering adjacent node brightnesses.
     *
     * @param node Node to calculate energy for.
     * @return Calculated energy.
     */
    private double calculateEnergy(PixelNode node) {
        double leftBrightness = (node.left != null) ? node.left.brightness : node.brightness;
        double rightBrightness = (node.right != null) ? node.right.brightness : node.brightness;
        double upBrightness = (node.up != null) ? node.up.brightness : node.brightness;
        double downBrightness = (node.down != null) ? node.down.brightness : node.brightness;

        double upperLeftBrightness = (node.up != null && node.up.left != null) ? node.up.left.brightness : node.brightness;
        double upperRightBrightness = (node.up != null && node.up.right != null) ? node.up.right.brightness : node.brightness;
        double lowerLeftBrightness = (node.down != null && node.down.left != null) ? node.down.left.brightness : node.brightness;
        double lowerRightBrightness = (node.down != null && node.down.right != null) ? node.down.right.brightness : node.brightness;

        double horizEnergy = (upperLeftBrightness + 2 * leftBrightness + lowerLeftBrightness) - (upperRightBrightness + 2 * rightBrightness + lowerRightBrightness);
        double vertEnergy = (upperLeftBrightness + 2 * upBrightness + upperRightBrightness) - (lowerLeftBrightness + 2 * downBrightness + lowerRightBrightness);

        return Math.sqrt(horizEnergy * horizEnergy + vertEnergy * vertEnergy);
    }

    /**
     * Calculates average brightness of a color.
     * @param color Color to calculate brightness for.
     * @return Average brightness.
     */
    private double br(Color color) {
        return (double) ((color.getRed() + color.getGreen() + color.getBlue()) / 3.0);
    }

    /**
     * Removes a seam from the image, updating node connections.
     * @param seam List of nodes forming the seam to be removed.
     */
    public void removeSeam(List<PixelNode> seam) {
        for (int i = 0; i < seam.size(); i++) {
            PixelNode seamNode = seam.get(i);
            PixelNode nextSeamNode = (i + 1 < seam.size()) ? seam.get(i + 1) : null;

            if (seamNode == root) {
                root = seamNode.right;
            }

            if(seamNode.left != null) {
                seamNode.left.right = seamNode.right;
            }

            if(seamNode.right != null) {
                seamNode.right.left = seamNode.left;
            }

            if (seamNode.down != null) {
                if(seamNode.down.left == nextSeamNode) {
                    seamNode.left.down = seamNode.down;
                    nextSeamNode.right.up = nextSeamNode.up;
                } else if(seamNode.down.right == nextSeamNode) {
                    seamNode.right.down = seamNode.down;
                    nextSeamNode.left.up = nextSeamNode.up;
                }
            }
        }
    }

    /**
     * Restores a previously removed seam.
     * @param seam List of nodes in the removed seam.
     */
    public void undoSeam(List<PixelNode> seam) {
        for (PixelNode node : seam) {
            if (node.left != null) {
                node.left.right = node;
            }

            if (node.right != null) {
                node.right.left = node;
            }

            if (node.up != null) {
                node.up.down = node;
            }

            if (node.down != null) {
                node.down.up = node;
            }

            if (node.left == null && node.up == null) {
                setRoot(node);
            }
        }
    }

    /**
     * Creates a copy of the current image.
     * @return Copy of the current image.
     */
    public BufferedImage deepCopyImage() {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixelRGB = image.getRGB(x, y);
                copy.setRGB(x, y, pixelRGB);
            }
        }

        return copy;
    }

    /**
     * Updates the image based on current pixel node 'grid'.
     */
    public void updateImage() {
        if (root == null) {
            System.out.println("No image data available.");
            return;
        }

        int newWidth = calculateWidth();
        int newHeight = calculateHeight();
        BufferedImage updatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        PixelNode rowStart = root;
        
        for (int y = 0; rowStart != null; y++) {
            PixelNode node = rowStart;

            for (int x = 0; node != null; x++) {
                updatedImage.setRGB(x, y, node.color.getRGB());
                node = node.right;
            }

            rowStart = rowStart.down;
        }

        this.image = updatedImage;
    }

    /**
     * Calculates the width based on the pixel 'grid'.
     * @return Image width.
     */
    private int calculateWidth() {
        int width = 0;
        PixelNode currentNode = root;

        while (currentNode != null) {
            width++;
            currentNode = currentNode.right;
        }

        return width;
    }

    /**
     * Calculates the height based on the pixel 'grid'.
     * @return Image height.
     */
    private int calculateHeight() {
        int height = 0;
        PixelNode currentNode = root;

        while (currentNode != null) {
            height++;
            currentNode = currentNode.down;
        }

        return height;
    }

    /**
     * Saves the image to a file, naming based on edit count.
     * @param image Image to save.
     */
    public void saveImage(BufferedImage image) {
        try {
            File outputFile = new File("target/previewIMG" + editCounter + ".png");
            ImageIO.write(image, "png", outputFile);
            System.out.println("Edited image saved successfully to: target/previewIMG.png");
            editCounter++;
        } catch (IOException e) {
            System.err.println("Error saving the image: " + e.getMessage());
        }
    }

    /**
     * Returns the root node of the pixel 'grid'.
     * @return Root node.
     */
    public PixelNode getRoot() {
        return root;
    }

    /**
     * Sets the root of the pixel node 'grid'.
     * @param root New root node.
     */
    public void setRoot(PixelNode root) {
        this.root = root;
    }

    /**
     * Retrieves the current manipulated image.
     * @return Current image.
     */
    public BufferedImage getImage() {
        return image;
    }
}
