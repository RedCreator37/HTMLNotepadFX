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
    public TextArea descTextBox;

    /**
     * Sets the values on initialization
     */
    @FXML
    protected void initialize() {
        versionLabel.setText(VersionData.VERSION);
        buildLabel.setText(String.valueOf(VersionData.BUILD_NUMBER));
        buildDateLabel.setText(VersionData.BUILD_DATE);
        betaLabel.setText(String.valueOf(VersionData.IS_BETA));

        // the amount of ram available to the program
        ramLabel.setText(Runtime.getRuntime().maxMemory() / 1048576 + " MB");

        // available disk space
        diskLabel.setText(new File("/").getTotalSpace() / 1073741824 + " GB");
    }

    /**
     * Closes the about box
     */
    public void closeAboutDialog() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}
