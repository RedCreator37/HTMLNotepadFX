import util.FileIO;
import util.Print;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Controller class for HTMLSource.fxml
 */
public class HTMLSource {

    /* Initialize controls */
    public TextArea sourceText; // text area for source code
    static String HTMLSourceText;

    /** Initialize the window, get the source code... */
    @FXML
    protected void initialize() {   // fixme: it sometimes takes more time to update the source code
        sourceText.setText(HTMLSourceText);
    }

    /**
     * Save the HTML source code to a text file
     */
    public void saveToFile() {
        // We don't want the window to stay above the Save As dialog
        Stage stage = (Stage) sourceText.getScene().getWindow();
        stage.setAlwaysOnTop(false);

        // Open a file chooser dialog
        File file;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(   // set file extensions filter
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        file = fileChooser.showSaveDialog(MainFX.currentStage);
        if (file != null)
            FileIO.saveFile(file, sourceText.getText());

        // Now make the window always on top again
        stage.setAlwaysOnTop(true);
    }

    /**
     * Print the HTML source code
     */
    public void printSource() {
        Print.printText(sourceText.getText());
    }

    /** Close the HTML Source code window */
    public void closeSource() {
        Stage stage = (Stage) sourceText.getScene().getWindow();
        stage.close();
    }
}
