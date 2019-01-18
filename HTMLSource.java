import Utilities.FileIO;
import Utilities.Print;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Controller class for HTMLSource.fxml
 *
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
public class HTMLSource {

    public TextArea sourceText; // text area for source code

    /**
     * Get the HTML source code from textEdit and put it into sourceText text area
     */
    public void getHTMLSource() {   // FIXME: will sometimes get the text of a blank HTMLEditor
        Controller object = new Controller();
        String source = object.textEdit.getHtmlText();

        sourceText.setText(source);
    }

    /**
     * Save the HTML source code to a text file
     */
    public void saveToFile() {
        File file;
        // Open a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(   // set file extensions filter
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        file = fileChooser.showSaveDialog(MainFX.currentStage);
        if (file != null) {
            FileIO.saveFile(file, sourceText.getText());
        }
    }

    /**
     * Print the HTML source code
     */
    public void printSource() {
        Print.printText(sourceText.getText());
    }
}
