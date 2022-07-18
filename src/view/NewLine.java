package view;

import javax.swing.*;

public class NewLine extends JFrame {
    public JPanel mainFrame;
    public JButton okButton;
    public JButton cancelButton;
    public JTextField itemName;
    public JTextField price;
    public JTextField count;

    public NewLine()
    {
        setContentPane(mainFrame);
        setSize(400,300);
        setLocation(100,100);
        setTitle("Create New Line");
        cancelButton.setActionCommand("Cancel Line");
        okButton.setActionCommand("Add Line");
    }
}
