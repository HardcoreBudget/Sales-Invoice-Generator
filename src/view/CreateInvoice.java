package view;

import javax.swing.*;

public class CreateInvoice extends  JFrame{
    public JPanel mainFrame;
    public JTextField invoiceNumber;
    public JTextField customerName;
    public JTextField date;
    public JButton okButton;
    public JButton cancelButton;

    public CreateInvoice()
    {
        setContentPane(mainFrame);
        setSize(400,300);
        setLocation(100,100);
        setTitle("Create New Invoice");
        cancelButton.setActionCommand("Cancel Invoice");
        okButton.setActionCommand("New Invoice");
    }
}
