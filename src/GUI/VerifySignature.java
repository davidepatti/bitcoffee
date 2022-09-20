package GUI;

import bitcoffee.Kit;
import bitcoffee.S256Point;
import bitcoffee.Signature;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;

public class VerifySignature extends JDialog{
    private JPanel panelVerifySign;
    private JTextField textFieldSec;
    private JTextField textFieldDer;
    private JTextField textFieldZ;
    private JLabel btnHome;
    private JTextArea textAreaResult;
    private JButton verifyButton;
    private final ImageIcon icon;


    public VerifySignature(JFrame parent) {

        super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        btnHome.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                dispose();
                Dashboard dash= new Dashboard(null);
            }
        });

        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sec= textFieldSec.getText();
                String der= textFieldDer.getText();
                String z= textFieldZ.getText();
                if (sec.isEmpty() || der.isEmpty() || z.isEmpty()) {
                    JOptionPane.showMessageDialog(parent,
                            "Some fields are empty: please insert a value for any of them",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    cmd_verifySign(sec, der, z);
                }
            }
        });

        setTitle("Bitcoffee");
        icon= new ImageIcon("src/GUI/images/icons8-blockchain-2.png");
        setIconImage(icon.getImage());
        setContentPane(panelVerifySign);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(1000,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setVisible(true);

    }


    public void cmd_verifySign(String sec, String der, String z) {
        textAreaResult.selectAll();
        textAreaResult.replaceSelection("");
        try {
            var z_num = new BigInteger(z, 16);
            var point = S256Point.parseSEC(sec);
            var signature = Signature.parse(Kit.hexStringToByteArray(der));
            textAreaResult.append(" >> Verify signature test: " + point.verify(z_num, signature));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Please check the values inserted in the fields",
                    "INPUT ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }


    public static void main(String[] args) {
        VerifySignature verifySign= new VerifySignature(null);
    }

}
