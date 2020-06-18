package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * File input/output routines
 */
public class FileIO {

    /**
     * Opens a text file and return its content in form of a string
     */
    public static String loadFile(File file) {
        StringBuilder sb = new StringBuilder();

        try {
            Scanner scan = new Scanner(new FileReader(file.getAbsolutePath()));
            while (scan.hasNext()) sb.append(scan.nextLine()).append("\n");
        } catch (FileNotFoundException e) {
            ErrorHandler.fileNotFound(file.getAbsolutePath());
        }

        return sb.toString();
    }

    /**
     * Saves the given string to a text file
     */
    public static void saveFile(File file, String fileContent) {
        try {   // use a buffered writer to write to the file
            BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            out.write(fileContent);
            out.close();
        } catch (IOException e) {
            ErrorHandler.fileIOError(file.getAbsolutePath(), e);
        }
    }
}
