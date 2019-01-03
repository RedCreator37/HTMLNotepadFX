package Utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main File Input/Output class for Notepad
 * Please use this class for all file operations (opening, saving, etc.)
 *
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
public class FileIO {

    /**
     * Open a text file and return its content in form of a string
     */
    public static String openFile(File file) {
        String filename = file.getAbsolutePath();   // get the filename
        StringBuilder sb = new StringBuilder();

        try {
            Scanner scan = new Scanner(new FileReader(filename));   // open the file
            while (scan.hasNext()) // while there's still something to read
                sb.append(scan.nextLine() + "\n");  // append text to StringBuilder

        } catch (FileNotFoundException e) {
            ErrorHandler.fileNotFound(filename);
        }

        return sb.toString();

    }

    /**
     * Save the given string to a text file
     */
    public static void saveFile(File file, String fileContent) {
        try {
            // create a buffered writer to write to a file
            BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            out.write(fileContent); // write the contents of the TextArea to the file
            out.close();
        } catch (IOException e) {
            ErrorHandler.fileIOError(file.getAbsolutePath(), e);
        }

    }

}
