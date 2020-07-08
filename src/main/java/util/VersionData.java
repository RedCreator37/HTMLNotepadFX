package util;

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
     * Gets the location of the config file
     *
     * @return the config file location, based on the OS
     */
    private static String getConfigFileLocation() {
        String os = System.getProperty("os.name").toLowerCase();
        return System.getProperty("user.home") + (os.contains("win")
                ? "\\HTMLNotepadFX_settings.xml" : "/.HTMLNotepadFX_settings.xml");
    }
}
