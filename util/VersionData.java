package util;

/**
 * Version info class for HTMLNotepadFX
 */
public class VersionData {

    public static final String VERSION = "0.4";
    public static final String BUILD_DATE = "August 2019";
    public static final int BUILD_NUMBER = 1130;
    public static final boolean IS_BETA = true;
    public static final String CONFIG_LOCATION = getConfigFileLocation();
    public static final double CONFIG_VERSION = 1;

    /**
     * Get the location of HTMLNotepadFX config file
     */
    private static String getConfigFileLocation() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) return System.getProperty("user.home")  // windows
                + "\\HTMLNotepadFX_settings.xml";

        else return System.getProperty("user.home") // everything else
                + "/.HTMLNotepadFX_settings.xml";
    }
}
