# Java Image Processing

This repository contains a Java implementation for image processing and manipulation. It was my final project for Fundamentals of Computer Science II. It includes multiple classes that work together to manage image representations, pixel data, service layers, and user interfaces. The project also includes unit tests for key components.

*To properly provide a file path, the String provided should look something like, "src/main/resources/(imagename).png*

*Edited images are saved to the target folder*

## Classes Overview

### `EditCommand`

The `EditCommand` class represents an abstract command for editing images, providing a template for various edit operations.

#### Attributes
- No specific attributes.

#### Methods
- `execute()`: Abstract method to be implemented by subclasses to execute the edit command.
- `undo()`: Abstract method to be implemented by subclasses to undo the edit command.

### `ImageRepresentation`

The `ImageRepresentation` class represents an image, including its pixel data and various attributes.

#### Attributes
- `pixels`: 2D array of `PixelNode` objects representing the pixel data of the image.
- `width`: Width of the image.
- `height`: Height of the image.

#### Methods
- `getPixel(int x, int y)`: Returns the `PixelNode` at the specified coordinates.
- `setPixel(int x, int y, PixelNode pixel)`: Sets the `PixelNode` at the specified coordinates.
- `applyFilter(Filter filter)`: Applies a filter to the image.
- `resize(int newWidth, int newHeight)`: Resizes the image to the specified dimensions.

### `PixelNode`

The `PixelNode` class represents a single pixel in an image, containing color information and other relevant attributes.

#### Attributes
- `red`: Red component of the pixel.
- `green`: Green component of the pixel.
- `blue`: Blue component of the pixel.
- `alpha`: Alpha (transparency) component of the pixel.

#### Methods
- `setColor(int red, int green, int blue, int alpha)`: Sets the color of the pixel.
- `getColor()`: Returns the color of the pixel as an array of integers.
- `blend(PixelNode other)`: Blends this pixel with another pixel.

### `ServiceLayer`

The `ServiceLayer` class provides various services for image processing, acting as an intermediary between the user interface and the underlying image data.

#### Attributes
- `images`: List of `ImageRepresentation` objects managed by the service layer.

#### Methods
- `loadImage(String filePath)`: Loads an image from the specified file path.
- `saveImage(String filePath, ImageRepresentation image)`: Saves the specified image to the specified file path.
- `applyEdit(EditCommand command)`: Applies an edit command to the current image.
- `undoEdit()`: Undoes the last applied edit command.

### `UserInterface`

The `UserInterface` class provides a graphical user interface for interacting with the image processing system.

#### Attributes
- `serviceLayer`: The `ServiceLayer` instance used by the user interface.

#### Methods
- `displayImage(ImageRepresentation image)`: Displays the specified image in the user interface.
- `openImage()`: Opens an image file and displays it.
- `saveImage()`: Saves the currently displayed image.
- `applyFilter()`: Applies a filter to the currently displayed image.

## Unit Tests

### `ImageRepresentationTest`

The `ImageRepresentationTest` class contains unit tests for the `ImageRepresentation` class.

#### Methods
- `testGetPixel()`: Tests the `getPixel` method.
- `testSetPixel()`: Tests the `setPixel` method.
- `testApplyFilter()`: Tests the `applyFilter` method.
- `testResize()`: Tests the `resize` method.

### `PixelNodeTest`

The `PixelNodeTest` class contains unit tests for the `PixelNode` class.

#### Methods
- `testSetColor()`: Tests the `setColor` method.
- `testGetColor()`: Tests the `getColor` method.
- `testBlend()`: Tests the `blend` method.

### `ServiceLayerTest`

The `ServiceLayerTest` class contains unit tests for the `ServiceLayer` class.

#### Methods
- `testLoadImage()`: Tests the `loadImage` method.
- `testSaveImage()`: Tests the `saveImage` method.
- `testApplyEdit()`: Tests the `applyEdit` method.
- `testUndoEdit()`: Tests the `undoEdit` method.

## Libraries Used

This project uses the following libraries:

- **JUnit Jupiter**: A testing framework for Java.
- **AssertJ**: A fluent assertion library for Java.

### Maven Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.8.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.21.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>