/*
 * Copyright (c) 2019 Tobija Žuntar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package Utilities;

/**
 * Main error handler for Notepad
 * Please put all error messages here and call them instead of putting them directly into other classes.
 */
class ErrorHandler {

    static void fileNotFound(String filename) {
        Dialogs.errorDialog(
                "File not found",
                "File not found",
                "The specified file '" + filename + "' was not found.\nPlease include full path to the file.");
    }

    static void fileIOError(String filename, Exception exception) {
        Dialogs.detailedExceptionDialog(
                "File I/O Error",
                "File Input / Output error",
                "There was an error processing the file " + filename,
                exception);
    }

    static void printError(Exception exception) {
        Dialogs.detailedExceptionDialog(
                "Print Error",
                "Could not print the document",
                "There was an error processing your request",
                exception);
    }

} // end class Utilities.ErrorHandler
