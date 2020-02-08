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

    static Stage currentStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("Untitled - HTMLNotepadFX");

        // adjust the window size to make it work on smaller screens
        Pair<Double, Double> screenSize = getScreenSize();
        double width = 1135, height = 700;
        if (screenSize.getKey() < width) width = screenSize.getValue();
        if (screenSize.getValue() < height) height = screenSize.getKey();

        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();
        currentStage = primaryStage;

        // fixme: not working as it creates a new instance every time!
        new Controller().loadSettings();
        primaryStage.setOnCloseRequest(event -> {   // ask for confirmation before closing the program
            boolean confirmed = Dialogs.confirmationDialog(
                    "HTMLNotepadFX", "Warning",
                    "All unsaved changes will be lost! Continue?");
            if (confirmed) {
                new Controller().saveSettings();
                System.exit(0);
            } else event.consume(); // user has selected Cancel, don't close the program
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
        return new Pair<>(size.getWidth(), size.getHeight());
    }

}
