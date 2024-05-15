package henrycaldwell;

import java.io.File;
import java.util.Scanner;

/**
 * Handles user interactions for the image editing service via a console interface.
 */
public class UserInterface {
    // Service layer for handling core functionality of image editing.
    private static ServiceLayer editingService;

    /**
     * Main method to launch the user interface. Processes user commands to manipulate images.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean imageLoaded = false;

        while (!imageLoaded) {
            System.out.print("Please enter a valid image path: ");
            String filePath = scanner.nextLine();
            File imageFile = new File(filePath);
            
            if (imageFile.exists() && !imageFile.isDirectory()) {
                editingService = new ServiceLayer(filePath);
                imageLoaded = true;
            } else {
                System.out.println("The file does not exist or is not accessible. Please try again.");
            }
        }

        String input;
        boolean running = true;

        while (running) {
            displayMenu();
            input = scanner.nextLine().toLowerCase();

            switch (input) {
                case "b":
                    editingService.findAndHighlightSeam(false);
                    break;
                case "e":
                    editingService.findAndHighlightSeam(true);
                    break;
                case "d":
                    editingService.removeSeam();
                    break;
                case "u":
                    editingService.undoLastEdit();
                    break;
                case "q":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }

        System.out.println("Exiting...");
        scanner.close();
    }

    /**
     * Displays the main menu, listing available commands.
     */
    private static void displayMenu() {
        System.out.println("\nPlease enter a command:");
        System.out.println("B - Highlight the bluest seam");
        System.out.println("E - Highlight the seam with the lowest energy");
        System.out.println("D - Delete the highlighted seam");
        System.out.println("U - Undo the last deletion");
        System.out.println("Q - Quit");
        System.out.print("Enter command: ");
    }
}