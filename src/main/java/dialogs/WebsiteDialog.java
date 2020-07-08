package dialogs;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

public class WebsiteDialog extends CustomDialog<String> {

    /**
     * Constructs a new WebsiteDialog instance
     *
     * @param caption    the title bar text
     * @param header     the dialog box header text
     * @param body       the dialog box body text / content
     * @param stylesheet the stylesheet to use or <code>null</code>
     */
    public WebsiteDialog(String caption, String header, String body, String stylesheet) {
        super(caption, header, body, stylesheet);
    }

    private TextField textField;

    /**
     * Initializes the controls
     */
    @Override
    public void setControls() {
        // set button types
        mainButtonType = new ButtonType("Load", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(mainButtonType, ButtonType.CANCEL);

        // create other controls
        GridPane pane = new GridPane(), textPane = new GridPane(),
                controlPane = new GridPane(), previewPane = new GridPane();
        textField = new TextField();
        WebView preview = new WebView();

        textPane.setHgap(10);
        DialogUtils.setGridInsets(controlPane);
        preview.setMaxHeight(250);
        preview.setMaxWidth(400);

        textField.setPromptText("www.example.org");
        textField.setMinWidth(260);

        textPane.add(new Label(dialog.getContentText()), 0, 0);
        controlPane.add(new Label("Website address:"), 0, 0);
        controlPane.add(textField, 1, 0);
        previewPane.add(preview, 0, 0);

        pane.add(textPane, 0, 0);
        pane.add(controlPane, 0, 1);
        dialog.getDialogPane().setExpandableContent(previewPane);

        // reload the preview when the address changes
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                preview.getEngine().load(textField.getText());
            } catch (Exception ignored) { }
            Platform.runLater(textField::requestFocus);
        });

        // disable the main button until some text is entered into the fields
        Node mainButton = dialog.getDialogPane().lookupButton(mainButtonType);
        mainButton.setDisable(true);
        textField.textProperty().addListener((obs, oldVal, newVal)
                -> mainButton.setDisable(newVal.trim().isEmpty()));

        dialog.getDialogPane().setContent(pane);
        // rename the "Show Details" hyperlink to "Show Preview"
        Platform.runLater(() -> DialogUtils.setDetailsButtonText(dialog
                .getDialogPane(), "Show Preview", "Hide Preview"));
        previewPane.setStyle("-fx-border-color: rgb(59, 146, 219)");
        Platform.runLater(textField::requestFocus);
    }

    /**
     * Sets the result converter
     */
    @Override
    public void setResultConverter() {
        dialog.setResultConverter(btn -> btn == mainButtonType
                ? textField.getText() : null);
    }

}
