package util;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.print.PrinterException;

/**
 * Print handler for Notepad
 */
public class Print {

    /**
     * Display a print dialog and send the text to printer when settings are accepted
     */
    public static void printText(String text) {
        try {   // use system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            // look and feel not supported, load the default one
        }

        JTextPane textPane = new JTextPane();   // fixme: not working on macOS
        textPane.setText(text);

        try {
            textPane.print();
        } catch (PrinterException e) {
            ErrorHandler.printError(e);
        }
    }
}
