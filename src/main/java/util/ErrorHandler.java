package util;

import javafx.scene.control.Alert;

final class ErrorHandler {

    static void fileNotFound(String filename) {
        Dialogs.alert("Error", "File not found",
                "The specified file '" + filename + "' was not found." +
                        "\nPlease include full path to the file.", Alert.AlertType.ERROR);
    }

    static void fileIOError(String filename, String stacktrace) {
        Dialogs.detailedExceptionDialog("File I/O Error",
                "File Input / Output error",
                "There was an error processing the file " + filename,
                stacktrace);
    }

    static void printError(String stacktrace) {
        Dialogs.detailedExceptionDialog("Print Error",
                "Could not print the document",
                "There was an error processing your request",
                stacktrace);
    }
}
