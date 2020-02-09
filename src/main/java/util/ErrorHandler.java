package util;

/**
 * Error handler for HTMLNotepadFX
 */
class ErrorHandler {

    static void fileNotFound(String filename) {
        Dialogs.errorDialog("Error", "File not found",
                "The specified file '" + filename + "' was not found." +
                        "\nPlease include full path to the file.");
    }

    static void fileIOError(String filename, Exception exception) {
        Dialogs.detailedExceptionDialog("File I/O Error",
                "File Input / Output error",
                "There was an error processing the file " + filename,
                exception);
    }

    static void printError(Exception exception) {
        Dialogs.detailedExceptionDialog("Print Error",
                "Could not print the document",
                "There was an error processing your request",
                exception);
    }
}
