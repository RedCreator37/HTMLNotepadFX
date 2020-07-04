package dialogs;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;

/**
 * Common methods used in dialog classes
 */
public final class DialogUtils {

    /**
     * Non-instantiable
     */
    private DialogUtils() {
    }

    /**
     * FIXME: implement
     *
     * @param text the input text
     * @return the input text with HTML metacharacters escaped
     */
    public static String escapeHtmlText(String text) {
        return text;
    }

    /**
     * Sets the text of the "Show Details" button / hyperlink
     * @param pane the dialog pane
     * @param collapsed the initial text
     * @param expanded the text after the pane is expanded
     */
    public static void setDetailsButtonText(DialogPane pane, String collapsed, String expanded) {
        Hyperlink detailsButton = (Hyperlink) pane.lookup(".details-button");
        pane.expandedProperty().addListener(
                (ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal)
                        -> detailsButton.setText(newVal ? expanded : collapsed));
        pane.setExpanded(true);
        pane.setExpanded(false);
    }

}
