package dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.util.Optional;

/**
 * A custom instance of the standard dialog
 *
 * @param <R> the type of the dialog result
 */
public abstract class CustomDialog<R> {

    /**
     * The current dialog instance
     */
    final Dialog<R> dialog;

    /**
     * The type of the dialog accept button
     */
    ButtonType mainButtonType;

    /**
     * Constructs a new CustomDialog instance
     *
     * @param caption    the title bar text
     * @param header     the dialog box header text
     * @param body       the dialog box body text / content
     * @param stylesheet the stylesheet to use or <code>null</code>
     *                   to use the default
     */
    public CustomDialog(String caption, String header, String body, String stylesheet) {
        dialog = new Dialog<>();
        dialog.setTitle(caption);
        dialog.setHeaderText(header);
        dialog.setContentText(body);
        if (stylesheet != null && !stylesheet.trim().equals(""))
            dialog.getDialogPane().getScene().getStylesheets().add(stylesheet);
        setControls();
        setResultConverter();
    }

    /**
     * Initializes the controls
     */
    public abstract void setControls();

    /**
     * Sets the result converter
     */
    public abstract void setResultConverter();

    /**
     * Displays the dialog box and fetches the result
     *
     * @return the result or <code>empty</code> if the dialog was
     * cancelled
     */
    public Optional<R> run() {
        return dialog.showAndWait();
    }

}
