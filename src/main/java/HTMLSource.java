import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.FileIO;
import util.Printing;

import java.io.File;

/**
 * Controller class for HTMLSource.fxml
 */
public class HTMLSource {

    // initialize controls
    public TextArea sourceText; // text area for the source code
    static String htmlSourceText;

    /**
     * Performs initialization
     */
    @FXML
    protected void initialize() {
        refreshHTML();
        sourceText.setStyle("-fx-border-color: #ffffff");
    }

    /**
     * Saves the source code to a text file
     */
    public void saveToFile() {
        // don't stay on top while the save as dialog is displayed
        Stage stage = (Stage) sourceText.getScene().getWindow();
        stage.setAlwaysOnTop(false);

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
        File file = chooser.showSaveDialog(MainFX.currentStage);
        if (file != null) FileIO.saveFile(file, sourceText.getText());

        stage.setAlwaysOnTop(true);
    }

    /**
     * Reloads the source code
     */
    public void refreshHTML() {
        sourceText.setText(htmlSourceText);
    }

    /**
     * Prints the source code
     */
    public void printSource() {
        Printing.printText(sourceText.getText());
    }

    /**
     * Closes the window
     */
    public void closeSource() {
        ((Stage) sourceText.getScene().getWindow()).close();
    }
}
