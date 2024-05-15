package henrycaldwell;

import java.awt.Color;

/**
 * Represents a pixel in an image 'grid', storing its color, energy, brightness, and links to adjacent pixels.
 */
public class PixelNode {
    // The energy of the pixel, calculated based on the surrounding pixels' brightness.
    // The brightness of the pixel, calculated as the average of the RGB color components.
    double energy, brightness;
    // References to the pixel node directly above, below, left, and right of this one in the 'grid'.
    PixelNode up, down, left, right;
    // The color of the pixel represented by this node.
    Color color;

    /**
     * Initializes a PixelNode with a specific color.
     * @param color The color of the pixel.
     */
    public PixelNode(Color color) {
        this.color = color;
    }
}
