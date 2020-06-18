package util;

/**
 * Version information for HTMLNotepadFX
 */
public class VersionData {

    public static final String VERSION = "0.5";
    public static final String BUILD_DATE = "June 2020";
    public static final int BUILD_NUMBER = 1138;
    public static final boolean IS_BETA = true;
    public static final String CONFIG_LOCATION = getConfigFileLocation();
    public static final double CONFIG_VERSION = 1;

    /**
     * Returns the location of the config file
     */
    private static String getConfigFileLocation() {
        String os = System.getProperty("os.name").toLowerCase();
        return System.getProperty("user.home") + (os.contains("win")
                ? "\\HTMLNotepadFX_settings.xml" : "/.HTMLNotepadFX_settings.xml");
    }
}
