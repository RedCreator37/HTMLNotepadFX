import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import util.FileIO;
import util.VersionData;

import java.io.File;
import java.util.Objects;

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
        String licenseText = FileIO.openFile(
                new File(Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("LICENSE")).getFile()));
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
