package dialogs.simple;

import dialogs.DialogUtils;
import dialogs.GenericTagDialog;
import javafx.scene.control.TextArea;

/**
 * A dialog for inserting text inside the &lt;marquee&gt; tags
 */
public class MarqueeDialog extends GenericTagDialog {

    /**
     * Constructs a new MarqueeDialog instance
     *
     * @param caption    the title bar text
     * @param header     the dialog box header text
     * @param body       the dialog box body text / content
     * @param stylesheet the stylesheet to use or <code>null</code>
     */
    public MarqueeDialog(String caption, String header, String body, String stylesheet) {
        super(caption, header, body, stylesheet);
    }

    /**
     * Encodes the input into the html
     *
     * @param input the string entered in the text field
     * @return resulting HTML code
     */
    @Override
    public String getHtmlCode(String input) {
        return "<marquee>" + DialogUtils.escapeHtmlText(input) + "</marquee>";
    }

    /**
     * Sets the parameters for the input box
     *
     * @param inputBox the input box
     */
    @Override
    public void setFieldText(TextArea inputBox) {
        inputBox.setPromptText("Enter text...");
    }

}
