package dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.Optional;

/**
 * A basic image insertion dialog box with preview
 */
public class ImageDialog extends CustomDialog<String> {

    /**
     * Constructs a new ImageDialog instance
     *
     * @param caption the title bar text
     * @param header  the dialog box header text
     * @param body    the dialog box body text / content
     */
    public ImageDialog(String caption, String header, String body) {
        super(caption, header, body);
    }

    private ButtonType mainButtonType;
    private String input1, input2;
    private boolean local;

    /**
     * Initializes the controls
     */
    @Override
    public void setControls() {
        // set button types
        mainButtonType = new ButtonType("Insert", ButtonBar.ButtonData.OK_DONE);
        this.dialog.getDialogPane().getButtonTypes().addAll(mainButtonType, ButtonType.CANCEL);

        GridPane pane = new GridPane(), controlPane = new GridPane(),
                imgBoxPane = new GridPane(), selectionPane = new GridPane();
        TextField field1 = new TextField(), field2 = new TextField();
        WebView imgBox = new WebView();
        RadioButton localBtn = new RadioButton("From this computer"),
                webBtn = new RadioButton("From the Internet");
        ToggleGroup mainGroup = new ToggleGroup();

        // create basic controls
        imgBox.setMaxSize(250, 220);
        imgBox.setZoom(0.5);
        localBtn.setToggleGroup(mainGroup);
        webBtn.setToggleGroup(mainGroup);
        webBtn.setSelected(true);

        controlPane.setHgap(10);
        controlPane.setVgap(10);
        controlPane.setPadding(new Insets(10, 10, 0, 0));
        selectionPane.setHgap(10);
        selectionPane.setVgap(10);
        selectionPane.setPadding(new Insets(10, 10, 0, 0));

        field1.setPromptText("Image path");
        field2.setPromptText("Alt text");

        controlPane.add(new Label(this.dialog.getContentText()), 0, 0);
        controlPane.add(localBtn, 0, 1);
        controlPane.add(webBtn, 0, 2);
        Label addressLabel = new Label("Address:");
        selectionPane.add(addressLabel, 0, 0);
        selectionPane.add(field1, 1, 0);
        selectionPane.add(new Label("Alt text:"), 0, 1);
        selectionPane.add(field2, 1, 1);
        controlPane.add(selectionPane, 0, 3);
        imgBoxPane.add(imgBox, 0, 0);

        pane.add(controlPane, 0, 1);
        pane.add(imgBoxPane, 1, 1);

        // disable the main button until some text is entered into the fields
        Node mainBtn = this.dialog.getDialogPane().lookupButton(mainButtonType);
        mainBtn.setDisable(true);
        field1.textProperty().addListener((obs, oldVal, newVal) -> {
            field2.setText(field1.getText());
            mainBtn.setDisable(newVal.trim().isEmpty());
            String html = getImageHtml(field1.getText(), field2.getText(),
                    localBtn.isSelected());
            imgBox.getEngine().loadContent(html);
            this.input1 = newVal;
        });

        // add the browse button
        Node browseBtn = new Button("Browse...");
        ButtonBar bar = (ButtonBar) this.dialog.getDialogPane().lookup("ButtonBar");
        if (bar != null)
            bar.getButtons().add(browseBtn);
        browseBtn.setOnMouseClicked(e -> {
            File f = browseImages(this.dialog.getDialogPane().getScene().getWindow());
            if (f == null) return;
            field1.setText(f.getAbsolutePath());
            field2.setText(f.getName());
            localBtn.setSelected(true);
            imgBox.getEngine().loadContent(getImageHtml(f.getAbsolutePath(),
                    f.getName(), true));
        });

        field2.textProperty().addListener((obs, oldVal, newVal) -> this.input2 = newVal);

        // change the value when Local/Web is selected
        localBtn.selectedProperty().addListener((obs, oldVal, newVal) -> {
            addressLabel.setText(newVal ? "Path:" : "Address:");
            local = newVal;
        });

        // add controls, add border to the preview box, request focus
        this.dialog.getDialogPane().setContent(pane);
        this.dialog.getDialogPane().getScene().getStylesheets().add("Styles.css");
        imgBoxPane.setStyle("-fx-border-color: rgb(59, 146, 219);");
        Platform.runLater(field1::requestFocus);
    }

    /**
     * Sets the result converter
     */
    @Override
    public void setResultConverter() {
        this.dialog.setResultConverter(btn -> {
            if (btn == mainButtonType)
                return getImageHtml(input1, input2, local);
            return null;
        });
    }

    /**
     * Displays an image browser dialog
     *
     * @param parent the parent window
     * @return the selected file or null if the dialog was cancelled
     */
    private static File browseImages(Window parent) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG images (*.jpg, *.jpeg)",
                        "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF images (*.gif)", "*.gif"),
                new FileChooser.ExtensionFilter("PNG images (*.png)", "*.png"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
        return chooser.showOpenDialog(parent);
    }

    /**
     * Returns the HTML code for the image in this location
     *
     * @param location image path (either a web location or a local
     *                 filesystem path)
     * @param alt      image alt text
     * @param local    set to <code>true</code> if location points to a
     *                 local filesystem location
     * @return the HTML code of the image (if the image is loaded from
     * local filesystem, it'll be read and base64 encoded into html)
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static String getImageHtml(String location, String alt, boolean local) {
        location.replace("\"", "");
        if (local) {
            String base64;
            File file = new File(location);
            try (FileInputStream stream = new FileInputStream(file)) {
                byte[] contents = new byte[(int) file.length()];
                stream.read(contents);
                base64 = Base64.getEncoder().encodeToString(contents);
            } catch (Exception ignored) {
                return "<h1><i>Image not found</i></h1>";
            }

            Optional<String> extension = Optional.of(file.getName())
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(file.getName().lastIndexOf(".") + 1));
            if (extension.isEmpty())    // assume it's png if there's no extension
                extension = Optional.of("png");

            return "<img src=\"data:image/" + extension.get() + ";base64,"
                    + base64 + "\" alt=\"" + alt + "\"</img>";
        }
        return "<img src=\"" + location + "\" alt=\"" + alt + "\"/>";
    }

}
