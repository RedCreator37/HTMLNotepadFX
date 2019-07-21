package Utilities;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * This class is used as a general purpose dialog window creator.
 * You can use it to create dialogs in other classes.
 *
 * Parts of the code from
 * <href a="https://code.makery.ch/blog/javafx-dialogs-official/"></href>
 */
public class Dialogs {

    /**
     * Display a basic information dialog
     */
    public static void infoDialog(String title, String headerText, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Display an info dialog without header
     */
    public static void compactInfoDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Display a warning dialog with an OK button
     */
    public static void warningDialog(String title, String headerText, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Display a confirmation dialog and let the user select OK/Cancel
     *
     * @return true if the user selected OK or false if the user selected Cancel
     */
    public static boolean confirmationDialog(String title, String headerText, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();

        return (result.isPresent() && result.get() == ButtonType.OK);
    }

    /**
     * Display a basic error dialog
     *
     * Please use ErrorHandler to set the dialog content.
     * Also use ErrorHandler to create more advanced/custom error dialogs
     */
    public static void errorDialog(String title, String headerText, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Display a text input dialog with the default value in the input field
     */
    public static String inputDialog(String title, String headerText, String content, String defaultValue) {
        TextInputDialog inputDialog = new TextInputDialog(defaultValue);
        inputDialog.setTitle(title);
        inputDialog.setHeaderText(headerText);
        inputDialog.setContentText(content);

        inputDialog.showAndWait(); // wait for input
        return inputDialog.getResult();
    }

    /**
     * Display a customizable text input dialog with two fields, the
     * second field will be updated when the text in the first one is
     * changed.
     *
     * @param title       dialog title
     * @param headerText  the header text to be displayed in the dialog
     * @param buttonText  default button action text (ex. "Insert")
     * @param content     the text that'll be displayed above input fields
     * @param promptText1 first text field's prompt text
     * @param promptText2 second text field's prompt text
     * @param label1      text to be displayed before the first text field
     * @param label2      the same as label1, this time for the second field
     * @return an Optional String Pair (both Key and Value contain a
     * string, first one is the first field's text, second one is the
     * second field's text).
     */
    public static Optional<Pair<String, String>> doubleInputDialog(String title, String headerText, String content,
                                                                   String buttonText, String promptText1,
                                                                   String promptText2, String label1, String label2) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);

        // set button types
        ButtonType mainButtonType = new ButtonType(buttonText, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(mainButtonType, ButtonType.CANCEL);

        // create other controls
        GridPane pane = new GridPane(), textPane = new GridPane(),
                controlPane = new GridPane();
        TextField field1 = new TextField(), field2 = new TextField();

        controlPane.setHgap(10);
        textPane.setHgap(10);
        controlPane.setVgap(10);
        controlPane.setPadding(new Insets(10, 10, 0, 0));

        field1.setPromptText(promptText1);
        field2.setPromptText(promptText2);

        textPane.add(new Label(content), 0, 0);
        controlPane.add(new Label(label1), 0, 0);
        controlPane.add(field1, 1, 0);
        controlPane.add(new Label(label2), 0, 1);
        controlPane.add(field2, 1, 1);

        pane.add(textPane, 0, 0);
        pane.add(controlPane, 0, 1);

        // disable the main button until some text is entered into the fields
        Node mainButton = dialog.getDialogPane().lookupButton(mainButtonType);
        mainButton.setDisable(true);
        field1.textProperty().addListener((observable, oldValue, newValue) -> {
            field2.setText(field1.getText());
            mainButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(pane);
        Platform.runLater(field1::requestFocus);

        // convert the result to a string pair
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == mainButtonType)
                return new Pair<>(field1.getText(), field2.getText());
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Display a generic error dialog with a details sub pane containing
     * exception stacktrace.
     */
    static void detailedExceptionDialog(String title, String headerText, String text, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(text);

        // Create expandable Exception.
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String exceptionText = stringWriter.toString();

        Label label = new Label("Details:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}
