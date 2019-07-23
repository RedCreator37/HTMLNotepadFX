import Utilities.Dialogs;
import Utilities.FileIO;
import Utilities.Print;
import Utilities.VersionData;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;

/**
 * Controller class for MainWindow.fxml
 */
public class Controller extends Component {

    /* Main settings */
    private boolean saveSettings = true;
    private double configVersion = VersionData.CONFIG_VERSION;
    private float opacity = 1f;

    /**
     * Load settings from an XML file
     */
    void loadSettings() {
        Properties loadSettings = new Properties();
        try {
            loadSettings.loadFromXML(new FileInputStream(VersionData.SETTINGS_LOCATION));
            textEdit.setMouseTransparent(Boolean.valueOf(loadSettings.getProperty("mouse_disabled")));
            opacitySlider.setValue(Float.valueOf(loadSettings.getProperty("opacity")) * 100);
            configVersion = Integer.parseInt(loadSettings.getProperty("config_version"));

            if (configVersion != VersionData.CONFIG_VERSION) {
                Dialogs.warningDialog(
                        "Notepad",
                        "Invalid config file version",
                        "Loaded config file reports version " + configVersion +
                                " while this program is using version " + VersionData.CONFIG_VERSION +
                                "\nKeep in mind that some settings probably haven't been loaded.");
            }
        } catch (IOException | NumberFormatException e) {
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
            saveSettings.setProperty("config_version", String.valueOf(configVersion));
            try {
                File file = new File(VersionData.SETTINGS_LOCATION);
                FileOutputStream fileOut = new FileOutputStream(file);
                saveSettings.storeToXML(fileOut, "");
                fileOut.close();
            } catch (IOException e) {
                System.out.println("Saving settings failed, continuing...");
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //   F I L E   M A N A G E M E N T
    /////////////////////////////////////////////////////////////////////////////////////

    /* Initialize controls */
    public HTMLEditor textEdit = new HTMLEditor();
    public Slider opacitySlider = new Slider();
    public MenuBar mainMenuBar = new MenuBar();
    public CheckMenuItem disableMouse = new CheckMenuItem();
    public CheckMenuItem checkboxSaveSettings = new CheckMenuItem();

    private File file;  // current file
    private boolean modified;   // whether the file has been modified

    /**
     * Create a blank file by deleting the contents of textEdit
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

    /**
     * Retrieve a HTML file from the internet and load it in textEdit
     */
    public void downloadHTMLFile() {
        String url = Dialogs.inputDialog(
                "Notepad",
                "Retrieve HTML file from web",
                "Enter a valid web address of a HTML file to download and display.\n" +
                        "Embedded objects (such as images) won't be downloaded in the process.\n" +
                        "\nWarning!\n" +
                        "Any unsaved changes in the current file will be lost!",
                "http://"
        );

        // the user has clicked OK
        if (url != null) try {
            Platform.runLater(() -> {   // set the waiting cursor
                Stage stage = (Stage) textEdit.getScene().getWindow();
                stage.getScene().setCursor(Cursor.WAIT);
            });

            URL fileURL = new URL(url); // get the URL
            InputStream inputStream = fileURL.openStream();
            Scanner scanner = new Scanner(inputStream);

            textEdit.setHtmlText(""); // clean the textEdit first
            MainFX.setTitle("Untitled - Notepad", MainFX.currentStage);
            modified = false; // the file hasn't been modified yet

            file = null;    // initialize a new file

            while (scanner.hasNextLine()) {   // get the text and append it to textEdit
                appendHTMLText(textEdit, scanner.nextLine());
            }
        } catch (IOException | IllegalArgumentException e) {
            Dialogs.errorDialog(
                    "Notepad",
                    "Error retrieving HTML file",
                    "An error has occurred while attempting to \nretrieve specified HTML file: \n" +
                            e.getMessage()
            );
        } finally {
            Platform.runLater(() -> {   // revert to the default cursor
                Stage stage = (Stage) textEdit.getScene().getWindow();
                stage.getScene().setCursor(Cursor.DEFAULT);
            });
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //   E D I T I N G   A N D   O B J E C T   I N S E R T I O N
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Append text to the HTML code of a HTMLEditor control
     */
    private void appendHTMLText(HTMLEditor editor, String text) {
        editor.setHtmlText(editor.getHtmlText() + text);
    }

    /**
     * Insert a web image to textEdit
     */
    public void insertImage() {
        Optional<Pair<String, String>> result =
                Dialogs.doubleInputDialog(
                        "Notepad",
                        "Insert an image",
                        "Insert an image into the document",
                        "Insert",
                        "Enter image address",
                        "Enter image alternative text",
                        "Address:", "Alt Text:");

        result.ifPresent(bothFields -> {
            String compiledImageAddress = "<img src=\"" + bothFields.getKey() + "\" alt=\""
                    + bothFields.getValue() + "\" />";
            appendHTMLText(textEdit, compiledImageAddress);
        });
    }

    /**
     * Insert a hyperlink to textEdit
     */
    public void insertLink() {
        Optional<Pair<String, String>> result =
                Dialogs.doubleInputDialog(
                        "Notepad",
                        "Insert hyperlink",
                        "Insert a hyperlink into the document",
                        "Insert",
                        "Enter link address",
                        "Enter link text",
                        "Address:", "Text:");

        result.ifPresent(bothFields -> {
            String compiledAddress = "<a href=\"" + bothFields.getKey() + "\">"
                    + bothFields.getValue() + "</a>";
            appendHTMLText(textEdit, compiledAddress);
        });
    }

    /**
     * Insert a JavaScript script to textEdit
     */
    public void insertScript() {
        String scriptText = Dialogs.textAreaInputDialog(
                "Notepad",
                "Insert a script",
                "Warning!\n" +
                        "Scripts can be harmful and some browsers will block them!\n",
                "Insert",
                "Enter script code"
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
        String scriptAltText = Dialogs.textAreaInputDialog(
                "Notepad",
                "Insert script alternative text",
                "This text will be displayed instead of script result\n" +
                        "if the browser blocks / doesn't support JavaScript scripts.",
                "Insert",
                "Enter something like \"Your browser does not support" +
                        "JavaScript\"..."
        );

        // Check whether the user has clicked OK
        if (scriptAltText != null) {
            String compiledScriptAltText = "<noscript> " + scriptAltText + " </noscript>";
            appendHTMLText(textEdit, compiledScriptAltText);
        }
    }

    /**
     * Insert a HTML quote to textEdit
     */
    public void insertQuote() {
        String quoteText = Dialogs.textAreaInputDialog(
                "Notepad",
                "Insert a quote",
                "Enter a quote to insert:",
                "Insert",
                "Enter a quote..."
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
     * Insert a HTML symbol to textEdit
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
        String code = Dialogs.textAreaInputDialog(
                "Notepad",
                "Insert source code",
                "Insert some text to be displayed in the <code> tag:",
                "Insert",
                "Enter some code..."
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
            String IFrameTag = "<iframe src=\"" + websiteAddress + "\" height=\"300\" width=\"500\"></iframe>";
            appendHTMLText(textEdit, IFrameTag);
        }
    }

    /**
     * Insert a custom HTML tag to textEdit
     */
    public void insertHTMLTag() {
        String HTMLTag = Dialogs.textAreaInputDialog(
                "Notepad",
                "Insert a custom HTML tag",
                "Refer to HTML documentation for valid values.\n" +
                        "\nWarning!\n" +
                        "Some browsers may block certain tags for security reasons.",
                "Insert",
                "Enter something like \"<tag>text</tag>\""
        );

        // Check whether the user has clicked OK
        if (HTMLTag != null) {
            appendHTMLText(textEdit, HTMLTag);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //   P R I N T I N G
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Call the printing method in Print class
     */
    public void print() {
        Print.printText(textEdit.getHtmlText());
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //   O P T I O N S   M E N U
    /////////////////////////////////////////////////////////////////////////////////////

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
                    "Would you also like to delete the settings file?"
            );

            // the user has chosen to delete the file
            if (doDeleteFile) try {
                File file = new File(VersionData.SETTINGS_LOCATION);

                if (file.delete()) {
                    System.out.println("Removing settings file done.");
                }
            } catch (Exception e) {
                System.out.println("Removing settings file failed, continuing...");
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

    /////////////////////////////////////////////////////////////////////////////////////
    //   I N F O   M E N U
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Show an About dialog with info about the program
     */
    public void showAboutDialog() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXML/About.fxml"));
            Stage stage = new Stage();
            stage.setTitle("About Notepad");
            stage.setScene(new Scene(root, 638, 281));
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //   C L O S I N G   A N D   E X I T I N G   T H E   P R O G R A M
    /////////////////////////////////////////////////////////////////////////////////////

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

} // end class Controller
