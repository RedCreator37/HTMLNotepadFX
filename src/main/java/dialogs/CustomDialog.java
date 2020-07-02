package dialogs;

import javafx.scene.control.Dialog;

import java.util.Optional;

/**
 * A custom instance of the standard dialog
 *
 * @param <T> the type of the dialog result
 */
public abstract class CustomDialog<T> {

    /**
     * The current dialog instance
     */
    final Dialog<T> dialog;

    /**
     * Constructs a new CustomDialog instance
     *
     * @param caption the title bar text
     * @param header  the dialog box header text
     * @param body    the dialog box body text / content
     */
    public CustomDialog(String caption, String header, String body) {
        dialog = new Dialog<>();
        dialog.setTitle(caption);
        dialog.setHeaderText(header);
        dialog.setContentText(body);
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
    public Optional<T> run() {
        return dialog.showAndWait();
    }

}
