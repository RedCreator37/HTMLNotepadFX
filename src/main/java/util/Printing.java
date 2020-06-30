package util;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import java.awt.print.PrinterException;
import java.util.Arrays;

/**
 * Contains utility methods for printing
 */
public final class Printing {

    /**
     * Non-instantiable
     */
    private Printing() {
    }

    /**
     * Displays a print dialog and prints this text
     *
     * @param text the text to be printed
     */
    public static void printText(String text) {
        try {   // use system LaF
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        Thread printThread = new Thread(() -> {
            JTextPane textPane = new JTextPane();
            textPane.setText(text);
            try {
                textPane.print();
            } catch (PrinterException e) {
                ErrorHandler.printError(Arrays.toString(e.getStackTrace()));
            }
        });
        printThread.start();
    }
}
