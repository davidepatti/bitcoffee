package GUI;

import bitcoffee.TxFetcher;
import bitcoffee.Tx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;


public class FetchTransaction extends JDialog{
    private JPanel panelFetchTx;
    private JTextField textFieldTxId;
    private JCheckBox testnetCheckBox;
    private JLabel btnHome;
    private JButton verifyButton;
    private JTextArea textAreaResult;
    private ImageIcon icon;



    public FetchTransaction(JFrame parent) {

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
                String transactionId= textFieldTxId.getText();
                boolean testnet;
                if (transactionId.isEmpty()) {
                    JOptionPane.showMessageDialog(parent,
                            "The transaction ID field cannot be empty: please entry a value",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (testnetCheckBox.isSelected()) testnet= true;
                    else testnet=false;
                    cmd_fetchtx(transactionId, testnet);
                }
            }
        });

        setTitle("Bitcoffee");
        icon= new ImageIcon("src/GUI/images/icons8-blockchain-2.png");
        setIconImage(icon.getImage());
        setContentPane(panelFetchTx);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(1000,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setVisible(true);

    }


    public void cmd_fetchtx(String txid, boolean testnet) {
        textAreaResult.selectAll();
        textAreaResult.replaceSelection("");
        try {
            var tx = TxFetcher.fetch(txid, testnet, true);
            textAreaResult.append(tx + "\n");
            textAreaResult.append("-------------------------------------------- \n");
            textAreaResult.append("Verifying transaction: \n");
            textAreaResult.append("-------------------------------------------- \n");
            if (tx.verify()) textAreaResult.append("--> Transaction confirmed as valid");
            else textAreaResult.append("--> Transaction is NOT valid!");
        } catch (Exception ec) {
            JOptionPane.showMessageDialog(this,
                    "Please insert a valid identifier of the transaction or the correct net",
                    "INPUT ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        FetchTransaction fetchTx= new FetchTransaction(null);
    }

}
