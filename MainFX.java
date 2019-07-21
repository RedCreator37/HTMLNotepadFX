import Utilities.Dialogs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Main application class for Notepad
 */
public class MainFX extends Application {

    static Stage currentStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/MainWindow.fxml"));
        primaryStage.setTitle("Untitled - Notepad");

        // adjust the window size to make it work on smaller screens
        Pair<Double, Double> screenSize = getScreenSize();
        double width = 1135, height = 700;
        if (screenSize.getKey() < height) height = screenSize.getKey();
        if (screenSize.getValue() < width) width = screenSize.getValue();

        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();

        currentStage = primaryStage;

        // load settings
        Controller controllerObject = new Controller();
        controllerObject.loadSettings();

        primaryStage.setOnCloseRequest(event -> {   // Ask for confirmation when closing the program
            boolean confirmed = Dialogs.confirmationDialog(
                    "Notepad",
                    "Warning",
                    "All unsaved changes will be lost! Continue?");

            if (confirmed) {
                Controller controller = new Controller();
                controller.saveSettings();

                System.exit(0); // User selected OK, close the program
            } else {
                event.consume(); // User selected Cancel, don't close the program
            }
        });

        System.gc();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Set the title bar text in specified window.
     *
     * @param newTitle new title bar text to display
     * @param stage    a window that will get the new title
     */
    static void setTitle(String newTitle, Stage stage) {
        stage.setTitle(newTitle);
    }

    /**
     * Get the screen size and return it in favor of a key - value
     * pair (key is screen wight, value is screen height)
     */
    private static Pair<Double, Double> getScreenSize() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        double width = size.getWidth(), height = size.getHeight();

        return new Pair<>(width, height);
    }

}
