package GUI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Dashboard extends JDialog{
    private JButton derivatePrivateKeyButton;
    private JPanel DashboardPanel;
    private JButton checkTransactionButton;
    private JButton createTransactionButton;
    private JButton getTransactionButton;
    private JButton verifySignatureButton;
    private JButton fetchTransactionButton;
    private JButton signatureButton;
    private JButton difficultyAdjustmentButton;
    private JButton parseBlockButton;


    public Dashboard(JFrame parent) {
      //  super(parent);
        super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        signatureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Signature sign= new Signature(null);
            }
        });
        parseBlockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ParseBlock prsBlock= new ParseBlock(null);
            }
        });
        difficultyAdjustmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                DiffAdj dffAdj= new DiffAdj(null);
            }
        });
        derivatePrivateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                P2Pkaddr derivateKey= new P2Pkaddr(null);
            }
        });
        checkTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CheckTransaction checkTx= new CheckTransaction(null);
            }
        });
        verifySignatureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                VerifySignature verifySign= new VerifySignature(null);
            }
        });
        createTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(parent,
                        "Coming soon...",
                        "NOT AVAILABLE", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        fetchTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                FetchTransaction fetchTx= new FetchTransaction(null);
            }
        });
        getTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                GetTransaction getTx= new GetTransaction(null);
            }
        });
        setTitle("Bitcoffee");
        ImageIcon icon = new ImageIcon("src/GUI/images/icons8-blockchain-2.png");
        setIconImage(icon.getImage());
        setContentPane(DashboardPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(1000,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setVisible(true);


    }



    public static void main(String[] args) {
        Dashboard myDashboard= new Dashboard(null);
    }


}
