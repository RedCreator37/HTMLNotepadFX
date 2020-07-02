package util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.Optional;

/**
 * Contains utility methods for displaying dialog boxes
 *
 * Parts of the code from
 * <href a="https://code.makery.ch/blog/javafx-dialogs-official/"></href>
 */
public final class Dialogs {

    /**
     * Displays a generic alert box
     *
     * @param caption the title bar text
     * @param header  the dialog box header text
     * @param body    the dialog box body text / content
     * @param type    the type of the dialog box to display
     */
    public static void alert(String caption, String header, String body, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(caption);
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation dialog with OK / Cancel buttons
     *
     * @param caption the title bar text
     * @param header  the dialog box header text
     * @param body    the dialog box body text / content
     * @return true if OK was selected; false otherwise
     */
    public static boolean confirmationDialog(String caption, String header, String body) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(caption);
        alert.setHeaderText(header);
        alert.setContentText(body);
        Optional<ButtonType> result = alert.showAndWait();
        return (result.isPresent() && result.get() == ButtonType.OK);
    }

    /**
     * Displays a text input dialog
     *
     * @param caption the title bar text
     * @param header  the dialog box header text
     * @param body    the dialog box body text / content
     * @param hint    the default / hint value that'll be displayed in the input box
     * @return the entered string
     */
    public static String inputDialog(String caption, String header, String body, String hint) {
        TextInputDialog input = new TextInputDialog(hint);
        input.setTitle(caption);
        input.setHeaderText(header);
        input.setContentText(body);
        input.showAndWait();
        return input.getResult();
    }

    /**
     * Displays a customizable text input dialog with two fields. The
     * second field will get updated when the first one changes.
     *
     * @param caption the title bar text
     * @param header  the dialog box header text
     * @param body    the dialog box body text / content
     * @param action  default button action text (ex. "Insert")
     * @param hint1   first text field's hint text
     * @param hint2   second text field's hint text
     * @param label1  text to be displayed above the first text field
     * @param label2  text to be displayed above the second text field
     * @return an optional string pair containing the values
     */
    public static Optional<Pair<String, String>> twoFieldsInputDialog(String caption, String header, String body,
                                                                      String action, String hint1, String hint2,
                                                                      String label1, String label2) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(caption);
        dialog.setHeaderText(header);

        // set button types
        ButtonType mainButtonType = new ButtonType(action, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(mainButtonType, ButtonType.CANCEL);

        // create other controls
        GridPane pane = new GridPane(), textPane = new GridPane(),
                controlPane = new GridPane();
        TextField field1 = new TextField(), field2 = new TextField();

        controlPane.setHgap(10);
        textPane.setHgap(10);
        controlPane.setVgap(10);
        controlPane.setPadding(new Insets(10, 10, 0, 0));

        field1.setPromptText(hint1);
        field2.setPromptText(hint2);

        textPane.add(new Label(body), 0, 0);
        controlPane.add(new Label(label1), 0, 0);
        controlPane.add(field1, 1, 0);
        controlPane.add(new Label(label2), 0, 1);
        controlPane.add(field2, 1, 1);

        pane.add(textPane, 0, 0);
        pane.add(controlPane, 0, 1);

        // disable the main button until some text is entered into the fields
        Node mainButton = dialog.getDialogPane().lookupButton(mainButtonType);
        mainButton.setDisable(true);
        field1.textProperty().addListener((observable, oldVal, newVal) -> {
            field2.setText(field1.getText());
            mainButton.setDisable(newVal.trim().isEmpty());
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
     * Displays a text input dialog with a text area
     *
     * @param caption the title bar text
     * @param header  the dialog box header text
     * @param body    the dialog box body text / content
     * @param action  default button action text (ex. "Insert")
     * @param hint    text area's hint text
     * @return the entered string (or null if cancelled)
     */
    public static String textAreaInputDialog(String caption, String header, String body,
                                             String action, String hint) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(caption);
        dialog.setHeaderText(header);

        // set button types
        ButtonType buttonType = new ButtonType(action, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

        GridPane pane = new GridPane();
        TextArea area = new TextArea();

        area.setPromptText(hint);
        pane.add(new Label(body + "\n"), 0, 0);
        pane.add(area, 0, 1);

        // disable the default button until text is entered
        Node mainButton = dialog.getDialogPane().lookupButton(buttonType);
        mainButton.setDisable(true);
        area.textProperty().addListener(newVal -> mainButton.setDisable(false));

        dialog.getDialogPane().setContent(pane);
        Platform.runLater(area::requestFocus);

        // convert the result
        dialog.setResultConverter(btn -> {
            if (btn == buttonType) return area.getText();
            return null;
        });

        dialog.showAndWait();
        return dialog.getResult();
    }

    /**
     * Displays a text input dialog with a wide text field (for
     * entering long strings of data, such as web addresses)
     *
     * @param caption    the title bar text
     * @param header     the dialog box header text
     * @param text1      the text to be displayed above the first text field
     * @param text2      the text to be displayed above the second text field
     * @param action     default button action text (ex. "Insert")
     * @param defaultVal the default value in the text field
     * @return the entered string (or null if cancelled)
     */
    public static String wideInputDialog(String caption, String header, String text1, String text2,
                                         String action, String defaultVal) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(caption);
        dialog.setHeaderText(header);

        // set button types
        ButtonType buttonType = new ButtonType(action, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

        GridPane pane = new GridPane();
        TextField field = new TextField();

        field.setText(defaultVal);
        pane.add(new Label(text1 + "\n"), 0, 0);
        pane.add(field, 0, 1);
        pane.add(new Label(text2), 0, 2);

        // disable the default button until text is entered
        Node mainButton = dialog.getDialogPane().lookupButton(buttonType);
        mainButton.setDisable(true);
        field.textProperty().addListener(newVal -> mainButton.setDisable(false));

        dialog.getDialogPane().setContent(pane);
        Platform.runLater(field::requestFocus);

        // convert the result
        dialog.setResultConverter(btn -> {
            if (btn == buttonType) return field.getText();
            return null;
        });

        dialog.showAndWait();
        return dialog.getResult();
    }

    /**
     * Displays a generic error dialog with a Details sub pane containing
     * exception stacktrace.
     *
     * @param caption    the title bar text
     * @param header     the dialog box header text
     * @param body       the dialog box body text / content
     * @param stacktrace the exception stacktrace
     */
    static void detailedExceptionDialog(String caption, String header, String body, String stacktrace) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(caption);
        alert.setHeaderText(header);
        alert.setContentText(body);

        TextArea area = new TextArea(stacktrace);
        area.setEditable(false);
        area.setMaxWidth(Double.MAX_VALUE);
        area.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(area, Priority.ALWAYS);
        GridPane.setHgrow(area, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(new Label("Details:"), 0, 0);
        expContent.add(area, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}
