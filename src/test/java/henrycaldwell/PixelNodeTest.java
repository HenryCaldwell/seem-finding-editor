package henrycaldwell;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.awt.Color;

public class PixelNodeTest {
    @Test
    void pixelNodeColor() {
        PixelNode node = new PixelNode(Color.RED);
        assertEquals(Color.RED, node.color, "PixelNode should correctly assign the color.");
    }

    @Test
    void pixelNodeNeighborsInitiallyNullTest() {
        PixelNode node = new PixelNode(Color.RED);
        assertNull(node.left, "Left neighbor should initially be null.");
        assertNull(node.right, "Right neighbor should initially be null.");
        assertNull(node.up, "Up neighbor should initially be null.");
        assertNull(node.down, "Down neighbor should initially be null.");
    }

    @Test
    void linkPixelNodeNeighborsTest() {
        PixelNode center = new PixelNode(Color.RED);
        PixelNode left = new PixelNode(Color.GREEN);
        PixelNode right = new PixelNode(Color.BLUE);
        PixelNode up = new PixelNode(Color.YELLOW);
        PixelNode down = new PixelNode(Color.BLACK);

        center.left = left;
        center.right = right;
        center.up = up;
        center.down = down;

        assertEquals(left, center.left, "Left neighbor should be linked correctly.");
        assertEquals(right, center.right, "Right neighbor should be linked correctly.");
        assertEquals(up, center.up, "Up neighbor should be linked correctly.");
        assertEquals(down, center.down, "Down neighbor should be linked correctly.");
    }

    @Test
    void pixelNodeEnergyAndBrightnessTest() {
        PixelNode node = new PixelNode(Color.RED);
        node.energy = 100.0;
        node.brightness = 150.0;

        assertEquals(100.0, node.energy, "Energy should be set and retrieved correctly.");
        assertEquals(150.0, node.brightness, "Brightness should be set and retrieved correctly.");
    }
}