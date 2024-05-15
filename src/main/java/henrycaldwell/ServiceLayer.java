package henrycaldwell;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Manages image manipulation tasks like finding and removing seams and supports undoing changes.
 */
public class ServiceLayer {
    // The image representation that this service layer manipulates.
    private ImageRepresentation imageRep;
    //The last seam that was found and highlighted in the image.
    private List<PixelNode> lastFoundSeam;
    // A history of edit commands that have been applied to the image.
    private Stack<EditCommand> editHistory = new Stack<>();

    /**
     * Initializes with an image loaded from the provided file path.
     * @param filePath The path to the image file.
     */
    public ServiceLayer(String filePath) {
        this.imageRep = new ImageRepresentation(loadImage(filePath));
    }

    /**
     * Loads an image from the file path.
     * @param filePath The file path.
     * @return The loaded BufferedImage.
     */
    private BufferedImage loadImage(String filePath) {
        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image from path: " + filePath, e);
        }
    }

    /**
     * Finds and highlights a seam based on energy or blueness.
     * @param isLowestEnergy If true, finds the lowest energy seam, otherwise the bluest seam.
     */
    public void findAndHighlightSeam(boolean isLowestEnergy) {
        if(imageRep.getRoot().right != null) {
            BufferedImage image = imageRep.deepCopyImage();
            lastFoundSeam = isLowestEnergy ? findLowestEnergySeam() : findBluestSeam();
            Color highlightColor = isLowestEnergy ? Color.RED : Color.BLUE;
            PixelNode rowStart = imageRep.getRoot();
            int y = 0;

            while (rowStart != null) {
                PixelNode current = rowStart;
                int x = 0;

                while (current != null) {
                    if (lastFoundSeam.contains(current)) {
                        image.setRGB(x, y, highlightColor.getRGB());
                    }

                    current = current.right;
                    x++;
                }

                rowStart = rowStart.down;
                y++;
            }

            imageRep.saveImage(image);
        } else {
            System.out.println("Only one column remains. You can not create an empty image.");
        }
    }

    /**
     * Finds the lowest energy seam in the image.
     * @return The seam as a list of PixelNodes.
     */
    public List<PixelNode> findLowestEnergySeam() {
        Map<PixelNode, Double> cumulativeEnergy = new HashMap<>();
        Map<PixelNode, PixelNode> edgeTo = new HashMap<>();
        PixelNode start = imageRep.getRoot();

        if (start == null) {
            return new ArrayList<>();
        }

        PixelNode node = start;

        while (node != null) {
            cumulativeEnergy.put(node, node.energy);
            node = node.right;
        }

        PixelNode rowStart = start.down;

        while (rowStart != null) {
            node = rowStart;

            while (node != null) {
                PixelNode upNode = node.up;
                PixelNode leftNode = (upNode != null) ? upNode.left : null;
                PixelNode rightNode = (upNode != null) ? upNode.right : null;

                List<PixelNode> predecessors = Arrays.asList(leftNode, upNode, rightNode);
                double minEnergy = Double.MAX_VALUE;
                PixelNode minNode = null;

                for (PixelNode pred : predecessors) {
                    if (pred != null) {
                        double energy;
                        energy = cumulativeEnergy.getOrDefault(pred, Double.MAX_VALUE);
                        if (energy < minEnergy) {
                            minEnergy = energy;
                            minNode = pred;
                        }
                    }
                }

                if (minNode != null) {
                    cumulativeEnergy.put(node, node.energy + minEnergy);
                    edgeTo.put(node, minNode);
                }

                node = node.right;
            }

            rowStart = rowStart.down;
        }

        double minTotalEnergy = Double.MAX_VALUE;
        PixelNode minNode = null;
        node = getLastRowStart(imageRep.getRoot());
        while (node != null) {
            double energy = cumulativeEnergy.getOrDefault(node, Double.MAX_VALUE);

            if (energy < minTotalEnergy) {
                minTotalEnergy = energy;
                minNode = node;
            }

            node = node.right;
        }

        List<PixelNode> seam = new ArrayList<>();

        while (minNode != null) {
            seam.add(minNode);
            minNode = edgeTo.get(minNode);
        }

        Collections.reverse(seam);
        return seam;
    }

    /**
     * Finds the seam with the highest blueness in the image.
     * @return A list of PixelNode objects representing the seam.
     */
    public List<PixelNode> findBluestSeam() {
        Map<PixelNode, Integer> cumulativeBlueness = new HashMap<>();
        Map<PixelNode, PixelNode> edgeTo = new HashMap<>();
        PixelNode start = imageRep.getRoot();

        if (start == null) {
            return new ArrayList<>();
        }

        PixelNode node = start;

        while (node != null) {
            cumulativeBlueness.put(node, node.color.getBlue());
            node = node.right;
        }

        PixelNode rowStart = start.down;

        while (rowStart != null) {
            node = rowStart;

            while (node != null) {
                PixelNode upNode = node.up;
                PixelNode leftNode = (upNode != null) ? upNode.left : null;
                PixelNode rightNode = (upNode != null) ? upNode.right : null;

                List<PixelNode> predecessors = Arrays.asList(leftNode, upNode, rightNode);
                int maxBlueness = Integer.MIN_VALUE;
                PixelNode maxNode = null;

                for (PixelNode pred : predecessors) {
                    if (pred != null) {
                        int blueness = cumulativeBlueness.getOrDefault(pred, 0) + node.color.getBlue();
                        if (blueness > maxBlueness) {
                            maxBlueness = blueness;
                            maxNode = pred;
                        }
                    }
                }

                if (maxNode != null) {
                    cumulativeBlueness.put(node, maxBlueness);
                    edgeTo.put(node, maxNode);
                }

                node = node.right;
            }

            rowStart = rowStart.down;
        }

        List<PixelNode> seam = new ArrayList<>();
        PixelNode lastRowStart = getLastRowStart(start);
        PixelNode maxBluenessNode = lastRowStart;

        for (PixelNode tmpNode = lastRowStart; tmpNode != null; tmpNode = tmpNode.right) {
            if (cumulativeBlueness.get(tmpNode) > cumulativeBlueness.get(maxBluenessNode)) {
                maxBluenessNode = tmpNode;
            }
        }

        for (PixelNode tmpNode = maxBluenessNode; tmpNode != null; tmpNode = edgeTo.get(tmpNode)) {
            seam.add(0, tmpNode);
        }

        return seam;
    }

    /**
     * Finds the starting PixelNode of the last row.
     * @param start The root PixelNode.
     * @return The starting PixelNode of the last row.
     */
    private PixelNode getLastRowStart(PixelNode start) {
        PixelNode lastRowStart = start;

        while (lastRowStart.down != null) {
            lastRowStart = lastRowStart.down;
        }
        
        return lastRowStart;
    }

    /**
     * Removes the last found seam and updates the image.
     */
    public void removeSeam() {
        if (lastFoundSeam != null && !lastFoundSeam.isEmpty()) {
            EditCommand removeCommand = new RemoveSeamCommand(this.imageRep, new ArrayList<>(lastFoundSeam));
            removeCommand.execute();
            editHistory.push(removeCommand);
        } else {
            System.out.println("No seam has been highlighted yet. Please highlight a seam before trying to delete.");
        }
    }

    /**
     * Undoes the last edit if there is any.
     */
    public void undoLastEdit() {
        if (!editHistory.isEmpty()) {
            EditCommand lastEdit = editHistory.pop();
            lastEdit.undo();
        } else {
            System.out.println("Nothing left to undo.");
        }
    }

    /**
     * Implements the EditCommand interface to support undoable seam removal operations.
     */
    private class RemoveSeamCommand implements EditCommand {
        private ImageRepresentation targetImage;
        private List<PixelNode> seam;

        /**
         * Initializes a command to remove a specified seam from an image.
         * @param targetImage The ImageRepresentation on which operations are performed.
         * @param seam The seam to be removed, represented as a list of PixelNodes.
         */
        public RemoveSeamCommand(ImageRepresentation targetImage, List<PixelNode> seam) {
            this.targetImage = targetImage;
            this.seam = new ArrayList<>(seam);
        }

        /**
         * Removes the specified seam from the image and updates the display.
         */
        @Override
        public void execute() {
            targetImage.removeSeam(seam);
            targetImage.updateImage();
            targetImage.saveImage(targetImage.getImage());
            targetImage.calculateEnergyForNodes();
            lastFoundSeam = null;
        }

        /**
         * Restores the image to its state before the last seam removal.
         */
        @Override
        public void undo() {
            targetImage.undoSeam(seam);
            targetImage.updateImage();
            targetImage.saveImage(targetImage.getImage());
            targetImage.calculateEnergyForNodes();
        }
    }

    /**
     * Retrieves the current image representation.
     * @return The ImageRepresentation for the purpose of testing.
     */
    public ImageRepresentation getImageRep() {
        return imageRep;
    }

    /**
     * Retrieves the current last found seam.
     * @return The lastFoundSeam for the purpose of testing.
     */
    public List<PixelNode> getLastFoundSeam() {
        return lastFoundSeam;
    }

    /**
     * Retrieves the edit history stack.
     * @return The editHistory for the purpose of testing.
     */
    public Stack<EditCommand> getEditHistory() {
        return editHistory;
    }
}
