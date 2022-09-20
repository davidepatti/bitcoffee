package GUI;

import bitcoffee.Block;
import bitcoffee.Kit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ParseBlock extends JDialog{
    private JPanel ParseBlockPanel;
    private JTextField textFieldRawBlock;
    private JButton startParseButton;
    private JTextArea textAreaOutput;
    private JLabel btnHome;
    private JTextArea textAreaInfo;
    private final ImageIcon icon;


    public ParseBlock(JFrame parent) {
    //    super(parent);
        super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        btnHome.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                dispose();
                Dashboard dash= new Dashboard(null);
            }
        });

        startParseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String block_raw= textFieldRawBlock.getText();
                if (block_raw.isEmpty()) {
                    JOptionPane.showMessageDialog(parent,
                            "Please enter a value for the block",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    cmd_parseblock(block_raw);
                }
            }
        });

        setTitle("Bitcoffee");
        icon= new ImageIcon("src/GUI/images/icons8-blockchain-2.png");
        setIconImage(icon.getImage());
        setContentPane(ParseBlockPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(1000,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setVisible(true);

    }

    public void cmd_parseblock(String block_raw) {
        // defining security for catching exception (System.exit in case of failure of the parse)
        MySecurityManager secManager = new MySecurityManager();
        System.setSecurityManager(secManager);
        // resetting the text areas
        textAreaOutput.selectAll();
        textAreaOutput.replaceSelection("");
        textAreaInfo.selectAll();
        textAreaInfo.replaceSelection("");
        // for printing system out message in the annidate methods of the class Block
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old= System.out;
        System.setOut(ps);

        textAreaOutput.append("Parsing raw block: " + block_raw + "\n");
        try {
            var block = Block.parseSerial(Kit.hexStringToByteArray(block_raw));
            String blockInfo= block.toString().replace("bitcoffee.Block","");
            textAreaInfo.append(blockInfo);
            textAreaOutput.append("------------------------------------------------" + "\n");
            textAreaOutput.append("Details" + "\n");
            textAreaOutput.append("------------------------------------------------" + "\n");
            textAreaOutput.append("BIP: ");
            assert block != null;
            if (block.checkBIP9()) textAreaOutput.append("BIP9" + "\n");
            else if (block.checkBIP91()) textAreaOutput.append("BIP91" + "\n");
            else if (block.checkBIP141()) textAreaOutput.append("BIP9141" + "\n");
            textAreaOutput.append("Block hash: " + block.getHashHexString() + "\n");
            textAreaOutput.append("Difficulty: " + block.difficulty());
            System.setOut(old);
        }
           catch (SecurityException e){
            textAreaOutput.selectAll();
            textAreaOutput.replaceSelection("");
            textAreaOutput.append(baos.toString());
            JOptionPane.showMessageDialog(this,
                 "Wrong size in block header serialization",
                    "INPUT ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }



    public static void main(String[] args) {
        ParseBlock prsBlck= new ParseBlock(null);
    }

}
