package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Contains utility methods for opening and saving files
 */
public final class FileIO {

    /**
     * Non-instantiable
     */
    private FileIO() {
    }

    /**
     * Opens this file and returns its content
     *
     * @param file the file to open
     * @return the file content
     */
    public static String loadFile(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            Scanner in = new Scanner(new FileReader(file.getAbsolutePath()));
            while (in.hasNext()) sb.append(in.nextLine()).append("\n");
        } catch (FileNotFoundException e) {
            ErrorHandler.fileNotFound(file.getAbsolutePath());
        }
        return sb.toString();
    }

    /**
     * Saves this string to this file
     *
     * @param file     the file to save
     * @param contents the text to save
     */
    public static void saveFile(File file, String contents) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            out.write(contents);
            out.close();
        } catch (IOException e) {
            ErrorHandler.fileIOError(file.getAbsolutePath(), Arrays.toString(e.getStackTrace()));
        }
    }
}
