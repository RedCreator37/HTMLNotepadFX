package Utilities;

import javax.swing.JTextPane;
import java.awt.print.PrinterException;

/**
 * Print handler for Notepad
 */
public class Print {

    /**
     * Display a print dialog and send the text to printer when settings are accepted
     */
    public static void printText(String text) {
        JTextPane textPane = new JTextPane();   // fixme: not working on macOS
        textPane.setText(text);

        try {
            textPane.print();
        } catch (PrinterException e) {
            ErrorHandler.printError(e);
        }
    }
}
