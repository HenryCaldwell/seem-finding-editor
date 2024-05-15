package henrycaldwell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class ImageRepresentationTest {
    private ImageRepresentation imageRepresentation;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        testImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);

        testImage.setRGB(0, 0, Color.RED.getRGB());      // Top-left
        testImage.setRGB(1, 0, Color.GREEN.getRGB());    // Top-center
        testImage.setRGB(2, 0, Color.BLUE.getRGB());     // Top-right
        testImage.setRGB(0, 1, Color.YELLOW.getRGB());   // Middle-left
        testImage.setRGB(1, 1, Color.ORANGE.getRGB());   // Center
        testImage.setRGB(2, 1, Color.CYAN.getRGB());     // Middle-right
        testImage.setRGB(0, 2, Color.MAGENTA.getRGB());  // Bottom-left
        testImage.setRGB(1, 2, Color.PINK.getRGB());     // Bottom-center
        testImage.setRGB(2, 2, Color.GRAY.getRGB());     // Bottom-right

        imageRepresentation = new ImageRepresentation(testImage);
    }

    @Test
    void initializeNodesGridTest() {
        PixelNode root = imageRepresentation.getRoot();

        assertThat(root).isNotNull();
        assertThat(root.color).isEqualTo(new Color(testImage.getRGB(0, 0)));
        assertThat(root.right.color).isEqualTo(new Color(testImage.getRGB(1, 0)));
        assertThat(root.right.right.color).isEqualTo(new Color(testImage.getRGB(2, 0)));

        PixelNode secondRow = root.down;
        assertThat(secondRow.color).isEqualTo(new Color(testImage.getRGB(0, 1)));
        assertThat(secondRow.right.color).isEqualTo(new Color(testImage.getRGB(1, 1)));
        assertThat(secondRow.right.right.color).isEqualTo(new Color(testImage.getRGB(2, 1)));

        PixelNode thirdRow = secondRow.down;
        assertThat(thirdRow.color).isEqualTo(new Color(testImage.getRGB(0, 2)));
        assertThat(thirdRow.right.color).isEqualTo(new Color(testImage.getRGB(1, 2)));

        PixelNode lastNode = thirdRow.right.right;
        assertThat(lastNode.color).isEqualTo(new Color(testImage.getRGB(2, 2)));

        assertThat(lastNode.right).isNull();
        assertThat(lastNode.down).isNull();
        assertThat(lastNode.up).isNotNull();
        assertThat(lastNode.left).isNotNull();
    }

    @Test
    void calculateEnergyForNodesTest() {
        imageRepresentation.calculateEnergyForNodes();

        PixelNode rowStart = imageRepresentation.getRoot();
        boolean energyCalculatedCorrectly = true;

        while (rowStart != null) {
            PixelNode current = rowStart;

            while (current != null) {
                boolean surroundedBySameColor = isSurroundedBySameColor(current);

                if (surroundedBySameColor && current.energy != 0) {
                    energyCalculatedCorrectly = false;
                    break;
                } else if (!surroundedBySameColor && current.energy <= 0) {
                    energyCalculatedCorrectly = false;
                    break;
                }

                current = current.right;
            }

            if (!energyCalculatedCorrectly) break;
            
            rowStart = rowStart.down;
        }

        assertThat(energyCalculatedCorrectly).isTrue();
    }

    private boolean isSurroundedBySameColor(PixelNode node) {
        Color nodeColor = node.color;

        if ((node.left != null && !node.left.color.equals(nodeColor)) ||
                (node.right != null && !node.right.color.equals(nodeColor)) ||
                (node.up != null && !node.up.color.equals(nodeColor)) ||
                (node.down != null && !node.down.color.equals(nodeColor))) {
            return false;
        }

        if ((node.up != null && node.up.left != null && !node.up.left.color.equals(nodeColor)) ||
                (node.up != null && node.up.right != null && !node.up.right.color.equals(nodeColor)) ||
                (node.down != null && node.down.left != null && !node.down.left.color.equals(nodeColor)) ||
                (node.down != null && node.down.right != null && !node.down.right.color.equals(nodeColor))) {
            return false;
        }

        return true;
    }

    @Test
    void deepCopyImageTest() {
        BufferedImage copiedImage = imageRepresentation.deepCopyImage();
        assertThat(copiedImage).isNotSameAs(testImage);
        assertThat(copiedImage.getRGB(0, 0)).isEqualTo(Color.RED.getRGB());
        assertThat(copiedImage.getRGB(1, 0)).isEqualTo(Color.GREEN.getRGB());
        assertThat(copiedImage.getRGB(2, 0)).isEqualTo(Color.BLUE.getRGB());
        assertThat(copiedImage.getRGB(0, 1)).isEqualTo(Color.YELLOW.getRGB());
        assertThat(copiedImage.getRGB(1, 1)).isEqualTo(Color.ORANGE.getRGB());
        assertThat(copiedImage.getRGB(2, 1)).isEqualTo(Color.CYAN.getRGB());
        assertThat(copiedImage.getRGB(0, 2)).isEqualTo(Color.MAGENTA.getRGB());
        assertThat(copiedImage.getRGB(1, 2)).isEqualTo(Color.PINK.getRGB());
        assertThat(copiedImage.getRGB(2, 2)).isEqualTo(Color.GRAY.getRGB());
        assertThat(copiedImage.getWidth()).isEqualTo(3);
        assertThat(copiedImage.getHeight()).isEqualTo(3);
    }

    @Test
    void removeSeamUpdatesNodeConnectionsTest() {
        List<PixelNode> seam = new ArrayList<>();
        PixelNode currentNode = imageRepresentation.getRoot().right;
        seam.add(currentNode);

        currentNode = currentNode.down.right;
        seam.add(currentNode);

        currentNode = currentNode.down.left;
        seam.add(currentNode);

        imageRepresentation.removeSeam(seam);

        PixelNode root = imageRepresentation.getRoot();
        assertThat(root.color).isEqualTo(new Color(testImage.getRGB(0, 0)));
        assertThat(root.right.color).isEqualTo(new Color(testImage.getRGB(2, 0)));
        assertThat(root.right.right).isNull();

        PixelNode secondRow = root.down;
        assertThat(secondRow.color).isEqualTo(new Color(testImage.getRGB(0, 1)));
        assertThat(secondRow.right.color).isEqualTo(new Color(testImage.getRGB(1, 1)));
        assertThat(secondRow.right.right).isNull();

        PixelNode thirdRow = secondRow.down;
        assertThat(thirdRow.color).isEqualTo(new Color(testImage.getRGB(0, 2)));
        assertThat(thirdRow.right.color).isEqualTo(new Color(testImage.getRGB(2, 2)));
        assertThat(thirdRow.right.right).isNull();

        assertThat(thirdRow.down).isNull();
    }

    @Test
    void updateImageTest() {
        List<PixelNode> seam = new ArrayList<>();
        PixelNode currentNode = imageRepresentation.getRoot().right;
        seam.add(currentNode);

        currentNode = currentNode.down.right;
        seam.add(currentNode);

        currentNode = currentNode.down.left;
        seam.add(currentNode);

        imageRepresentation.removeSeam(seam);

        imageRepresentation.updateImage();
        BufferedImage updatedImage = imageRepresentation.getImage();

        int expectedWidth = 2;
        int expectedHeight = 3;
        assertThat(updatedImage.getWidth()).isEqualTo(expectedWidth);
        assertThat(updatedImage.getHeight()).isEqualTo(expectedHeight);

        assertThat(new Color(updatedImage.getRGB(0, 0))).isEqualTo(new Color(testImage.getRGB(0, 0)));
        assertThat(new Color(updatedImage.getRGB(1, 0))).isEqualTo(new Color(testImage.getRGB(2, 0)));

        assertThat(new Color(updatedImage.getRGB(0, 1))).isEqualTo(new Color(testImage.getRGB(0, 1)));
        assertThat(new Color(updatedImage.getRGB(1, 1))).isEqualTo(new Color(testImage.getRGB(1, 1)));

        assertThat(new Color(updatedImage.getRGB(0, 2))).isEqualTo(new Color(testImage.getRGB(0, 2)));
        assertThat(new Color(updatedImage.getRGB(1, 2))).isEqualTo(new Color(testImage.getRGB(2, 2)));
    }
}
