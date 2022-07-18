package view;

import javax.swing.*;

public class SIG {
    public JPanel mainFrame;
    public JTable table1;
    public JTable table2;
    public JTextField invoiceNumberTF;
    public JTextField dateTF;
    public JTextField customerNameTF;
    public JButton saveButton;
    public JButton cancelButton;
    public JButton createNewInvoiceButton;
    public JButton deleteInvoiceButton;
    public JTextField invoiceTotalTF;
    public JButton newLineButton;
    public JButton deleteLineButton;

    public SIG()
    {
        saveButton.setActionCommand("Save Line");
        createNewInvoiceButton.setActionCommand("Create");
        deleteInvoiceButton.setActionCommand("Delete");
        cancelButton.setActionCommand("Cancel Changes");
        newLineButton.setActionCommand("New Line");
        deleteLineButton.setActionCommand("Delete Line");
        table1.setDefaultEditor(Object.class,null);
    }
}
