package util;

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
                sb.append(scan.nextLine()).append("\n");  // append text to a StringBuilder

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
