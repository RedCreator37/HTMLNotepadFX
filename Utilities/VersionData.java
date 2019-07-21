/*
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
