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
    }

    /**
     * Saves the HTML source code to a text file
     */
    public void saveToFile() {
        // don't stay on top while the save as dialog is displayed
        Stage stage = (Stage) sourceText.getScene().getWindow();
        stage.setAlwaysOnTop(false);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(   // set file extensions filter
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
        File file = fileChooser.showSaveDialog(MainFX.currentStage);
        if (file != null) FileIO.saveFile(file, sourceText.getText());

        // now make the window always on top again
        stage.setAlwaysOnTop(true);
    }

    /**
     * Reloads the HTML source
     */
    public void refreshHTML() {
        sourceText.setText(htmlSourceText);
    }

    /**
     * Prints the HTML source
     */
    public void printSource() {
        Printing.printText(sourceText.getText());
    }

    /**
     * Closes the window
     */
    public void closeSource() {
        Stage stage = (Stage) sourceText.getScene().getWindow();
        stage.close();
    }
}
