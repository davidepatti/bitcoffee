package GUI;

import bitcoffee.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.UnknownHostException;
import java.util.HashSet;

public class GetTransaction extends JDialog{
    private JPanel panelGetTx;
    private JLabel btnHome;
    private JTextField textFieldHost;
    private JTextField textFieldLastBlock;
    private JTextField textFieldAddress;
    private JCheckBox testnetCheckBox;
    private JTextArea textAreaResult;
    private JButton getButton;
    private ImageIcon icon;



    public GetTransaction(JFrame parent) {

        super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        btnHome.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                dispose();
                Dashboard dash= new Dashboard(null);
            }
        });
        getButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = textFieldHost.getText();
                String lastBlock= textFieldLastBlock.getText();
                String address= textFieldAddress.getText();
                boolean testnet;
                if (host.isEmpty() || lastBlock.isEmpty() || address.isEmpty()) {
                    JOptionPane.showMessageDialog(parent,
                            "Some fields are empty: please insert a value for any of them",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (testnetCheckBox.isSelected()) testnet= true;
                    else testnet=false;
                    cmd_gettx(host, lastBlock, address, testnet);
                }
            }
        });

        setTitle("Bitcoffee");
        icon= new ImageIcon("src/GUI/images/icons8-blockchain-2.png");
        setIconImage(icon.getImage());
        setContentPane(panelGetTx);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(1000,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setVisible(true);

    }


    public void cmd_gettx(String host, String last_block, String address, boolean testnet) {
        textAreaResult.selectAll();
        textAreaResult.replaceSelection("");
        try {
            var h160 = Kit.decodeBase58(address);

            var node = new SimpleNode(host, testnet);

            var bf = new BloomFilter(30, 5, 90210);
            bf.add(h160);

            node.Handshake();
            node.send(bf.filterLoad());

            var getheaders_msg = new MessageGetHeaders(last_block);
            node.send(getheaders_msg);

            var headers_msg = (MessageHeaders) node.waitFor(MessageHeaders.COMMAND);

            var getdata_msg = new MessageGetData();

            for (Block b : headers_msg.getBlocks()) {
                try {
                    if (!b.checkPoW()) {
                        throw new RuntimeException("Not valid PoW");
                    }
                } catch (RuntimeException re) {
                    JOptionPane.showMessageDialog(this,
                            "Not valid PoW",
                            "ERROR", JOptionPane.WARNING_MESSAGE);
                }
                getdata_msg.addData(MessageGetData.FILTERED_BLOCK_DATA_TYPE, b.getHashHexString());
            }

            node.send(getdata_msg);

            boolean found = false;

            var msg_to_wait = new HashSet<String>();
            msg_to_wait.add(MerkleBlock.COMMAND);
            msg_to_wait.add(Tx.COMMAND);

            while (!found) {
                var msg = node.waitFor(msg_to_wait);
                if (msg != null) {

                    if (msg.getCommand().equals("merkleblock")) {
                        try {
                            if (!((MerkleBlock) msg).isValid())
                                throw new RuntimeException("Not valid Merkle proof");
                        } catch (RuntimeException re) {
                            JOptionPane.showMessageDialog(this,
                                    "Not valid Merkle proof",
                                    "ERROR", JOptionPane.WARNING_MESSAGE);
                        }
                        if (((MerkleBlock) msg).isValid()) System.out.println("Received valid Merkle block");

                    } else {
                        var receveived_tx = (Tx) msg;
                        for (TxOut tout : receveived_tx.getTxOuts()) {
                            //if (tout.getScriptPubKey().getAddress(true).equals(address)) {
                            if (ScriptPubKey.parse(tout.getScriptPubkeyBytes()).getAddress(true).equals(address)) {
                                textAreaResult.append("Found address " + address + " in tx id: " + receveived_tx.getId());
                                found = true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Unknown host. If you're sure the inserted host is valid, try to put different values for the block or the address",
                    "ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }



    public static void main(String[] args) {
        GetTransaction getTx= new GetTransaction(null);
    }


}
