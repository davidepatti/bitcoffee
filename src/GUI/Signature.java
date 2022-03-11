package GUI;

import bitcoffee.Kit;
import bitcoffee.PrivateKey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;

public class Signature extends JDialog{
    private JPanel panelSignature;
    private JTextField textFieldSecret;
    private JTextField textFieldMessage;
    private JLabel btn_home;
    private JButton calculateButton;
    private JTextArea textAreaResult;
    private ImageIcon icon;


    public Signature(JFrame parent) {

   //     super(parent);
        super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        btn_home.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                dispose();
                Dashboard dash= new Dashboard(null);
            }

        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String secret= textFieldSecret.getText();
                String message= textFieldMessage.getText();
                if (secret.isEmpty() || message.isEmpty()) {
                    JOptionPane.showMessageDialog(parent,
                            "Please enter all fields",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    cmd_sign(secret, message);
                }
            }
        });

        setTitle("Bitcoffee");
        icon= new ImageIcon("src/GUI/images/icons8-blockchain-2.png");
        setIconImage(icon.getImage());
        setContentPane(panelSignature);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(1000,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setVisible(true);

    }


    private void cmd_sign(String secret, String message) {
        textAreaResult.selectAll();
        textAreaResult.replaceSelection("");

        var secret_bytes = Kit.hash256(secret);
        var secret_num = new BigInteger(1, secret_bytes);
        var msg_bytes = Kit.hash256(message);
        var msg_num = new BigInteger(1, msg_bytes);
        textAreaResult.append("Signing (secret:" + secret + " message:" + message + ") \n");
        textAreaResult.append("Secret hash: " + secret_num.toString(16) + "\n");
        textAreaResult.append("Message hash: " + msg_num.toString(16) + "\n");
        var pk = new PrivateKey(secret_bytes);
        var signature = pk.signDeterminisk(msg_bytes);
        textAreaResult.append("Signature: " + signature);
    }



    public static void main(String[] args) {
        Signature sign= new Signature(null);
    }

}
