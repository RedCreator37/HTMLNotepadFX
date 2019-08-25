import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.FileIO;
import util.Print;

import java.io.File;

/**
 * Controller class for HTMLSource.fxml
 */
public class HTMLSource {

    // initialize controls
    public TextArea sourceText; // text area for the source code
    static String htmlSourceText;

    /**
     * Initialize the window, get the source code...
     */
    @FXML
    protected void initialize() {   // fixme: it sometimes takes more time to update the source code
        sourceText.setText(htmlSourceText);
    }

    /**
     * Save the HTML source code to a text file
     */
    public void saveToFile() {
        // don't stay on top while the save as dialog is displayed
        Stage stage = (Stage) sourceText.getScene().getWindow();
        stage.setAlwaysOnTop(false);

        File file;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(   // set file extensions filter
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        file = fileChooser.showSaveDialog(MainFX.currentStage);
        if (file != null)
            FileIO.saveFile(file, sourceText.getText());

        // now make the window always on top again
        stage.setAlwaysOnTop(true);
    }

    /**
     * Print the HTML source code
     */
    public void printSource() {
        Print.printText(sourceText.getText());
    }

    /**
     * Close the HTML Source code window
     */
    public void closeSource() {
        Stage stage = (Stage) sourceText.getScene().getWindow();
        stage.close();
    }
}
