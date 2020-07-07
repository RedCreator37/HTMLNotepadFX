package dialogs;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

/**
 * A JavaScript script insertion dialog with preview pane and support
 * for loading and saving scripts
 */
public class ScriptDialog extends CustomDialog<String> {

    /**
     * Constructs a new ScriptDialog instance
     *
     * @param caption    the title bar text
     * @param header     the dialog box header text
     * @param body       the dialog box body text / content
     * @param stylesheet the stylesheet to use or <code>null</code>
     */
    public ScriptDialog(String caption, String header, String body, String stylesheet) {
        super(caption, header, body, stylesheet);
    }

    private ButtonType mainButtonType;
    private TextArea scriptBox;
    private TextField altTextField;

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
                editingPane = new GridPane(), testingPane = new GridPane(),
                buttonPane = new GridPane();
        scriptBox = new TextArea();
        altTextField = new TextField();
        WebView preview = new WebView();

        setGridInsets(editingPane);
        setGridInsets(testingPane);
        setGridInsets(buttonPane);
        textPane.setHgap(10);
        preview.setMaxHeight(250);
        preview.setMaxWidth(400);

        scriptBox.setPromptText("Enter a JavaScript script here");
        scriptBox.setMinWidth(260);
        altTextField.setPromptText("Enter script alt text");
        altTextField.setMinWidth(260);

        textPane.add(new Label(dialog.getContentText()), 0, 0);
        editingPane.add(scriptBox, 0, 0);
        CheckBox altCheckBox = new CheckBox("Set alt text");
        altCheckBox.setSelected(false);
        editingPane.add(new Label("Warning! Scripts can be harmful and some" +
                "browsers will block them!"), 0, 1);
        editingPane.add(altCheckBox, 0, 2);
        altTextField.setDisable(true);
        editingPane.add(altTextField, 0, 3);
        editingPane.add(buttonPane, 1, 0);
        testingPane.add(preview, 0, 0);

        Node testBtn = new Button("Test script"),
                loadBtn = new Button("Load..."),
                saveBtn = new Button("Save...");
        buttonPane.add(testBtn, 0, 0);
        buttonPane.add(loadBtn, 0, 1);
        buttonPane.add(saveBtn, 0, 2);

        pane.add(textPane, 0, 0);
        pane.add(editingPane, 0, 1);
        dialog.getDialogPane().setExpandableContent(testingPane);

        // reload the preview when Check is clicked
        testBtn.setOnMouseClicked(e -> {
            try {
                preview.getEngine().load(getScriptHtml(scriptBox.getText(),
                        altTextField.getText()));
            } catch (Exception ignored) { }
        });

        altCheckBox.selectedProperty().addListener((obs, oldVal, newVal)
                -> altTextField.setDisable(!newVal));

        // disable the main button until some text is entered into the fields
        Node mainButton = dialog.getDialogPane().lookupButton(mainButtonType);
        mainButton.setDisable(true);
        scriptBox.textProperty().addListener((obs, oldVal, newVal)
                -> mainButton.setDisable(newVal.trim().isEmpty()));

        dialog.getDialogPane().setContent(pane);
        // rename the "Show Details" hyperlink to "Show Testing Pane"
        Platform.runLater(() -> DialogUtils.setDetailsButtonText(dialog
                .getDialogPane(), "Show Testing Pane", "Hide Testing Pane"));
        testingPane.setStyle("-fx-border-color: rgb(59, 146, 219)");
        Platform.runLater(scriptBox::requestFocus);
    }

    /**
     * Sets the result converter
     */
    @Override
    public void setResultConverter() {
        dialog.setResultConverter(btn -> {
            if (btn == mainButtonType)
                return getScriptHtml(scriptBox.getText(), altTextField.getText());
            return null;
        });
    }

    /**
     * Returns the HTML code with this script
     *
     * @param script the script
     * @param alt    the script alt text
     * @return HTML code with this script
     */
    private static String getScriptHtml(String script, String alt) {
        String html = "<script>" + DialogUtils.escapeHtmlText(script) + "</script>";
        if (alt != null && !alt.trim().isEmpty()) html += "<noscript>" + DialogUtils
                .escapeHtmlText(alt) + "</noscript>";
        return html;
    }

}
