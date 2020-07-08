package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.Optional;

/**
 * Contains utility methods for displaying dialog boxes
 *
 * Parts of the code from
 * <href a="https://code.makery.ch/blog/javafx-dialogs-official/">here</href>
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
