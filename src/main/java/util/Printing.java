package util;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import java.awt.print.PrinterException;

/**
 * Printing routines
 */
public class Printing {

    /**
     * Displays a print dialog and send the text to printer
     */
    public static void printText(String text) {
        try {   // use system LaF
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { } // not supported, load the default one

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
