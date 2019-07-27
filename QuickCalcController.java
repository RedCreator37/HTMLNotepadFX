import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * A very basic calculator which performs mathematical operations on
 * two numbers.
 */
public class QuickCalcController {
    public TextField field1;
    public TextField field2;
    public TextField resultField;
    public Button btnPlus;
    public Button btnMinus;
    public Button btnAsterisk;
    public Button btnSlash;
    public Button btn2;
    public Button btn3;

    /**
     * Perform some initialization when the window is loaded
     */
    @FXML
    protected void initialize() {
        btnPlus.setOnAction(e -> calculate(field1.getText(), field2.getText(), 0));
        btnMinus.setOnAction(e -> calculate(field1.getText(), field2.getText(), 1));
        btnAsterisk.setOnAction(e -> calculate(field1.getText(), field2.getText(), 2));
        btnSlash.setOnAction(e -> calculate(field1.getText(), field2.getText(), 3));
        btn2.setOnAction(e -> calculate(field1.getText(), field2.getText(), 4));
        btn3.setOnAction(e -> calculate(field1.getText(), field2.getText(), 5));
        Platform.runLater(field1::requestFocus);
    }


    /**
     * Perform the calculations
     *
     * @param input1        first number encapsulated in a string
     * @param input2        second number ...
     * @param calculationID id of the calculation to perform; can be
     *                      one of the following:
     *                      0 - add, 1 - subtract, 2 - multiply,
     *                      3 - divide, 4 - exponent by two,
     *                      5 - exponent by three. Throws new
     *                      IllegalStateException if no valid option
     *                      is passed.
     */
    private void calculate(String input1, String input2, int calculationID) {
        double num1, num2;
        num1 = safeStringToDouble(input1);
        num2 = safeStringToDouble(input2);

        switch (calculationID) { // perform the calculations
            case 0: // add
                resultField.setText(String.valueOf(num1 + num2));
                break;
            case 1: // subtract
                resultField.setText(String.valueOf(num1 - num2));
                break;
            case 2: // multiply
                resultField.setText(String.valueOf(num1 * num2));
                break;
            case 3: // divide
                resultField.setText(String.valueOf(num1 / num2));
                break;
            case 4: // exponent by two
                resultField.setText(String.valueOf(num1 * num1));
                break;
            case 5: // exponent by three
                resultField.setText(String.valueOf((num1 * num1) * (num1 * num1)));
                break;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Copy the result from the result field
     */
    public void copyResult() {
        String text = resultField.getText();
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    /**
     * Try to convert string to integer, return 0 if conversion fails
     */
    private double safeStringToDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Close the Quick Calculator window
     */
    public void close() {
        Stage stage = (Stage) field1.getScene().getWindow();
        stage.close();
    }
}
