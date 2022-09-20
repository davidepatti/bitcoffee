package GUI;

import bitcoffee.Kit;
import bitcoffee.Tx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CheckTransaction extends JDialog{
    private JPanel panelCheckTx;
    private JTextField textFieldRawTx;
    private JButton checkButton;
    private JLabel btnHome;
    private JTextArea textAreaResult;


    public CheckTransaction(JFrame parent) {

        super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        btnHome.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                dispose();
                Dashboard dash= new Dashboard(null);
            }
        });

        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rawTx= textFieldRawTx.getText();
                if (rawTx.isEmpty()) {
                    JOptionPane.showMessageDialog(parent,
                            "The field cannot be empty: please entry a value",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    cmd_checktx(rawTx);
                }
            }
        });

        setTitle("Bitcoffee");
        ImageIcon icon = new ImageIcon("src/GUI/images/icons8-blockchain-2.png");
        setIconImage(icon.getImage());
        setContentPane(panelCheckTx);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(1000,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setVisible(true);

    }


    public void cmd_checktx(String raw_tx) {
        textAreaResult.selectAll();
        textAreaResult.replaceSelection("");
        // for printing system out message in the annidate methods of the class Tx
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream ps2 = new PrintStream(baos2);
        PrintStream old= System.out;
        System.setOut(ps);
        try {
            var tx = Tx.parse(Kit.hexStringToByteArray(raw_tx), false);
            System.setOut(old);
            textAreaResult.append(baos + "\n");
            System.setOut(ps2);
            textAreaResult.append(">> Checking fee for tx id: " + tx.getId() + "\n");
            var fee = tx.calculateFee();
            System.setOut(old);
            textAreaResult.append(baos2.toString());
            if (fee >= 0)
                textAreaResult.append("Valid transactions fees: " + fee);
            else
                textAreaResult.append("ERROR: not valid transaction fees: " + fee);
        } catch (Exception e){
            JOptionPane.showMessageDialog(this,
                    "Please insert a valid hex value of a serialized transaction",
                    "INPUT ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }




    public static void main(String[] args) {
        CheckTransaction checkTx= new CheckTransaction(null);
    }



}
