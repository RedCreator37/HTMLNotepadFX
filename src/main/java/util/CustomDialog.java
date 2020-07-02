package util;

import javafx.scene.control.Dialog;

import java.util.Optional;

public abstract class CustomDialog<T> {

    final Dialog<T> dialog;

    public CustomDialog(String caption, String header, String body) {
        dialog = new Dialog<>();
        dialog.setTitle(caption);
        dialog.setHeaderText(header);
        dialog.setContentText(body);
        setControls();
        setResultConverter();
    }

    public abstract void setControls();

    public abstract void setResultConverter();

    public Optional<T> run() {
        return dialog.showAndWait();
    }

}
