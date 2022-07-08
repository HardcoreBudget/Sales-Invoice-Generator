package InputOutput;

import javax.swing.*;

public class CreateInvoice extends  JFrame{
    public JPanel mainFrame;
    public JTextField InvoiceNumber;
    public JTextField CustomerName;
    public JTextField Date;
    public JButton okButton;
    public JButton cancelButton;
    public JTextField ProductName;
    public JTextField Price;
    public JTextField Quantity;

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
