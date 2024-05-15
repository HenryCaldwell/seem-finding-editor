package henrycaldwell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class ServiceLayerTest {
    private ServiceLayer serviceLayer;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        serviceLayer = new ServiceLayer("src/main/resources/TESTCASE_IMAGE.png");
        testImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);

        testImage.setRGB(0, 0, Color.RED.getRGB());     // Top-left
        testImage.setRGB(1, 0, Color.BLUE.getRGB());    // Top-center
        testImage.setRGB(2, 0, Color.BLACK.getRGB());   // Top-right
        testImage.setRGB(0, 1, Color.YELLOW.getRGB());  // Middle-left
        testImage.setRGB(1, 1, Color.GRAY.getRGB());    // Center
        testImage.setRGB(2, 1, Color.CYAN.getRGB());    // Middle-right
        testImage.setRGB(0, 2, Color.MAGENTA.getRGB()); // Bottom-left
        testImage.setRGB(1, 2, Color.BLACK.getRGB());   // Bottom-center
        testImage.setRGB(2, 2, Color.GRAY.getRGB());    // Bottom-right
    }

    @Test
    void imageLoadedTest() {
        assertThat(serviceLayer.getImageRep().getImage().getRGB(0, 0)).isEqualTo(testImage.getRGB(0, 0));
        assertThat(serviceLayer.getImageRep().getImage().getRGB(1, 0)).isEqualTo(testImage.getRGB(1, 0));
        assertThat(serviceLayer.getImageRep().getImage().getRGB(2, 0)).isEqualTo(testImage.getRGB(2, 0));

        assertThat(serviceLayer.getImageRep().getImage().getRGB(0, 1)).isEqualTo(testImage.getRGB(0, 1));
        assertThat(serviceLayer.getImageRep().getImage().getRGB(1, 1)).isEqualTo(testImage.getRGB(1, 1));
        assertThat(serviceLayer.getImageRep().getImage().getRGB(2, 1)).isEqualTo(testImage.getRGB(2, 1));

        assertThat(serviceLayer.getImageRep().getImage().getRGB(0, 2)).isEqualTo(testImage.getRGB(0, 2));
        assertThat(serviceLayer.getImageRep().getImage().getRGB(1, 2)).isEqualTo(testImage.getRGB(1, 2));
        assertThat(serviceLayer.getImageRep().getImage().getRGB(2, 2)).isEqualTo(testImage.getRGB(2, 2));
    }

    @Test
    void findAndHighlightLowestEnergySeam() {
        serviceLayer.findAndHighlightSeam(true);

        PixelNode first = serviceLayer.getImageRep().getRoot();
        PixelNode second = first.right.down;
        PixelNode third = second.right.down;

        List<PixelNode> comparisonList = new ArrayList<>();
        comparisonList.add(first);
        comparisonList.add(second);
        comparisonList.add(third);

        assertThat(serviceLayer.getLastFoundSeam()).isEqualTo(comparisonList);
    }

    @Test
    void findAndHighlightBluestSeam() {
        serviceLayer.findAndHighlightSeam(false);

        PixelNode first = serviceLayer.getImageRep().getRoot().right;
        PixelNode second = first.down;
        PixelNode third = second.left.down;

        List<PixelNode> comparisonList = new ArrayList<>();
        comparisonList.add(first);
        comparisonList.add(second);
        comparisonList.add(third);

        assertThat(serviceLayer.getLastFoundSeam()).isEqualTo(comparisonList);
    }

    @Test
    void removeSeamAndCheckEditHistory() {
        serviceLayer.findAndHighlightSeam(true);
        List<PixelNode> testSeam = serviceLayer.getLastFoundSeam();

        serviceLayer.removeSeam();

        assertThat(serviceLayer.getEditHistory()).isNotEmpty();

        for (PixelNode node : testSeam) {
            if (node.left != null) {
                assertThat(node.left.right).isNotEqualTo(node);
            }

            if (node.right != null) {
                assertThat(node.right.left).isNotEqualTo(node);
            }

            if (node.down != null) {
                assertThat(node.down.up).isNotEqualTo(node);
            }

            if (node.up != null) {
                assertThat(node.up.down).isNotEqualTo(node);
            }
        }
    }

    @Test
    void undoLastEdit() {
        serviceLayer.findAndHighlightSeam(true);
        List<PixelNode> testSeam = serviceLayer.getLastFoundSeam();

        serviceLayer.removeSeam();
        serviceLayer.undoLastEdit();

        for (PixelNode node : testSeam) {
            if (node.left != null) {
                assertThat(node.left.right).isEqualTo(node);
            }

            if (node.right != null) {
                assertThat(node.right.left).isEqualTo(node);
            }

            if (node.down != null) {
                assertThat(node.down.up).isEqualTo(node);
            }
            
            if (node.up != null) {
                assertThat(node.up.down).isEqualTo(node);
            }
        }
    }

    @Test
    void undoWithoutEdits() {
        serviceLayer.undoLastEdit();
        assertThat(serviceLayer.getEditHistory()).isEmpty();
    }
}
