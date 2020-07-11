import dialogs.ImageDialog;
import dialogs.LinkDialog;
import dialogs.ScriptDialog;
import dialogs.WebsiteDialog;
import dialogs.simple.CodeDialog;
import dialogs.simple.CustomTagDialog;
import dialogs.simple.MarqueeDialog;
import dialogs.simple.QuoteDialog;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.Dialogs;
import util.FileIO;
import util.VersionData;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static util.VersionData.CONFIG_LOCATION;
import static util.VersionData.CONFIG_VERSION;
import static util.VersionData.HTML_FILE_EXTENSIONS;
import static util.VersionData.stylesheet;

/**
 * Controller class for MainWindow.fxml
 */
public class Controller extends Component {

    // initialize controls
    public HTMLEditor textEdit = new HTMLEditor();
    public Menu recentMenu = new Menu();
    public Slider opacitySlider = new Slider();
    public CheckMenuItem disableMouseBox = new CheckMenuItem(),
            reloadLastBox = new CheckMenuItem(),
            saveSettingsBox = new CheckMenuItem(),
            oldUiBox = new CheckMenuItem();

    private double confVersion = CONFIG_VERSION;
    private List<String> recentFiles = new ArrayList<>();

    /**
     * Loads settings from the config file
     *
     * @see VersionData#CONFIG_LOCATION
     */
    void loadSettings() {
        Properties settings = new Properties();
        try {
            settings.loadFromXML(new FileInputStream(CONFIG_LOCATION));
            confVersion = Double.parseDouble(settings.getProperty("config_version"));
            if (confVersion > CONFIG_VERSION) {
                Dialogs.alert("Error", "Config file version mismatch",
                        "The config file reports the version " + confVersion +
                                " while this program is still using " +
                                CONFIG_VERSION +
                                "\nSettings will not be loaded.", Alert.AlertType.ERROR);
                return;
            }

            textEdit.setMouseTransparent(Boolean.parseBoolean(settings.getProperty("mouse_disabled")));
            opacitySlider.setValue(Float.parseFloat(settings.getProperty("opacity")) * 100);
            oldUiBox.setSelected(Boolean.parseBoolean(settings.getProperty("old_ui")));
            toggleOldUi();

            // attempt to reload the last used file
            String lastFileName = settings.getProperty("last_file");
            if (lastFileName != null) openFile(new File(lastFileName));
            reloadLastBox.setSelected(lastFileName != null);

            String recent = settings.getProperty("recent_files");
            recentFiles.addAll(Arrays.asList(recent.split(";")));

            for (String s : recentFiles) {
                MenuItem recentFile = new MenuItem(s);
                recentFile.setOnAction(e -> {
                    try {
                        openFile(new File(recentFile.getText()));
                    } catch (Exception ignored) { }
                });
                recentMenu.getItems().add(recentFile);
            }
        } catch (Exception e) {
            System.err.println("Loading settings failed: " + e.getMessage());
        }
        if (opacitySlider.getValue() < 0.1f) opacitySlider.setValue(0.1f);
    }

    /**
     * Saves settings to the config file
     *
     * @see VersionData#CONFIG_LOCATION
     */
    void saveSettings() {
        if (!saveSettingsBox.isSelected()) return;
        Properties settings = new Properties();
        settings.setProperty("config_version", String.valueOf(confVersion));
        settings.setProperty("mouse_disabled", String.valueOf(textEdit.isMouseTransparent()));
        settings.setProperty("opacity", String.valueOf(MainFX.currentStage.getOpacity()));
        settings.setProperty("old_ui", String.valueOf(oldUiBox.isSelected()));

        // save the current file name
        if (file != null && reloadLastBox.isSelected())
            settings.setProperty("last_file", file.getAbsolutePath());

        StringBuilder recent = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i == recentFiles.size()) break;
            recent.append(';').append(recentFiles.get(i));
        }
        settings.setProperty("recent_files", recent.toString());

        try {
            var fileOut = new FileOutputStream(new File(CONFIG_LOCATION));
            settings.storeToXML(fileOut, "");
            fileOut.close();

            // manually hide the properties file on windows
            if (System.getProperty("os.name").toLowerCase().contains("win"))
                Files.setAttribute(Paths.get(CONFIG_LOCATION),
                        "dos:hidden", true);
        } catch (IOException e) {
            System.err.println("Saving settings failed: " + e.getMessage());
        }
    }

    /// FILE MANAGEMENT /////////////////////////////////////////////////////////////////

    private File file;
    private boolean modified;

    /**
     * Asks the user whether they want to keep or discard the changes
     * they've made to the file
     *
     * @param modified the modified status of the file
     * @return true if the user confirmed to <strong>discard</strong>
     * changes
     */
    private static boolean askKeepChanges(boolean modified) {
        return modified && !Dialogs.confirmationDialog(
                "Confirmation", "Warning",
                "All unsaved changes will be lost! Continue?");
    }

    /**
     * Creates a blank file by emptying textEdit
     */
    public void newFile() {
        if (askKeepChanges(modified)) return;
        textEdit.setHtmlText("");
        MainFX.setTitle("Untitled - HTMLNotepadFX", MainFX.currentStage);
        modified = false;
        file = null;
    }

    /**
     * Displays a file open dialog
     */
    public void openFileDialog() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(HTML_FILE_EXTENSIONS);
        file = chooser.showOpenDialog(MainFX.currentStage);
        if (file != null) openFile(file);
    }

    /**
     * Opens this file and renders its content in textEdit
     *
     * @param file the file to open
     */
    private void openFile(File file) {
        if (!modified) {
            textEdit.setHtmlText(FileIO.loadFile(file));
            MainFX.setTitle(file.getName() + " - HTMLNotepadFX", MainFX.currentStage);
            modified = false;
        } else {    // if the file has been modified
            boolean confirmed = Dialogs.confirmationDialog(
                    "Confirmation", "Warning",
                    "All unsaved changes will be lost! Continue?");
            if (confirmed) {
                textEdit.setHtmlText(FileIO.loadFile(file));
                MainFX.setTitle(file.getName() + " - HTMLNotepadFX", MainFX.currentStage);
                modified = false;
            }
        }
    }

    /**
     * Saves the current file
     */
    public void saveFile() {
        if (file == null) {
            saveAs();
            return;
        }
        FileIO.saveFile(file, textEdit.getHtmlText());
        // remove "modified" from the title bar
        MainFX.setTitle(file.getName() + " - HTMLNotepadFX", MainFX.currentStage);
        modified = false;

        boolean recent = recentFiles.stream().anyMatch(s -> s.equals(file.getAbsolutePath()));
        if (recent) return;

        recentFiles.add(file.getAbsolutePath());
        MenuItem recentFile = new MenuItem(file.getAbsolutePath()); // add the menu item
        recentFile.setOnAction(e -> {
            try {
                openFile(new File(recentFile.getText()));
            } catch (Exception ignored) { }
        });
        recentMenu.getItems().add(recentFile);

    }

    /**
     * Displays a file chooser dialog
     */
    public void saveAs() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(HTML_FILE_EXTENSIONS);
        file = chooser.showSaveDialog(MainFX.currentStage);
        if (file != null) saveFile();
    }

    /**
     * Exports the HTML code into a plain text file
     */
    public void exportSource() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export HTML source code");
        chooser.getExtensionFilters().addAll(HTML_FILE_EXTENSIONS);
        if (file != null) // original filename + .txt
            chooser.setInitialFileName(file.getName() + ".txt");

        File sourceFile = chooser.showSaveDialog(MainFX.currentStage);
        if (sourceFile != null) FileIO.saveFile(sourceFile, textEdit.getHtmlText());
    }

    /**
     * Appends "(Modified)" to the title bar
     */
    public void fileModified() {
        if (modified) return;
        MainFX.setTitle(MainFX.currentStage.getTitle()
                + " (Modified)", MainFX.currentStage);
        modified = true;
    }

    /**
     * Empties the Recent Files menu and recentFiles list
     */
    @SuppressWarnings("SuspiciousListRemoveInLoop")
    public void clearRecentMenu() {
        for (int i = 2; i < recentMenu.getItems().size(); i++)
            recentMenu.getItems().remove(i);
        recentFiles = new ArrayList<>();
    }

    /**
     * Fetches an existing web page from the web and renders it
     */
    public void loadWebPage() {
        Optional<String> input = new WebsiteDialog("Download web page",
                "Download an existing web page",
                "Enter a valid web address of an existing page" +
                        " to download.\n\nWarning! Any unsaved changes" +
                        "to the current file will be lost!\n\n", stylesheet).run();
        if (input.isEmpty()) return;
        Platform.runLater(() -> textEdit.getScene().getWindow().getScene()
                .setCursor(Cursor.WAIT));

        WebView webView = (WebView) textEdit.lookup("WebView");
        if (webView != null) {
            webView.getEngine().load(input.get());
            MainFX.setTitle("Untitled - HTMLNotepadFX", MainFX.currentStage);
            modified = false;
            file = null;
        }

        Platform.runLater(() -> textEdit.getScene().getWindow().getScene()
                .setCursor(Cursor.DEFAULT));
    }

    /// EDITING AND INSERTING OBJECTS ///////////////////////////////////////////////////

    /**
     * Appends the provided HTML text to this editor
     *
     * @param editor the editor that'll get the text
     * @param text   the text to append
     */
    private void appendHtmlText(HTMLEditor editor, String text) {
        editor.setHtmlText(editor.getHtmlText() + text);
        fileModified();
    }

    /**
     * Inserts a web image
     */
    public void insertImage() {
        new ImageDialog("Insert", "Insert an image",
                "Insert an image to the document", stylesheet).run()
                .ifPresent(s -> appendHtmlText(textEdit, s));
    }

    /**
     * Inserts a hyperlink
     */
    public void insertLink() {
        new LinkDialog("Insert", "Insert a hyperlink",
                "Insert a hyperlink to the document", stylesheet).run()
                .ifPresent(s -> appendHtmlText(textEdit, s));
    }

    /**
     * Inserts a JavaScript script
     */
    public void insertScript() {
        new ScriptDialog("Insert", "Insert a script",
                "Insert a JavaScript script to the document", stylesheet).run()
                .ifPresent(s -> appendHtmlText(textEdit, s));
    }

    /**
     * Inserts a quote
     */
    public void insertQuote() {
        new QuoteDialog("Insert", "Insert a quote",
                "Enter a quote to insert:", stylesheet).run()
                .ifPresent(s -> appendHtmlText(textEdit, s));
    }

    /**
     * Inserts scrolling text (&lt;marquee&gt; tag)
     */
    public void insertScrollingText() {
        new MarqueeDialog("Insert", "Insert scrolling text",
                "Enter the text to insert:", stylesheet).run()
                .ifPresent(s -> appendHtmlText(textEdit, s));
    }

    /**
     * Inserts an HTML symbol
     */
    public void insertSymbol() {
        String symbolCode = Dialogs.inputDialog(
                "Insert",
                "Insert a symbol",
                "Enter a symbol code to insert:\n" +
                        "Symbol codes must end with a semicolon!",
                "&symbol;");
        if (symbolCode != null) appendHtmlText(textEdit, symbolCode);
    }

    /**
     * Insert a code tag to textEdit
     */
    public void insertCode() {
        new CodeDialog("Insert",
                "Insert code", "Enter code to be displayed within" +
                " <code> tags:", stylesheet).run()
                .ifPresent(s -> appendHtmlText(textEdit, s));
    }

    /**
     * Insert the current system date and time
     */
    public void insertDateTime() {
        appendHtmlText(textEdit, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));
    }

    /**
     * Inserts an embedded website (iframe)
     */
    public void insertEmbeddedWebsite() {
        new WebsiteDialog("Insert",
                "Embed a website", "Enter the web address of an existing" +
                " website to embed into the\ndocument:", stylesheet).run()
                .ifPresent(s -> appendHtmlText(textEdit, "<iframe src=\""
                        + s + "\" height=\"300\" " + "width=\"500\"></iframe>"));
    }

    /**
     * Inserts a custom HTML tag
     */
    public void insertHtmlTag() {
        new CustomTagDialog("Insert",
                "Insert a custom HTML tag",
                "Enter any valid HTML in the field below.\nKeep in mind that" +
                        " some browsers might block certain\ntags for security reasons.",
                stylesheet).run().ifPresent(s -> appendHtmlText(textEdit, s));
    }

    /// PRINTING ////////////////////////////////////////////////////////////////////////

    /**
     * Prints the current file
     */
    public void print() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job.showPrintDialog(textEdit.getScene().getWindow())) {
            textEdit.print(job);
            job.endJob();
        }
    }

    /// OPTIONS MENU ////////////////////////////////////////////////////////////////////

    /**
     * Displays HTML source code
     */
    public void openSource() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("HTMLSource.fxml"));
            Stage stage = new Stage();
            stage.setTitle("HTML Source Code");
            if (file != null) stage.setTitle("HTML Source Code - " + file.getName());
            HTMLSource.htmlSourceText = textEdit.getHtmlText();
            toggleNewUi(stage, new Scene(root, 822, 562));
        } catch (IOException e) {
            System.err.println("Failed loading HTML source code window: " + e.getMessage());
        }
    }

    /**
     * Toggles between the default and non-styled UI
     */
    public void toggleOldUi() {
        stylesheet = oldUiBox.isSelected() ? "" : "Styles.css";
        if (stylesheet.trim().isEmpty())
            MainFX.currentStage.getScene().getStylesheets().clear();
        else MainFX.currentStage.getScene().getStylesheets().add(stylesheet);
    }

    /**
     * Toggles saving settings
     */
    public void toggleSaveSettings() {
        if (saveSettingsBox.isSelected()) return;
        // ask to delete the settings file when saving is disabled
        boolean confirmed = Dialogs.confirmationDialog(
                "Confirmation", "Confirmation",
                "Would you also like to delete the settings file?");
        if (!confirmed) return;
        if (new File(CONFIG_LOCATION).delete())
            System.out.println("Removing settings file done.");
    }

    /**
     * Disables mouse interaction
     */
    public void disableMouse() {
        textEdit.setMouseTransparent(disableMouseBox.isSelected());
    }

    /**
     * Sets the opacity of MainWindow
     */
    public void setOpacity() {
        float opacity = (float) opacitySlider.getValue() / 100;
        if (opacity < 0.01f) opacity = 0.01f; // do not make the window invisible
        MainFX.currentStage.setOpacity(opacity);
    }

    /**
     * Toggles the new UI style, for this stage and scene
     *
     * @param stage the wrapper stage
     * @param scene the scene which this applies to
     */
    private void toggleNewUi(Stage stage, Scene scene) {
        if (!oldUiBox.isSelected()) scene.getStylesheets().add("Styles.css");
        stage.setScene(scene);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) stage.close();
        });
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    /// INFO MENU ///////////////////////////////////////////////////////////////////////

    /**
     * Displays the about box
     */
    public void showAboutDialog() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("About.fxml"));
            Stage stage = new Stage();
            stage.setTitle("About HTMLNotepadFX");
            toggleNewUi(stage, new Scene(root, 638, 281));
        } catch (IOException e) {
            System.err.println("Failed loading about dialog: " + e.getMessage());
        }
    }

    /// CLOSING AND EXITING THE PROGRAM /////////////////////////////////////////////////

    /**
     * Closes the program
     */
    public void close() {
        if (askKeepChanges(modified)) return;
        saveSettings();
        System.exit(0);
    }
}
