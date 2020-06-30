import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import util.Dialogs;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Main application class for HTMLNotepadFX
 */
public class MainFX extends Application {

    /**
     * Stores the settings
     */
    private Controller controller;

    /**
     * Used for communication with the Controller class
     *
     * @see Controller
     */
    static Stage currentStage;

    /**
     * Starts the UI
     *
     * @param primaryStage the main window
     * @throws Exception on fatal errors
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("Untitled - HTMLNotepadFX");

        // adjust the window size to still make it visible on smaller screens
        Pair<Double, Double> screenSize = getScreenSize();
        double width = 1135, height = 700;
        if (screenSize.getKey() < width) width = screenSize.getValue();
        if (screenSize.getValue() < height) height = screenSize.getKey();

        primaryStage.setScene(new Scene(root, width, height));
        currentStage = primaryStage;

        controller = new Controller();
        controller.loadSettings();
        controller.toggleOldUi();   // otherwise the stylesheets don't get loaded
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {   // ask for confirmation before closing
            boolean confirmed = Dialogs.confirmationDialog(
                    "HTMLNotepadFX", "Warning",
                    "All unsaved changes will be lost! Continue?");
            if (confirmed) {
                controller.saveSettings();
                System.exit(0);
            } else event.consume(); // don't close on Cancel
        });

        System.gc();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Sets title bar caption for the specified window.
     *
     * @param newTitle new title bar text to display
     * @param stage    a window that will get the new title
     */
    static void setTitle(String newTitle, Stage stage) {
        stage.setTitle(newTitle);
    }

    /**
     * Gets the screen size
     *
     * @return the screen size in a Key-Value pair
     * (key is screen width, value is screen height)
     */
    private static Pair<Double, Double> getScreenSize() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        return new Pair<>(size.getWidth(), size.getHeight());
    }

}
