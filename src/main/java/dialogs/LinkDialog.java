package dialogs;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

/**
 * A link insertion dialog with a collapsible preview pane
 */
public class LinkDialog extends CustomDialog<String> {

    /**
     * Constructs a new LinkDialog instance
     *
     * @param caption    the title bar text
     * @param header     the dialog box header text
     * @param body       the dialog box body text / content
     * @param stylesheet the stylesheet to use or <code>null</code>
     *                   to use the default
     */
    public LinkDialog(String caption, String header, String body, String stylesheet) {
        super(caption, header, body, stylesheet);
    }

    private ButtonType mainButtonType;
    private TextField field1, field2;

    /**
     * Initializes the controls
     */
    @Override
    public void setControls() {
        // set button types
        mainButtonType = new ButtonType("Insert", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(mainButtonType, ButtonType.CANCEL);

        // create other controls
        GridPane pane = new GridPane(), textPane = new GridPane(),
                controlPane = new GridPane(), previewPane = new GridPane();
        field1 = new TextField();
        field2 = new TextField();
        WebView preview = new WebView();

        textPane.setHgap(10);
        setGridInsets(controlPane);
        preview.setMaxHeight(250);
        preview.setMaxWidth(400);

        field1.setPromptText("Link address");
        field1.setMinWidth(260);
        field2.setPromptText("Link text");
        field2.setMinWidth(260);

        textPane.add(new Label(dialog.getContentText()), 0, 0);
        controlPane.add(new Label("Address:"), 0, 0);
        controlPane.add(field1, 1, 0);
        controlPane.add(new Label("Text:"), 0, 1);
        controlPane.add(field2, 1, 1);
        Node checkBtn = new Button("Check...");
        controlPane.add(checkBtn, 2, 0);
        previewPane.add(preview, 0, 0);

        pane.add(textPane, 0, 0);
        pane.add(controlPane, 0, 1);
        dialog.getDialogPane().setExpandableContent(previewPane);

        // reload the preview when Check is clicked
        checkBtn.setOnMouseClicked(e -> {
            Scene s = checkBtn.getScene().getWindow().getScene();
            s.setCursor(Cursor.WAIT);
            try {
                preview.getEngine().load(field1.getText());
            } catch (Exception ignored) { }
            s.setCursor(Cursor.DEFAULT);
        });

        // disable the main button until some text is entered into the fields
        Node mainButton = dialog.getDialogPane().lookupButton(mainButtonType);
        mainButton.setDisable(true);
        field1.textProperty().addListener((obs, oldVal, newVal) -> {
            field2.setText(field1.getText());
            mainButton.setDisable(newVal.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(pane);
        previewPane.setStyle("-fx-border-color: rgb(59, 146, 219)");
        Platform.runLater(field1::requestFocus);
    }

    /**
     * Sets the result converter
     */
    @Override
    public void setResultConverter() {
        dialog.setResultConverter(btn -> {
            if (btn == mainButtonType)
                return htmlLink(field1.getText(), field2.getText());
            return null;
        });
    }

    /**
     * Returns the HTML code for this link
     *
     * @param link the link location
     * @param text the text to be linked
     * @return HTML code of the link
     */
    private static String htmlLink(String link, String text) {
        return "<a href=\"" + link + "\">" + text + "</a>";
    }

}
