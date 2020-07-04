package dialogs;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

/**
 * A simple HTML tag insertion dialog with a preview pane
 */
public abstract class GenericTagDialog extends CustomDialog<String> {

    /**
     * Constructs a new GenericTagDialog instance
     *
     * @param caption    the title bar text
     * @param header     the dialog box header text
     * @param body       the dialog box body text / content
     * @param stylesheet the stylesheet to use or <code>null</code>
     */
    public GenericTagDialog(String caption, String header, String body, String stylesheet) {
        super(caption, header, body, stylesheet);
    }

    private ButtonType mainButtonType;
    private TextArea textField;

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
        textField = new TextArea();
        WebView preview = new WebView();

        textPane.setHgap(10);
        setGridInsets(controlPane);
        preview.setMaxHeight(250);
        preview.setMaxWidth(260);

        setFieldText(textField);
        textField.setMaxWidth(320);
        textField.setMaxHeight(135);

        textPane.add(new Label(dialog.getContentText()), 0, 0);
        controlPane.add(textField, 0, 0);
        previewPane.add(preview, 0, 0);

        pane.add(textPane, 0, 0);
        pane.add(controlPane, 0, 1);
        dialog.getDialogPane().setExpandableContent(previewPane);

        // reload the preview when Check is clicked
        textField.textProperty().addListener((obs, oldVal, newVal)
                -> preview.getEngine().loadContent(getHtmlCode(textField.getText())));

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
        dialog.setResultConverter(btn -> {
            if (btn == mainButtonType)
                return getHtmlCode(textField.getText());
            return null;
        });
    }

    /**
     * Encodes the input into the html
     *
     * @param input the string entered in the text field
     * @return resulting HTML code
     */
    public abstract String getHtmlCode(String input);

    /**
     * Sets the parameters for the input box
     *
     * @param inputBox the input box
     */
    public abstract void setFieldText(TextArea inputBox);

}
