import dialogs.ImageDialog;
import dialogs.LinkDialog;
import dialogs.simple.CodeDialog;
import dialogs.simple.CustomTagDialog;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

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

    private double confVersion = VersionData.CONFIG_VERSION;
    private List<String> recentFiles = new ArrayList<>();
    private final String stylesheet = "Styles.css";

    /**
     * Loads settings from the config file
     *
     * @see VersionData#CONFIG_LOCATION
     */
    void loadSettings() {
        Properties settings = new Properties();
        try {
            settings.loadFromXML(new FileInputStream(VersionData.CONFIG_LOCATION));
            confVersion = Double.parseDouble(settings.getProperty("config_version"));
            if (confVersion > VersionData.CONFIG_VERSION) {
                Dialogs.alert("Error", "Config file version mismatch",
                        "The config file reports the version " + confVersion +
                                " while this program is still using " +
                                VersionData.CONFIG_VERSION +
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
            var fileOut = new FileOutputStream(new File(VersionData.CONFIG_LOCATION));
            settings.storeToXML(fileOut, "");
            fileOut.close();

            // manually hide the properties file on windows
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Path path = Paths.get(VersionData.CONFIG_LOCATION);
                Files.setAttribute(path, "dos:hidden", true);
            }
        } catch (IOException e) {
            System.err.println("Saving settings failed: " + e.getMessage());
        }
    }

    /// FILE MANAGEMENT /////////////////////////////////////////////////////////////////

    private File file;
    private boolean modified;

    /**
     * Creates a blank file by emptying textEdit
     */
    public void newFile() {
        if (modified) {
            boolean confirmedNewFile = Dialogs.confirmationDialog(
                    "Confirmation", "Warning",
                    "All unsaved changes will be lost! Continue?");
            if (!confirmedNewFile) return;
        }
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
        chooser.getExtensionFilters().addAll(   // set file extensions filter
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
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
        chooser.getExtensionFilters().addAll(   // set extensions filters
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
        file = chooser.showSaveDialog(MainFX.currentStage);
        if (file != null) saveFile();
    }

    /**
     * Exports the HTML code into a plain text file
     */
    public void exportSource() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export HTML source to a file");
        chooser.getExtensionFilters().addAll(   // set extension filters
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

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
        MainFX.setTitle(MainFX.currentStage.getTitle() + " (Modified)", MainFX.currentStage);
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
     * Fetches an existing web page from the web and renders
     * it in textEdit
     */
    public void loadWebPage() {
        String url = Dialogs.wideInputDialog(
                "HTMLNotepadFX",
                "Load web page",
                "Enter a valid address of an existing page on\n" +
                        "the web to load into the editor for editing.",
                "\nWarning!\nAny unsaved changes in the current file will be lost!",
                "Load",
                "http://");
        if (url == null) return;
        Platform.runLater(() -> textEdit.getScene().getWindow().getScene()
                .setCursor(Cursor.WAIT));

        WebView webView = (WebView) textEdit.lookup("WebView");
        if (webView != null) {
            webView.getEngine().load(url);
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
        ImageDialog dlg = new ImageDialog("Insert", "Insert an image",
                "Insert an image to the document", stylesheet);
        Optional<String> input = dlg.run();
        if (input.isEmpty()) return;
        appendHtmlText(textEdit, input.get());
    }

    /**
     * Inserts a hyperlink
     */
    public void insertLink() {
        LinkDialog dlg = new LinkDialog("Insert", "Insert a hyperlink",
                "Insert a hyperlink to the document", stylesheet);
        Optional<String> input = dlg.run();
        if (input.isEmpty()) return;
        appendHtmlText(textEdit, input.get());
    }

    /**
     * Inserts a script
     */
    public void insertScript() {
        String scriptText = Dialogs.textAreaInputDialog(
                "Insert",
                "Insert a script",
                "Warning!\nScripts can be harmful and some browsers will block them!\n",
                "Insert",
                "Enter script code");
        if (scriptText == null) return;
        appendHtmlText(textEdit, "<script> " + scriptText + " </script>");
    }

    /**
     * Inserts a script alternative text
     */
    public void insertScriptAltText() {
        String altText = Dialogs.textAreaInputDialog(
                "Insert",
                "Insert script alternative text",
                "This text will be displayed instead of script result " +
                        "if the browser blocks / doesn't support\nJavaScript scripts.",
                "Insert",
                "Enter something like \"Your browser does not support JavaScript\"...");
        if (altText == null) return;
        appendHtmlText(textEdit, "<noscript> " + altText + " </noscript>");
    }

    /**
     * Inserts a quote
     */
    public void insertQuote() {
        QuoteDialog dlg = new QuoteDialog("Insert", "Insert a quote",
                "Enter a quote to insert:", stylesheet);
        Optional<String> input = dlg.run();
        if (input.isEmpty()) return;
        appendHtmlText(textEdit, input.get());
    }

    /**
     * Inserts scrolling text (marquee tag)
     */
    public void insertScrollingText() {
        String marqueeText = Dialogs.inputDialog(
                "Insert",
                "Insert scrolling text",
                "Enter the text to insert:",
                "Text");
        if (marqueeText == null) return;
        appendHtmlText(textEdit, "<marquee> " + marqueeText + " </marquee>");
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
        CodeDialog dlg = new CodeDialog("Insert",
                "Insert code", "Enter code to be displayed within" +
                " <code> tags:", stylesheet);
        Optional<String> input = dlg.run();
        if (input.isEmpty()) return;
        appendHtmlText(textEdit, input.get());
    }

    /**
     * Insert the current system date and time
     */
    public void insertDateTime() {
        appendHtmlText(textEdit, new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss"))
                .format(Calendar.getInstance().getTime()));
    }

    /**
     * Inserts an embedded website (iframe)
     */
    public void insertEmbeddedWebsite() {
        String websiteAddress = Dialogs.inputDialog(
                "Insert",
                "Embed a website",
                "Enter web address of the website to embed:",
                "http://");
        if (websiteAddress == null) return;
        appendHtmlText(textEdit, "<iframe src=\"" + websiteAddress
                + "\" height=\"300\" " + "width=\"500\"></iframe>");
    }

    /**
     * Inserts a custom HTML tag
     */
    public void insertHtmlTag() {
        CustomTagDialog dlg = new CustomTagDialog("Insert",
                "Insert a custom HTNL tag",
                "Enter any valid HTML in the field below.\nKeep in mind that" +
                        " some browsers might block certain\ntags for security reasons.",
                stylesheet);
        Optional<String> input = dlg.run();
        if (input.isEmpty()) return;
        appendHtmlText(textEdit, input.get());
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
        if (oldUiBox.isSelected())
            MainFX.currentStage.getScene().getStylesheets().clear();
        else MainFX.currentStage.getScene().getStylesheets().add("Styles.css");
    }

    /**
     * Toggles saving settings
     */
    public void toggleSaveSettings() {
        if (saveSettingsBox.isSelected()) return;
        // ask to delete the settings file when saving is disabled
        boolean doDeleteFile = Dialogs.confirmationDialog(
                "Confirmation", "Confirmation",
                "Would you also like to delete the settings file?");
        if (!doDeleteFile) return;
        if (new File(VersionData.CONFIG_LOCATION).delete())
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
        boolean confirmedClose = true;
        if (modified) confirmedClose = Dialogs.confirmationDialog(
                "Confirmation", "Warning",
                "All unsaved changes will be lost! Continue?");
        if (!confirmedClose) return;
        saveSettings();
        System.exit(0);
    }
}
