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
        return System.getProperty("user.home") + (os.contains("win")
                ? "\\HTMLNotepadFX_settings.xml" : "/.HTMLNotepadFX_settings.xml");
    }
}
