package GUI;

import bitcoffee.Kit;
import bitcoffee.PrivateKey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class P2Pkaddr extends JDialog{
    private JPanel panelP2Pkaddr;
    private JTextField textFieldTxID;
    private JCheckBox testnetCheckBox;
    private JButton generateButton;
    private JLabel btnHome;
    private JTextArea textAreaResult;
    private final ImageIcon icon;


    public P2Pkaddr(JFrame parent) {

        super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        btnHome.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                dispose();
                Dashboard dash= new Dashboard(null);
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String secret= textFieldTxID.getText();
                boolean testnet;
                if (secret.isEmpty()) {
                    JOptionPane.showMessageDialog(parent,
                            "The field secret cannot be empty: please entry a value",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    testnet= testnetCheckBox.isSelected();
                    cmd_p2pkaddr(secret, testnet);
                }
            }
        });

        setTitle("Bitcoffee");
        icon= new ImageIcon("src/GUI/images/icons8-blockchain-2.png");
        setIconImage(icon.getImage());
        setContentPane(panelP2Pkaddr);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(1000,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setVisible(true);

    }


    public void cmd_p2pkaddr(String secret, boolean testnet) {
        textAreaResult.selectAll();
        textAreaResult.replaceSelection("");
        var secret_bytes = Kit.hash256(secret);
        var mypk = new PrivateKey(secret_bytes);
        String myaddress;
        if (testnet) {
            myaddress = mypk.point.getP2pkhTestnetAddress();
            textAreaResult.append("Testnet address for secret: " + secret + "\n");
        }
        else{
            myaddress = mypk.point.getP2pkhAddress(true);
            textAreaResult.append("Mainnet address for secret: " + secret + "\n");
        }
        textAreaResult.append("address: " + myaddress + "\n");
        var wif = mypk.getWIF(true, testnet);
        textAreaResult.append("Use this WIF to import the private key into a wallet: " + wif);
    }



    public static void main(String[] args) {
        P2Pkaddr derivateKey= new P2Pkaddr(null);
    }


}
