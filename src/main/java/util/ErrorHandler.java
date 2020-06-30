package util;

import javafx.scene.control.Alert;

/**
 * Contains utility methods for error handling
 */
final class ErrorHandler {

    /**
     * Non-instantiable
     */
    private ErrorHandler() {
    }

    /**
     * Displays a File not found dialog
     *
     * @param filename the name of the file that wasn't found
     */
    static void fileNotFound(String filename) {
        Dialogs.alert("Error", "File not found",
                "The specified file '" + filename + "' was not found." +
                        "\nPlease include full path to the file.", Alert.AlertType.ERROR);
    }

    /**
     * Displays a generic File reading failed dialog
     *
     * @param filename   the name of the file
     * @param stacktrace the exception stacktrace
     */
    static void fileIOError(String filename, String stacktrace) {
        Dialogs.detailedExceptionDialog("File I/O Error",
                "File Input / Output error",
                "There was an error processing the file " + filename,
                stacktrace);
    }

    /**
     * Displays a generic Printing failed dialog
     *
     * @param stacktrace the PrintException stacktrace
     */
    static void printError(String stacktrace) {
        Dialogs.detailedExceptionDialog("Print Error",
                "Could not print the document",
                "There was an error processing your request",
                stacktrace);
    }
}
