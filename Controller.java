import Utilities.Dialogs;
import Utilities.FileIO;
import Utilities.Print;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Controller class for MainWindow.fxml
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
            saveSettings.setProperty("opacity", String.valueOf(MainFX.currentStage.getOpacity()));
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
                MainFX.setTitle("Untitled - Notepad", MainFX.currentStage);
                modified = false; // the file hasn't been modified yet

                file = null;    // initialize a new file
            }

        } else {    // the file hasn't been modified yet
            textEdit.setHtmlText("");
            MainFX.setTitle("Untitled - Notepad", MainFX.currentStage);
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
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        file = fileChooser.showOpenDialog(MainFX.currentStage);

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
            MainFX.setTitle(file.getName() + " - Notepad", MainFX.currentStage);
            modified = false;

        } else {    // if the file has been modified
            boolean confirmed;
            confirmed = Dialogs.confirmationDialog( // Ask for confirmation
                    "Notepad",
                    "Warning",
                    "All unsaved changes will be lost! Continue?");

            if (confirmed) {    // user confirmed to discard the changes
                textEdit.setHtmlText(FileIO.openFile(file));    // open the file

                MainFX.setTitle(file.getName() + " - Notepad", MainFX.currentStage);
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
            MainFX.setTitle(file.getName() + " - Notepad", MainFX.currentStage);    // remove the "modified" text
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
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        file = fileChooser.showSaveDialog(MainFX.currentStage);
        if (file != null) {
            saveFile();
        }
    }

    /**
     * Export the HTML source code of textEdit to a plain text file
     */
    public void exportSource() {
        // Open a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export HTML source to a file");
        fileChooser.getExtensionFilters().addAll(   // set file extensions filter
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*")
        );

        if (file != null) { // set the initial filename to the HTML file's name + .txt if possible
            fileChooser.setInitialFileName(file.getName() + ".txt");
        }

        File sourceFile;
        sourceFile = fileChooser.showSaveDialog(MainFX.currentStage);

        if (sourceFile != null) {
            FileIO.saveFile(sourceFile, textEdit.getHtmlText());
        }
    }

    /**
     * Display "(Modified)" text in the title bar when the file was modified
     */
    public void fileModified() {
        if (!modified) {    // if the text isn't already in the title bar
            String currentTitle = MainFX.currentStage.getTitle();
            MainFX.setTitle(currentTitle + " (Modified)", MainFX.currentStage);
            modified = true;
        }
    }

    /* INSERTING OBJECTS */

    /**
     * Insert a web image to textEdit
     */
    public void insertImage() {
        String imageAddress = Dialogs.inputDialog(
                "Notepad",
                "Insert an image",
                "Enter the web address of the image you want to insert:",
                "http://"
        );

        // Check whether the user has clicked OK
        if (imageAddress != null) {
            String compiledImageAddress = "<img src=\"" + imageAddress + "\" alt=\"" + imageAddress + "\" />";
            appendHTMLText(textEdit, compiledImageAddress);
        }
    }

    /**
     * Insert a hyperlink to textEdit
     */
    public void insertLink() {
        String linkAddress = Dialogs.inputDialog(   // todo: find a better way to specify link text
                "Notepad",
                "Insert a link",
                "Enter a web address to insert as a link:",
                "http://"
        );

        // Check whether the user has clicked OK
        if (linkAddress != null) {
            String compiledAddress = "<a href=\"" + linkAddress + "\">" + linkAddress + "</a>";
            appendHTMLText(textEdit, compiledAddress);
        }
    }

    /**
     * Insert a JavaScript script to textEdit
     */
    public void insertScript() {
        String scriptText = Dialogs.inputDialog(
                "Notepad",
                "Insert a script",
                "Warning!\n" +
                        "Scripts can be harmful and some browsers will block them!\n" +
                        "Enter your script to the input box on the right.",
                "script"
        );

        // Check whether the user has clicked OK
        if (scriptText != null) {
            String compiledScriptText = "<script> " + scriptText + " </script>";
            appendHTMLText(textEdit, compiledScriptText);
        }
    }

    /**
     * Insert a script alternative text that will be displayed if the browser
     * blocks / does not support JavaScript scripts
     */
    public void insertScriptAltText() {
        String scriptAltText = Dialogs.inputDialog(
                "Notepad",
                "Insert script alternative text",
                "This text will be displayed instead of a script if\n" +
                        "the browser blocks / does not support JavaScript scripts.",
                "Your browser does not support JavaScript."
        );

        // Check whether the user has clicked OK
        if (scriptAltText != null) {
            String compiledScriptAltText = "<noscript> " + scriptAltText + " </noscript>";
            appendHTMLText(textEdit, compiledScriptAltText);
        }
    }

    /**
     * Insert an HTML quote to textEdit
     */
    public void insertQuote() {
        String quoteText = Dialogs.inputDialog(
                "Notepad",
                "Insert a quote",
                "Enter a quote to insert:",
                "Quote"
        );

        // Check whether the user has clicked OK
        if (quoteText != null) {
            String compiledHTMLQuote = "<q> " + quoteText + " </q>";
            appendHTMLText(textEdit, compiledHTMLQuote);
        }
    }

    /**
     * Insert scrolling text (marquee tag) to textEdit
     */
    public void insertScrollingText() {
        String marqueeText = Dialogs.inputDialog(
                "Notepad",
                "Insert scrolling text",
                "Enter the text to insert:",
                "Text"
        );

        // Check whether the user has clicked OK
        if (marqueeText != null) {
            String compiledHTMLMarquee = "<marquee> " + marqueeText + " </marquee>";
            appendHTMLText(textEdit, compiledHTMLMarquee);
        }
    }

    /**
     * Insert an HTML symbol to textEdit
     */
    public void insertSymbol() {
        String symbolCode = Dialogs.inputDialog(
                "Notepad",
                "Insert a symbol",
                "Enter a symbol code to insert:\n" +
                        "Symbol codes must end with a semicolon!",
                "&symbol;"
        );

        // Check whether the user has clicked OK
        if (symbolCode != null) {
            appendHTMLText(textEdit, symbolCode);
        }
    }

    /**
     * Insert a code tag to textEdit
     */
    public void insertCode() {
        String code = Dialogs.inputDialog(
                "Notepad",
                "Insert source code",
                "Enter source code to insert:",
                "code"
        );

        // Check whether the user has clicked OK
        if (code != null) {
            String codeText = "<code> " + code + " </code>";
            appendHTMLText(textEdit, codeText);
        }
    }

    /**
     * Insert an embedded website to textEdit
     */
    public void insertEmbeddedWebsite() {
        String websiteAddress = Dialogs.inputDialog(
                "Notepad",
                "Embed a website",
                "Enter web address of the website to embed:",
                "http://"
        );

        // Check whether the user has clicked OK
        if (websiteAddress != null) {
            String IFrameTag = "<iframe src=\"" + websiteAddress + "\"></iframe>";
            appendHTMLText(textEdit, IFrameTag);
        }
    }


    /**
     * Insert a custom HTML tag to textEdit
     */
    public void insertHTMLTag() {
        String HTMLTag = Dialogs.inputDialog(
                "Notepad",
                "Insert a custom HTML tag",
                "Refer to HTML documentation for valid values.\n" +
                        "\nWarning!\n" +
                        "Some browsers may block certain tags for safety reasons.",
                "<tag>text</tag>"
        );

        // Check whether the user has clicked OK
        if (HTMLTag != null) {
            appendHTMLText(textEdit, HTMLTag);
        }
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
     * Open a window with HTML source of the text in textEdit
     */
    public void openSource() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXML/HTMLSource.fxml"));
            Stage stage = new Stage();
            stage.setTitle("HTML Source Code");

            // Get the filename if possible
            if (file != null) {
                stage.setTitle("HTML Source Code - " + file.getName());
            }
            // Get the HTML Source
            HTMLSource.HTMLSourceText = textEdit.getHtmlText();

            stage.setScene(new Scene(root, 822, 562));
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set saving settings on or off
     */
    public void toggleSaveSettings() {
        saveSettings = checkboxSaveSettings.isSelected();

        if (!saveSettings) {    // try deleting the settings file if user selected to not save the settings
            boolean doDeleteFile = Dialogs.confirmationDialog( // display a confirmation dialog
                    "Notepad",
                    "Notepad",
                    "Would you like to also delete the settings file?"
            );

            if (doDeleteFile) { // the user has chosen to delete the file
                try {
                    File file = new File(settingsLocation);

                    if (file.delete()) {
                        System.out.println("Removing settings file done.");
                    }
                } catch (Exception e) {
                    System.out.println("Removing settings file failed, continuing...");
                }
            }
        }
    }

    /**
     * Disable mouse interaction with textEdit
     */
    public void disableMouse() {
        textEdit.setMouseTransparent(disableMouse.isSelected());
    }

    /**
     * Change the opacity of MainWindow
     */
    public void changeOpacity() {
        opacity = (float) opacitySlider.getValue() / 100;
        if (opacity < 0.01f)    // do not make the window invisible
            opacity = 0.01f;
        MainFX.currentStage.setOpacity(opacity);
    }

    /* INFO MENU FUNCTIONS */

    /**
     * Show an About dialog with info about the program
     */
    public void showAboutDialog() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXML/About.fxml"));
            Stage stage = new Stage();
            stage.setTitle("About Notepad");
            stage.setScene(new Scene(root, 638, 281));
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /* MISC */

    /**
     * Append text to the HTML code of an HTMLEditor control
     */
    private void appendHTMLText(HTMLEditor editor, String text) {
        editor.setHtmlText(editor.getHtmlText() + text);
    }

} // end class Controller
