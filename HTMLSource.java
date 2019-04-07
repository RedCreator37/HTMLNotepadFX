/*
 * Copyright (c) 2019 Tobija Žuntar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import Utilities.FileIO;
import Utilities.Print;
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
        if (file != null) {
            FileIO.saveFile(file, sourceText.getText());
        }

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
