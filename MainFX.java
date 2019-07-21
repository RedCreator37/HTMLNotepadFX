import Utilities.Dialogs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for Notepad
 */
public class MainFX extends Application {

    static Stage currentStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/MainWindow.fxml"));
        primaryStage.setTitle("Untitled - Notepad");
        primaryStage.setScene(new Scene(root, 1135, 700));
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

}
