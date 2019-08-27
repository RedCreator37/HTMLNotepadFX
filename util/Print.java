package util;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import java.awt.print.PrinterException;

/**
 * Print handler for HTMLNotepadFX
 */
public class Print {

    /**
     * Display a print dialog and send the text to printer when settings are accepted
     */
    public static void printText(String text) {
        try {   // use system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // look and feel not supported, load the default one
        }

        Thread printThread = new Thread(() -> {
            JTextPane textPane = new JTextPane();
            textPane.setText(text);

            try {
                textPane.print();
            } catch (PrinterException e) {
                ErrorHandler.printError(e);
            }
        });
        printThread.start();
    }
}
