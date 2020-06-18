import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.math.BigDecimal;

/**
 * A very basic calculator which performs mathematical operations on
 * two numbers.
 */
public class QuickCalcController {
    public TextField field1, field2, resultField;
    public Button btnPlus, btnMinus, btnAsterisk, btnSlash, btn2, btn3;

    /**
     * Performs initialization
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
     * Performs calculations
     *
     * @param input1        first number encapsulated in a string
     * @param input2        second number ...
     * @param calculationID id of the calculation to perform; can be
     *                      one of the following:
     *                      0 - add, 1 - subtract, 2 - multiply,
     *                      3 - divide, 4 - exponent by two,
     *                      5 - exponent by three.
     * @throws IllegalStateException if no valid option is specified.
     */
    private void calculate(String input1, String input2, int calculationID) {
        BigDecimal num1, num2;
        num1 = stringToBigDecimal(input1);
        num2 = stringToBigDecimal(input2);

        switch (calculationID) { // perform the calculations
            case 0: // add
                resultField.setText(num1.add(num2).toString());
                break;
            case 1: // subtract
                resultField.setText(num1.subtract(num2).toString());
                break;
            case 2: // multiply
                resultField.setText(num1.multiply(num2).toString());
                break;
            case 3: // divide
                try {
                    resultField.setText(num1.divide(num2).toString());
                } catch (ArithmeticException e) {
                    resultField.setText(BigDecimal.ZERO.toString());
                }
                break;
            case 4: // exponent by two
                resultField.setText(num1.multiply(num1).toString());
                break;
            case 5: // exponent by three
                resultField.setText(num1.multiply(num1.multiply(num1)).toString());
                break;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Copies the result
     */
    public void copyResult() {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(resultField.getText()), null);
    }

    /**
     * Converts the input string to BigDecimal
     *
     * @param string input string
     * @return result of the conversion or 0 if it fails
     */
    private BigDecimal stringToBigDecimal(String string) {
        try {
            return new BigDecimal(string);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Closes the window
     */
    public void close() {
        Stage stage = (Stage) field1.getScene().getWindow();
        stage.close();
    }
}
