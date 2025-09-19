import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.DoubleBinaryOperator;

public class Calculator {
    private JTextField results;
    private JButton divideBtn;
    private JButton clearBtn;
    private JButton signBtn;
    private JButton percentBtn;
    private JButton equalBtn;
    private JButton multiplyBtn;
    private JButton minusBtn;
    private JButton addBtn;
    private JButton sixBtn;
    private JButton threeBtn;
    private JButton nineBtn;
    private JButton digitBtn; // '.'
    private JButton twoBtn;
    private JButton oneBtn;
    private JButton fiveBtn;
    private JButton fourBtn;
    private JButton sevenBtn;
    private JButton zeroBtn;
    private JButton eightBtn;
    private JPanel CalculatorView;

    private Double leftOperand = null;
    private Operation calcOperation = null;

    public Calculator() {
        // Zahlen
        sevenBtn.addActionListener(new NumberBtnClicked(sevenBtn.getText()));
        eightBtn.addActionListener(new NumberBtnClicked(eightBtn.getText()));
        nineBtn.addActionListener(new NumberBtnClicked(nineBtn.getText()));
        fourBtn.addActionListener(new NumberBtnClicked(fourBtn.getText()));
        fiveBtn.addActionListener(new NumberBtnClicked(fiveBtn.getText()));
        sixBtn.addActionListener(new NumberBtnClicked(sixBtn.getText()));
        oneBtn.addActionListener(new NumberBtnClicked(oneBtn.getText()));
        twoBtn.addActionListener(new NumberBtnClicked(twoBtn.getText()));
        threeBtn.addActionListener(new NumberBtnClicked(threeBtn.getText()));
        zeroBtn.addActionListener(new NumberBtnClicked(zeroBtn.getText()));

        // Operationen
        multiplyBtn.addActionListener(new OperationBtnClicked(Operation.MULTIPLICATION));
        divideBtn.addActionListener(new OperationBtnClicked(Operation.DIVISION));
        minusBtn.addActionListener(new OperationBtnClicked(Operation.SUBTRACTION));
        addBtn.addActionListener(new OperationBtnClicked(Operation.ADDITION));

        // Unäre/sonstige
        equalBtn.addActionListener(new EqualBtnClicked());
        clearBtn.addActionListener(new ClearBtnClicked());
        signBtn.addActionListener(new SignBtnClicked());
        digitBtn.addActionListener(new DigitBtnClicked());   // '.'
        percentBtn.addActionListener(new PercentBtnClicked()); // % als unäre Operation
    }

    // ===== Listeners =====

    private class NumberBtnClicked implements ActionListener {
        private final String value;
        public NumberBtnClicked(String value) { this.value = value; }

        @Override
        public void actionPerformed(ActionEvent e) {
            String current = results.getText();
            if (current == null) current = "";
            // führende 0 vermeiden (außer "0.")
            if (current.equals("0") && !value.equals(".")) {
                results.setText(value);
            } else {
                results.setText(current + value);
            }
        }
    }

    private class OperationBtnClicked implements ActionListener {
        private final Operation operation;
        public OperationBtnClicked(Operation operation) { this.operation = operation; }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Wenn schon eine Operation anliegt und rechts was steht -> zuerst auswerten (Chain calc)
            if (calcOperation != null && hasNumber(results.getText())) {
                Double right = parseOrNull(results.getText());
                if (leftOperand != null && right != null) {
                    leftOperand = calcOperation.apply(leftOperand, right);
                    results.setText(format(leftOperand));
                }
            } else if (hasNumber(results.getText())) {
                leftOperand = parseOrNull(results.getText());
            }
            calcOperation = operation;
            results.setText(""); // Eingabe für rechten Operanden
        }
    }

    private class ClearBtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            results.setText("");
            leftOperand = null;
            calcOperation = null;
        }
    }

    private class DigitBtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String t = results.getText();
            if (t == null || t.isEmpty()) {
                results.setText("0.");
            } else if (!t.contains(".")) {
                results.setText(t + ".");
            }
        }
    }

    private class PercentBtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Prozent bezieht sich auf die aktuelle Anzeige (unär)
            if (hasNumber(results.getText())) {
                double v = Double.parseDouble(results.getText()) / 100.0;
                results.setText(format(v));
            }
        }
    }

    private class EqualBtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calcOperation == null || leftOperand == null) return;
            Double right = parseOrNull(results.getText());
            if (right == null) right = leftOperand; // falls kein rechter Operand eingegeben wurde
            double output = calcOperation.apply(leftOperand, right);
            results.setText(format(output));
            // Ergebnis als neuer leftOperand für weiteres Rechnen; Operation zurücksetzen
            leftOperand = output;
            calcOperation = null;
        }
    }

    private class SignBtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String t = results.getText();
            if (t == null || t.isEmpty()) return;
            if (t.startsWith("-")) t = t.substring(1);
            else t = "-" + t;
            results.setText(t);
        }
    }

    // ===== Helpers =====

    private static boolean hasNumber(String s) {
        return s != null && !s.trim().isEmpty() && !s.equals("-");
    }

    private static Double parseOrNull(String s) {
        try { return hasNumber(s) ? Double.valueOf(s) : null; }
        catch (NumberFormatException ex) { return null; }
    }

    private static String format(double v) {
        if (Math.floor(v) == v) return String.valueOf((long) v);
        return String.valueOf(v);
    }

    // ===== Operationen =====
    private enum Operation {
        ADDITION((a, b) -> a + b),
        SUBTRACTION((a, b) -> a - b),
        MULTIPLICATION((a, b) -> a * b),
        DIVISION((a, b) -> b == 0 ? Double.NaN : a / b);

        private final DoubleBinaryOperator op;
        Operation(DoubleBinaryOperator op) { this.op = op; }
        double apply(double a, double b) { return op.applyAsDouble(a, b); }
    }

    // ===== Start =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Calculator");
            frame.setContentPane(new Calculator().CalculatorView);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}