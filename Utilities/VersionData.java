package Utilities;

/**
 * Version info class for Notepad
 */
public class VersionData {

    public static final String VERSION = "0.3 HTML";

    public static final int BUILD_NUMBER = 1070;

    public static final String BUILD_DATE = "May 2019";

    public static final boolean IS_BETA = true;

    public static final String SETTINGS_LOCATION = getConfigFileLocation();

    public static final double CONFIG_VERSION = 1;

    /**
     * Get the location of Notepad config file
     */
    private static String getConfigFileLocation() {
        String os = (System.getProperty("os.name").toLowerCase());

        if (os.contains("win")) return System.getProperty("user.home")  // windows
                + "\\notepad_settings.xml";

        else return System.getProperty("user.home") // everything else
                + "/.notepad_settings.xml";
    }
}
