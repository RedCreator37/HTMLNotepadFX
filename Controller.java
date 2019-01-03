import Utilities.Dialogs;
import Utilities.FileIO;
import Utilities.Print;
import Utilities.VersionData;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Controller class for MainWindow.fxml, Find.fxml and Settings.fxml
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
public class Controller extends Component {

    /* MAIN SETTINGS */

    private boolean saveSettings = true;
    private float opacity = 1f;

    private final String settingsLocation = ".Notepad_Settings.xml";

    /**
     * Load settings from an XML file
     */
    void loadSettings() {
        Properties loadSettings = new Properties();

        try {
            loadSettings.loadFromXML(new FileInputStream(settingsLocation));
            textEdit.setMouseTransparent(Boolean.valueOf(loadSettings.getProperty("mouse_disabled")));
            opacitySlider.setValue(Float.valueOf(loadSettings.getProperty("opacity")) * 100);
        } catch (IOException e) {
            System.out.println("Loading settings failed, continuing...");
        }

        if (opacity < 0.1f)
            opacity = 0.1f;
    }

    /**
     * Save settings to an XML file
     */
    void saveSettings() {
        if (saveSettings) {
            Properties saveSettings = new Properties();
            saveSettings.setProperty("mouse_disabled", String.valueOf(textEdit.isMouseTransparent()));
            saveSettings.setProperty("opacity", String.valueOf(Main.currentStage.getOpacity()));
            try {
                File file = new File(settingsLocation);
                FileOutputStream fileOut = new FileOutputStream(file);
                saveSettings.storeToXML(fileOut, "");
                fileOut.close();
            } catch (IOException e) {
                System.out.println("Saving settings failed, continuing...");
            }
        }
    }

    /***********************************************************
     *     F  I  L  E         M  A  N  A  G  E  M  E  N  T     *
     ***********************************************************/

    /* Initialize controls */
    public HTMLEditor textEdit = new HTMLEditor();
    public Slider opacitySlider = new Slider();
    public MenuBar mainMenuBar = new MenuBar();
    public CheckMenuItem disableMouse = new CheckMenuItem();
    public CheckMenuItem checkboxSaveSettings = new CheckMenuItem();

    /* FILE OPERATIONS */

    private File file;  // current file
    private boolean modified;   // whether the file has been modified

    /**
     * Create a blank file by deleting the content of textEdit
     */
    public void newFile() {
        boolean confirmedNewFile;

        if (modified) { // if the file has been modified
            confirmedNewFile = Dialogs.confirmationDialog(
                    "Notepad",
                    "Warning",
                    "All unsaved changes will be lost! Continue?");

            if (confirmedNewFile) {     // User selected OK
                textEdit.setHtmlText("");
                Main.setTitle("Untitled - Notepad", Main.currentStage);
                modified = false; // the file hasn't been modified yet

                file = null;    // initialize a new file
            }

        } else {    // the file hasn't been modified yet
            textEdit.setHtmlText("");
            Main.setTitle("Untitled - Notepad", Main.currentStage);
            modified = false;

            file = null;
        }
    }

    /**
     * Display a file chooser dialog and let the user choose a file
     */
    public void openFileDialog() {
        FileChooser fileChooser = new FileChooser(); // Open a file chooser dialog
        fileChooser.getExtensionFilters().addAll(   // set file extensions filter
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        file = fileChooser.showOpenDialog(Main.currentStage);

        if (file != null) { // If the user selected a file
            openFile(file);
        }
    }

    /**
     * Open a file and render its content in textEdit
     */
    private void openFile(File file) {
        if (!modified) {    // if the file hasn't been modified yet
            textEdit.setHtmlText(FileIO.openFile(file));    // open the file

            // Set the title bar text to match the file's name
            Main.setTitle(file.getName() + " - Notepad", Main.currentStage);
            modified = false;

        } else {    // if the file has been modified
            boolean confirmed;
            confirmed = Dialogs.confirmationDialog( // Ask for confirmation
                    "Notepad",
                    "Warning",
                    "All unsaved changes will be lost! Continue?");

            if (confirmed) {    // user confirmed to discard the changes
                textEdit.setHtmlText(FileIO.openFile(file));    // open the file

                Main.setTitle(file.getName() + " - Notepad", Main.currentStage);
                modified = false;
            }
        }
    }

    /**
     * Save changes to current file or go to saveAs if it's a new file
     */
    public void saveFile() {
        if (file != null) {  // if the text was already saved before or user selected it in saveAs dialog
            FileIO.saveFile(file, textEdit.getHtmlText());
            Main.setTitle(file.getName() + " - Notepad", Main.currentStage);    // remove the "modified" text
            modified = false;

        } else {    // if this is a new file
            saveAs();
        }
    }

    /**
     * Display a file chooser and let the user select into which file to save the content of textEdit,
     * and then write the text into specified file
     */
    public void saveAs() {
        // Open a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(   // set file extensions filter
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        file = fileChooser.showSaveDialog(Main.currentStage);
        if (file != null) {
            saveFile();
        }
    }

    /**
     * Display "(Modified)" text in the title bar when the file was modified
     */
    public void fileModified() {
        /*if (!modified && textEdit.isEditable()) {    // if the text isn't already in the title bar
            String currentTitle = Main.currentStage.getTitle();
            Main.setTitle(currentTitle + " (Modified)", Main.currentStage);
            modified = true;
        }*/
    }

    /* PRINTING */

    /**
     * Call the printing method in Print class
     */
    public void print() {
        Print.printText(textEdit.getHtmlText());
    }

    /* OPTIONS MENU FUNCTIONS */

    /**
     * Change the opacity of MainWindow
     */
    public void changeOpacity() {
        opacity = (float) opacitySlider.getValue() / 100;
        if (opacity < 0.01f)    // do not make the window invisible
            opacity = 0.01f;
        Main.currentStage.setOpacity(opacity);
    }

    /**
     * Disable mouse interaction with textEdit
     */
    public void disableMouse() {
        textEdit.setMouseTransparent(disableMouse.isSelected());
    }

    /* CLOSING AND EXITING THE PROGRAM */

    /**
     * Close the program
     */
    public void close() {
        boolean confirmedClose;

        if (modified) { // the file has been modified
            confirmedClose = Dialogs.confirmationDialog(
                    "Notepad",
                    "Warning",
                    "All unsaved changes will be lost! Continue?");

        } else {    // the file hasn't been modified
            confirmedClose = true;
        }
        if (confirmedClose) {     // User selected OK
            saveSettings();
            System.exit(0);
        }
    }

    /**
     * Show an About dialog with info about the program
     */
    public void showAboutDialog() {
        String betaNotice;

        if (VersionData.IS_BETA) {
            betaNotice = "BETA Pre-release";
        }

        Dialogs.infoDialog(
                "Notepad",
                "About Notepad",
                "Version: " + VersionData.VERSION +
                        "\nBuild number: " + VersionData.BUILD_NUMBER + "" +
                        "\nBuild date: " + VersionData.BUILD_DATE + "" +
                        "\n" + betaNotice);

    }

    public void toggleSaveSettings() {
        saveSettings = checkboxSaveSettings.isSelected();
    }
} // end class Controller
