import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import util.Dialogs;
import util.FileIO;
import util.VersionData;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;

/**
 * Controller class for MainWindow.fxml
 */
public class Controller extends Component {

    // general settings
    private boolean saveSettings = true;
    private double configVersion = VersionData.CONFIG_VERSION;
    private static boolean experimentalUI = false;
    private float opacity = 1f;

    /**
     * Load settings from an XML file
     */
    void loadSettings() {
        Properties loadSettings = new Properties();
        try {
            loadSettings.loadFromXML(new FileInputStream(VersionData.SETTINGS_LOCATION));
            textEdit.setMouseTransparent(Boolean.parseBoolean(loadSettings.getProperty("mouse_disabled")));
            opacitySlider.setValue(Float.parseFloat(loadSettings.getProperty("opacity")) * 100);

            // attempt to reload the last used file
            String lastFileName = loadSettings.getProperty("last_file");
            if (lastFileName != null) {
                file = new File(lastFileName);
                openFile(file);
            }

            reloadLastFile.setSelected(lastFileName != null);
            configVersion = Double.parseDouble(loadSettings.getProperty("config_version"));

            if (configVersion != VersionData.CONFIG_VERSION) {
                Dialogs.warningDialog(
                        "HTMLNotepadFX",
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

            // save the current file name
            if (file != null && reloadLastFile.isSelected())
                saveSettings.setProperty("last_file", file.getAbsolutePath());

            saveSettings.setProperty("config_version", String.valueOf(configVersion));
            try {
                File file = new File(VersionData.SETTINGS_LOCATION);
                FileOutputStream fileOut = new FileOutputStream(file);
                saveSettings.storeToXML(fileOut, "");
                fileOut.close();

                // attempt to make the settings file hidden
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    Path path = Paths.get(file.getAbsolutePath());
                    Files.setAttribute(path, "dos:hidden", true);
                }
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
    public CheckMenuItem reloadLastFile = new CheckMenuItem();
    public CheckMenuItem checkboxSaveSettings = new CheckMenuItem();
    public CheckMenuItem checkboxExperimentalUI = new CheckMenuItem();

    private File file;
    private boolean modified;

    /**
     * Create a blank file by deleting the contents of textEdit
     */
    public void newFile() {
        boolean confirmedNewFile;
        if (modified) {
            confirmedNewFile = Dialogs.confirmationDialog(
                    "HTMLNotepadFX",
                    "Warning",
                    "All unsaved changes will be lost! Continue?");

            if (confirmedNewFile) {
                textEdit.setHtmlText("");
                MainFX.setTitle("Untitled - HTMLNotepadFX", MainFX.currentStage);
                modified = false; // the file hasn't been modified yet

                file = null;
            }

        } else {    // if the file hasn't been modified yet
            textEdit.setHtmlText("");
            MainFX.setTitle("Untitled - HTMLNotepadFX", MainFX.currentStage);
            modified = false;

            file = null;
        }
    }

    /**
     * Display a file chooser dialog and let the user choose a file
     */
    public void openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(   // set file extensions filter
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        file = fileChooser.showOpenDialog(MainFX.currentStage);
        if (file != null) openFile(file);
    }

    /**
     * Open a file and render its content in textEdit
     */
    private void openFile(File file) {
        if (!modified) {    // if the file hasn't been modified yet
            textEdit.setHtmlText(FileIO.openFile(file));

            // Set the title bar text to match the file's name
            MainFX.setTitle(file.getName() + " - HTMLNotepadFX", MainFX.currentStage);
            modified = false;

        } else {    // if the file has been modified
            boolean confirmed;
            confirmed = Dialogs.confirmationDialog(
                    "HTMLNotepadFX",
                    "Warning",
                    "All unsaved changes will be lost! Continue?");

            if (confirmed) {
                textEdit.setHtmlText(FileIO.openFile(file));

                MainFX.setTitle(file.getName() + " - HTMLNotepadFX", MainFX.currentStage);
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

            // remove the "modified" text from the title bar
            MainFX.setTitle(file.getName() + " - HTMLNotepadFX", MainFX.currentStage);
            modified = false;
        } else saveAs();
    }

    /**
     * Display a file chooser and let the user select into which file to save the content of textEdit,
     * and then write the text into specified file
     */
    public void saveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(   // set extensions filters
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        file = fileChooser.showSaveDialog(MainFX.currentStage);
        if (file != null) saveFile();
    }

    /**
     * Export the HTML source code of textEdit to a plain text file
     */
    public void exportSource() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export HTML source to a file");
        fileChooser.getExtensionFilters().addAll(   // set extension filters
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*")
        );

        if (file != null) // set the initial filename to the HTML file's name + .txt if possible
            fileChooser.setInitialFileName(file.getName() + ".txt");

        File sourceFile;
        sourceFile = fileChooser.showSaveDialog(MainFX.currentStage);

        if (sourceFile != null)
            FileIO.saveFile(sourceFile, textEdit.getHtmlText());
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
        String url = Dialogs.longInputDialog(
                "HTMLNotepadFX",
                "Retrieve HTML file from the web",
                "Enter a valid web address of a HTML file to download and display.\n" +
                "Embedded objects (such as images) won't be downloaded in the process.\n\n",
                "\nWarning!\nAny unsaved changes in the current file will be lost!",
                "Retrieve",
                "http://"
        );

        if (url != null) try {  // if the user has clicked ok
            Platform.runLater(() -> {   // set the waiting cursor
                Stage stage = (Stage) textEdit.getScene().getWindow();
                stage.getScene().setCursor(Cursor.WAIT);
            });

            URL fileURL = new URL(url); // get the URL
            InputStream inputStream = fileURL.openStream();
            Scanner scanner = new Scanner(inputStream);

            textEdit.setHtmlText(""); // clean the textEdit first
            MainFX.setTitle("Untitled - HTMLNotepadFX", MainFX.currentStage);
            modified = false; // the file hasn't been modified yet

            file = null;

            while (scanner.hasNextLine()) // get the text and append it to textEdit
                appendHtmlText(textEdit, scanner.nextLine());
        } catch (IOException | IllegalArgumentException e) {
            Dialogs.errorDialog(
                    "HTMLNotepadFX",
                    "Error retrieving HTML file",
                    "An error has occurred while attempting to \n" +
                            "retrieve the specified HTML file:\n" +
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
    private void appendHtmlText(HTMLEditor editor, String text) {
        editor.setHtmlText(editor.getHtmlText() + text);
        fileModified();
    }

    /**
     * Insert a web image to textEdit
     */
    public void insertImage() {
        Optional<Pair<String, String>> result =
                Dialogs.doubleInputDialog(
                        "HTMLNotepadFX",
                        "Insert an image",
                        "Insert an image into the document",
                        "Insert",
                        "Enter image address",
                        "Enter image alternative text",
                        "Address:", "Alt Text:");

        result.ifPresent(bothFields -> {
            String compiledImageAddress = "<img src=\"" + bothFields.getKey() + "\" alt=\""
                    + bothFields.getValue() + "\" />";
            appendHtmlText(textEdit, compiledImageAddress);
        });
    }

    /**
     * Insert a hyperlink to textEdit
     */
    public void insertLink() {
        Optional<Pair<String, String>> result =
                Dialogs.doubleInputDialog(
                        "HTMLNotepadFX",
                        "Insert hyperlink",
                        "Insert a hyperlink into the document",
                        "Insert",
                        "Enter link address",
                        "Enter link text",
                        "Address:", "Text:");

        result.ifPresent(bothFields -> {
            String compiledAddress = "<a href=\"" + bothFields.getKey() + "\">"
                    + bothFields.getValue() + "</a>";
            appendHtmlText(textEdit, compiledAddress);
        });
    }

    /**
     * Insert a JavaScript script to textEdit
     */
    public void insertScript() {
        String scriptText = Dialogs.textAreaInputDialog(
                "HTMLNotepadFX",
                "Insert a script",
                "Warning!\n" +
                        "Scripts can be harmful and some browsers will block them!\n",
                "Insert",
                "Enter script code"
        );

        if (scriptText != null) {
            String compiledScriptText = "<script> " + scriptText + " </script>";
            appendHtmlText(textEdit, compiledScriptText);
        }
    }

    /**
     * Insert a script alternative text that will be displayed if the browser
     * blocks / does not support JavaScript scripts
     */
    public void insertScriptAltText() {
        String scriptAltText = Dialogs.textAreaInputDialog(
                "HTMLNotepadFX",
                "Insert script alternative text",
                "This text will be displayed instead of script result\n" +
                        "if the browser blocks / doesn't support JavaScript scripts.",
                "Insert",
                "Enter something like \"Your browser does not support" +
                        "JavaScript\"..."
        );

        if (scriptAltText != null) {
            String compiledScriptAltText = "<noscript> " + scriptAltText + " </noscript>";
            appendHtmlText(textEdit, compiledScriptAltText);
        }
    }

    /**
     * Insert a HTML quote to textEdit
     */
    public void insertQuote() {
        String quoteText = Dialogs.textAreaInputDialog(
                "HTMLNotepadFX",
                "Insert a quote",
                "Enter a quote to insert:",
                "Insert",
                "Enter a quote..."
        );

        if (quoteText != null) {
            String compiledHTMLQuote = "<q> " + quoteText + " </q>";
            appendHtmlText(textEdit, compiledHTMLQuote);
        }
    }

    /**
     * Insert scrolling text (marquee tag) to textEdit
     */
    public void insertScrollingText() {
        String marqueeText = Dialogs.inputDialog(
                "HTMLNotepadFX",
                "Insert scrolling text",
                "Enter the text to insert:",
                "Text"
        );

        if (marqueeText != null) {
            String compiledHTMLMarquee = "<marquee> " + marqueeText + " </marquee>";
            appendHtmlText(textEdit, compiledHTMLMarquee);
        }
    }

    /**
     * Insert a HTML symbol to textEdit
     */
    public void insertSymbol() {
        String symbolCode = Dialogs.inputDialog(
                "HTMLNotepadFX",
                "Insert a symbol",
                "Enter a symbol code to insert:\n" +
                        "Symbol codes must end with a semicolon!",
                "&symbol;"
        );

        if (symbolCode != null)
            appendHtmlText(textEdit, symbolCode);
    }

    /**
     * Insert a code tag to textEdit
     */
    public void insertCode() {
        String code = Dialogs.textAreaInputDialog(
                "HTMLNotepadFX",
                "Insert source code",
                "Insert some text to be displayed in the <code> tag:",
                "Insert",
                "Enter some code..."
        );

        if (code != null) {
            String codeText = "<code> " + code + " </code>";
            appendHtmlText(textEdit, codeText);
        }
    }

    /**
     * Insert an embedded website to textEdit
     */
    public void insertEmbeddedWebsite() {
        String websiteAddress = Dialogs.inputDialog(
                "HTMLNotepadFX",
                "Embed a website",
                "Enter web address of the website to embed:",
                "http://"
        );

        if (websiteAddress != null) {
            String IFrameTag = "<iframe src=\"" + websiteAddress + "\" height=\"300\" " +
                    "width=\"500\"></iframe>";
            appendHtmlText(textEdit, IFrameTag);
        }
    }

    /**
     * Insert a custom HTML tag to textEdit
     */
    public void insertHtmlTag() {
        String htmlTag = Dialogs.textAreaInputDialog(
                "HTMLNotepadFX",
                "Insert a custom HTML tag",
                "Refer to HTML documentation for valid values.\n" +
                        "\nWarning!\n" +
                        "Some browsers may block certain tags for security reasons.",
                "Insert",
                "Enter something like \"<tag>text</tag>\""
        );

        if (htmlTag != null) appendHtmlText(textEdit, htmlTag);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //   P R I N T I N G
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Print the content of the editor
     */
    public void print() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job.showPrintDialog(textEdit.getScene().getWindow())) {
            textEdit.print(job);
            job.endJob();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //   O P T I O N S   M E N U
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Open a window with HTML source of the text in textEdit
     */
    public void openSource() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("fxml/HTMLSource.fxml"));
            Stage stage = new Stage();
            stage.setTitle("HTML Source Code");

            // get the filename if possible
            if (file != null)
                stage.setTitle("HTML Source Code - " + file.getName());

            // get the HTML source code
            HTMLSource.htmlSourceText = textEdit.getHtmlText();
            Scene scene = new Scene(root, 822, 562);

            // use experimental UI if enabled
            if (experimentalUI)
                scene.getStylesheets().add("fxml/Styles.css");

            stage.setScene(scene);
            stage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) stage.close();
            });
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed loading HTML source code window: "
                    + e.getMessage());
        }
    }

    /**
     * Open a Quick Calculator window
     */
    public void openQuickCalculator() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("fxml/QuickCalculator.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Quick Calculator");

            Scene scene = new Scene(root, 449, 110);
            if (experimentalUI)
                scene.getStylesheets().add("fxml/Styles.css");
            stage.setScene(scene);

            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed loading Quick Calculator: "
                    + e.getMessage());
        }
    }

    /**
     * Toggle experimental user interface (custom control styling)
     */
    public void toggleExperimentalInterface() {
        experimentalUI = checkboxExperimentalUI.isSelected();
        if (experimentalUI)
            MainFX.currentStage.getScene().getStylesheets().add("fxml/Styles.css");
        else
            MainFX.currentStage.getScene().getStylesheets().clear();
    }

    /**
     * Set saving settings on or off
     */
    public void toggleSaveSettings() {
        saveSettings = checkboxSaveSettings.isSelected();
        if (!saveSettings) {    // try deleting the settings file if user selected to not save the settings
            boolean doDeleteFile = Dialogs.confirmationDialog(
                    "HTMLNotepadFX",
                    "HTMLNotepadFX",
                    "Would you also like to delete the settings file?"
            );

            // the user has chosen to delete the file
            if (doDeleteFile) {
                File file = new File(VersionData.SETTINGS_LOCATION);
                if (file.delete())
                    System.out.println("Removing settings file done.");
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
        if (opacity < 0.01f) // do not make the window invisible
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
            Parent root = FXMLLoader.load(getClass().getResource("fxml/About.fxml"));
            Stage stage = new Stage();
            stage.setTitle("About HTMLNotepadFX");

            Scene scene = new Scene(root, 638, 281);
            if (experimentalUI)
                scene.getStylesheets().add("fxml/Styles.css");
            stage.setScene(scene);

            stage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) stage.close();
            });
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed loading about dialog: "
                    + e.getMessage());
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
        if (modified) {
            confirmedClose = Dialogs.confirmationDialog(
                    "HTMLNotepadFX",
                    "Warning",
                    "All unsaved changes will be lost! Continue?");
        } else confirmedClose = true;

        if (confirmedClose) {
            saveSettings();
            System.exit(0);
        }
    }
}
