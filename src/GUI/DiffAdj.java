package GUI;

import bitcoffee.Block;
import bitcoffee.Kit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DiffAdj extends JDialog{
    private JPanel panelDiffAdj;
    private JTextField textFieldStartBlock;
    private JTextField textFieldEndBlock;
    private JButton calculateButton;
    private JTextArea textAreaInfoBlocks;
    private JLabel btnHome;
    private JTextArea textAreaResult;


    public DiffAdj(JFrame parent) {
     //   super(parent);
        super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        btnHome.addMouseListener(new MouseAdapter() {
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
                String start_block= textFieldStartBlock.getText();
                String end_block= textFieldEndBlock.getText();
                if (start_block.isEmpty() || end_block.isEmpty()) {
                    JOptionPane.showMessageDialog(parent,
                            "The fields cannot be empty: please insert a value",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    cmd_diffadj(start_block, end_block);
                }
            }
        });

        setTitle("Bitcoffee");
        ImageIcon icon = new ImageIcon("src/GUI/images/icons8-blockchain-2.png");
        setIconImage(icon.getImage());
        setContentPane(panelDiffAdj);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(1000,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setVisible(true);


    }


    public void cmd_diffadj(String startBlock, String endBlock) {
        textAreaInfoBlocks.selectAll();
        textAreaInfoBlocks.replaceSelection("");
        textAreaResult.selectAll();
        textAreaResult.replaceSelection("");
        try {
            var first_block = Block.parseSerial(Kit.hexStringToByteArray(startBlock));
            var last_block = Block.parseSerial(Kit.hexStringToByteArray(endBlock));
            textAreaResult.append("Computing difficulty adjustment... \n");

            textAreaInfoBlocks.append("First block: \n");
            String firstBlockInfo = first_block.toString().replace("bitcoffee.Block", "");
            textAreaInfoBlocks.append(firstBlockInfo + "\n");
            textAreaInfoBlocks.append("------------------------------------------------ \n");
            textAreaInfoBlocks.append("Last block: \n");
            String lastBlockInfo = last_block.toString().replace("bitcoffee.Block", "");
            textAreaInfoBlocks.append(lastBlockInfo);
            assert last_block != null;
            assert first_block != null;
            var time_diff = last_block.getTimestamp() - first_block.getTimestamp();
            textAreaResult.append("Time differential: " + time_diff + ", updating bits: " + Kit.bytesToHexString(first_block.getBits()) + "\n");
            var new_bits = Block.computeNewBits(first_block.getBits(), time_diff);
            textAreaResult.append("New bits: " + Kit.bytesToHexString(new_bits));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "One or both of the block inserted are not valid: please check the values and retry",
                    "INPUT ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }


    public static void main(String[] args) {
        DiffAdj difAdjust= new DiffAdj(null);
    }

}
