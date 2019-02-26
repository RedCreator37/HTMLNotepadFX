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

import Utilities.Dialogs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MainFX application class for Notepad
 */

public class MainFX extends Application {

    static Stage currentStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/MainWindow.fxml"));
        primaryStage.setTitle("Untitled - Notepad");
        primaryStage.setScene(new Scene(root, 1135, 700));
        primaryStage.show();

        currentStage = primaryStage;

        // load settings
        Controller controllerObject = new Controller();
        controllerObject.loadSettings();

        primaryStage.setOnCloseRequest(event -> {   // Ask for confirmation when closing the program
            boolean confirmed = Dialogs.confirmationDialog(
                    "Notepad",
                    "Warning",
                    "All unsaved changes will be lost! Continue?");

            if (confirmed) {
                Controller controller = new Controller();
                controller.saveSettings();

                System.exit(0); // User selected OK, close the program
            } else {
                event.consume(); // User selected Cancel, don't close the program
            }
        });

        System.gc();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Set the title bar text in specified window.
     *
     * @param newTitle new title bar text to display
     * @param stage    a window that will get the new title
     */
    static void setTitle(String newTitle, Stage stage) {
        stage.setTitle(newTitle);
    }

}
