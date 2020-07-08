package util;

import javafx.stage.FileChooser;

import java.util.ArrayList;

/**
 * Stores the program version data
 */
public final class VersionData {

    /**
     * Non-instantiable
     */
    private VersionData() {
    }

    /**
     * The program version
     */
    public static final String VERSION = "0.6";

    /**
     * The program build date
     */
    public static final String BUILD_DATE = "July 2020";

    /**
     * The program build number
     */
    public static final int BUILD_NUMBER = 1150;

    /**
     * True if the it's a beta build
     */
    public static final boolean IS_BETA = true;

    /**
     * The location of the config file, depends on the OS used
     */
    public static final String CONFIG_LOCATION = getConfigFileLocation();

    /**
     * The config file version magic number
     */
    public static final double CONFIG_VERSION = 15012;

    /**
     * The shared extension filter used when loading and saving
     * HTML files and their source code
     */
    public static final ArrayList<FileChooser.ExtensionFilter> HTML_FILE_EXTENSIONS = new ArrayList<>() {{
        add(new FileChooser.ExtensionFilter("HTML files (*.html, *.htm)", "*.html", "*.htm"));
        add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
        add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
    }};

    /**
     * Gets the location of the config file
     *
     * @return the config file location, based on the OS
     */
    private static String getConfigFileLocation() {
        String os = System.getProperty("os.name").toLowerCase();
        return System.getProperty("user.home") + (os.contains("win")
                ? "\\HTMLNotepadFX_settings.xml" : "/.HTMLNotepadFX_settings.xml");
    }

    /**
     * The stylesheet currently in use
     * TODO: move into Controller class
     */
    public static String stylesheet = "Styles.css";

}
