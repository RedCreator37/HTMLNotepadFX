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
     * Escapes HTML metacharacters and non-ascii characters in this
     * string
     *
     * @param text the input string
     * @return the string with HTML metacharacters escaped
     */
    public static String escapeHtmlText(String text) {
        StringBuilder b = new StringBuilder(text.length() * 2);
        for (char c : text.toCharArray()) {
            if (c > 127 || Character.toString(c).matches("[<>\\\\\"'&]"))
                b.append("&#").append((int) c).append(';');
            else b.append(c);
        }
        return b.toString();
    }

    /**
     * Sets the text of the "Show Details" button / hyperlink
     *
     * @param pane      the dialog pane
     * @param collapsed the initial text
     * @param expanded  the text after the pane is expanded
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
