import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import util.VersionData;

import java.io.File;

/**
 * Controller class for About.fxml
 */
public class AboutController {

    // initialize controls
    public Button closeButton;
    public Label versionLabel, buildLabel, buildDateLabel, betaLabel, ramLabel, diskLabel;
    public TextArea licenseTextArea;

    /**
     * Set the values when the window is initialized
     */
    @FXML
    protected void initialize() {
        versionLabel.setText(VersionData.VERSION);
        buildLabel.setText(String.valueOf(VersionData.BUILD_NUMBER));
        buildDateLabel.setText(VersionData.BUILD_DATE);
        betaLabel.setText(String.valueOf(VersionData.IS_BETA));

        // get the amount of ram available to the program
        ramLabel.setText(Runtime.getRuntime().maxMemory() / 1048576 + " MB");

        // get available disk space
        diskLabel.setText(new File("/").getTotalSpace() / 1073741824 + " GB");

        // display the license
        String licenseText = "MIT License\n" +
                "\n" +
                "Copyright (c) 2019 Tobija Žuntar\n" +
                "\n" +
                "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
                "of this software and associated documentation files (the \"Software\"), to deal\n" +
                "in the Software without restriction, including without limitation the rights\n" +
                "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
                "copies of the Software, and to permit persons to whom the Software is\n" +
                "furnished to do so, subject to the following conditions:\n" +
                "\n" +
                "The above copyright notice and this permission notice shall be included in all\n" +
                "copies or substantial portions of the Software.\n" +
                "\n" +
                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
                "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
                "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
                "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
                "SOFTWARE.\n" +
                "\nSource code: www.github.com/RedCreator37/HTMLNotepadFX";
        licenseTextArea.setText(licenseText);
    }

    /**
     * Close the dialog box
     */
    public void closeAboutDialog() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}