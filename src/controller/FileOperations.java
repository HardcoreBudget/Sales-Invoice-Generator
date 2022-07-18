package controller;

import model.InvoiceHeader;
import model.InvoiceLine;
import view.CreateInvoice;
import view.NewLine;
import view.SIG;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileOperations extends JFrame implements ActionListener {
    JMenu fileMenu;
    JMenuItem loadFile;
    JMenuItem saveFile;
    JMenuBar menuBar;
    JFileChooser fileChooser;
    public List<InvoiceHeader> invoiceHeaders;
    List<InvoiceLine> tempInvoiceLines;
    CreateInvoice createInvoice;
    NewLine newLine;
    SIG panel;
    int selectedRow=-1;
    public String[][] headerData;
    String[][] lineData;
    public boolean fileChooserConfirmed;

    public FileOperations(String title) {
        super("SIG");
        panel = new SIG();
        panel.createNewInvoiceButton.addActionListener(this);
        panel.deleteInvoiceButton.addActionListener(this);
        panel.saveButton.addActionListener(this);
        panel.cancelButton.addActionListener(this);
        panel.newLineButton.addActionListener(this);
        panel.deleteLineButton.addActionListener(this);

        createInvoice = new CreateInvoice();
        createInvoice.cancelButton.addActionListener(this);
        createInvoice.okButton.addActionListener(this);

        newLine = new NewLine();
        newLine.cancelButton.addActionListener(this);
        newLine.okButton.addActionListener(this);

        setContentPane(panel.mainFrame);
        setSize(1000, 500);
        setLocation(100, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        fileChooser = new JFileChooser();

        loadFile = new JMenuItem("Load", 'L');
        loadFile.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
        loadFile.addActionListener(this);
        loadFile.setActionCommand("Load");
        fileMenu.add(loadFile);

        saveFile = new JMenuItem("Save", 'S');
        saveFile.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
        saveFile.addActionListener(this);
        saveFile.setActionCommand("Save File");
        fileMenu.add(saveFile);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        loadHeaderContent(readFile("Choose Header File"));
        if(fileChooserConfirmed)
            loadLineContent(readFile("Choose Line File"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Load")) {
            loadHeaderContent(readFile("Choose Header File"));
            if(fileChooserConfirmed)
                loadLineContent(readFile("Choose Line File"));
        }
        if (e.getActionCommand().equals("Create")) {
            createInvoice.invoiceNumber.setText("");
            createInvoice.date.setText("");
            createInvoice.customerName.setText("");
            createInvoice.setVisible(true);
        }
        if (e.getActionCommand().equals("Cancel Invoice")) {
            createInvoice.setVisible(false);
        }
        if (e.getActionCommand().equals("New Invoice")) {
            if (invoiceHeaders == null) {
                invoiceHeaders = new ArrayList<>();
            }
            boolean duplicateInvoiceNumber=false;
            for(InvoiceHeader invoiceHeader: invoiceHeaders) {
                if(invoiceHeader.invoiceNumber.equals(createInvoice.invoiceNumber.getText().toString())){
                    duplicateInvoiceNumber=true;
                }
            }
            if(!duplicateInvoiceNumber) {
                invoiceHeaders.add(new InvoiceHeader(createInvoice.invoiceNumber.getText().toString(),
                        createInvoice.date.getText().toString(), createInvoice.customerName.getText().toString()));
                loadHeaderContent(null);
                createInvoice.setVisible(false);
                char[] date = createInvoice.date.getText().toString().toCharArray();
                if (date[2] != '-' || date[5] != '-') {
                    JOptionPane.showMessageDialog(this, "Date format for invoice number " +
                            createInvoice.invoiceNumber.getText().toString() + " is wrong", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
            else{
                JOptionPane.showMessageDialog(this,"Duplicate invoice number","Error",
                        JOptionPane.WARNING_MESSAGE);
            }


        }
        if (e.getActionCommand().equals("Delete")) {
            if (invoiceHeaders != null && panel.table1.getSelectedRow() != -1) {
                invoiceHeaders.remove(panel.table1.getSelectedRow());
                panel.table2.setModel(new DefaultTableModel());
                panel.invoiceNumberTF.setText("");
                panel.invoiceTotalTF.setText("");
                panel.customerNameTF.setText("");
                panel.dateTF.setText("");
                loadHeaderContent(null);
                selectedRow=-1;
                if (invoiceHeaders.size() == 0) {
                    invoiceHeaders = null;
                }
            }
        }
        if (e.getActionCommand().equals("Save Line")) {
            if (!panel.table2.isEditing() && invoiceHeaders != null && selectedRow!=-1) {
                List<InvoiceLine> invoiceLines = new ArrayList<>(invoiceHeaders.get(selectedRow).getFileLines());
                if(tempInvoiceLines!=null){
                    invoiceLines=new ArrayList<>(tempInvoiceLines);
                    tempInvoiceLines=null;
                }
                invoiceHeaders.set(selectedRow, new InvoiceHeader(panel.invoiceNumberTF.getText().toString(),
                        panel.dateTF.getText().toString(), panel.customerNameTF.getText().toString()));

                for (int i = 0; i < invoiceLines.size(); i++) {
                    if (panel.invoiceNumberTF.getText().toString().equals(panel.table2.getValueAt(i, 0).toString())) {
                        invoiceLines.set(i, new InvoiceLine(panel.table2.getValueAt(i, 0).toString()
                                , panel.table2.getValueAt(i, 1).toString()
                                , Double.parseDouble(panel.table2.getValueAt(i, 2).toString())
                                , Integer.parseInt(panel.table2.getValueAt(i, 3).toString())));
                    } else {

                        for (int j = 0; j < invoiceHeaders.size(); j++) {
                            if (invoiceHeaders.get(j).invoiceNumber.equals(panel.table2.getValueAt(i, 0).toString())) {
                                invoiceLines.set(i, new InvoiceLine(panel.table2.getValueAt(i, 0).toString()
                                        , panel.table2.getValueAt(i, 1).toString()
                                        , Double.parseDouble(panel.table2.getValueAt(i, 2).toString())
                                        , Integer.parseInt(panel.table2.getValueAt(i, 3).toString())));
                                List<InvoiceLine> additionalLine = new ArrayList<>(invoiceHeaders.get(j).getFileLines());
                                boolean match = false;
                                for (int z = 0; z < additionalLine.size(); z++) {
                                    if (additionalLine.get(z).itemName.equals(invoiceLines.get(i).itemName)
                                            && additionalLine.get(z).price == invoiceLines.get(i).price) {
                                        additionalLine.get(z).count += invoiceLines.get(i).count;
                                        match = true;
                                    }
                                }
                                if (!match) {
                                    additionalLine.add(invoiceLines.get(i));
                                }
                                invoiceHeaders.get(j).setFileLines(additionalLine);
                            }
                        }
                        invoiceLines.remove(i);
                    }
                }
                invoiceHeaders.get(selectedRow).setFileLines(invoiceLines);
                loadHeaderContent(null);
                viewLineTable();
            }
        }
        if (e.getActionCommand().equals("Cancel Changes")) {
            if (invoiceHeaders != null && !panel.table2.isEditing())
                viewLineTable();
        }
        if (e.getActionCommand().equals("Save File")) {
            if (headerData != null && lineData != null) {
                writeFile("Choose Header Save Location", headerData);
                List<String[]> temp = new ArrayList<>();

                for (int i = 0; i < invoiceHeaders.size(); i++) {
                    for (int j = 0; j < invoiceHeaders.get(i).getFileLines().size(); j++) {
                        temp.add(invoiceHeaders.get(i).getFileLines().get(j).getLineInfoWithoutTotalCost());

                    }
                }
                lineData = new String[temp.size()][4];
                for (int i = 0; i < temp.size(); i++) {
                    lineData[i] = temp.get(i);
                }
                writeFile("Choose Line Save Location", lineData);
            }
        }
        if (e.getActionCommand().equals("New Line")){
            if(invoiceHeaders!=null && selectedRow!=-1) {
                newLine.itemName.setText("");
                newLine.price.setText("");
                newLine.count.setText("");
                newLine.setVisible(true);
            }
        }
        if (e.getActionCommand().equals("Cancel Line")){
            newLine.setVisible(false);
        }
        if (e.getActionCommand().equals("Add Line")) {
            String[] productNames = newLine.itemName.getText().toString().split(",");
            String[] prices = newLine.price.getText().toString().split(",");
            String[] quantities = newLine.count.getText().toString().split(",");
            if (productNames.length == prices.length && productNames.length == quantities.length) {
                for (int i = 0; i < productNames.length; i++) {
                    boolean match = false;
                    for (int z = 0; z < tempInvoiceLines.size(); z++) {
                        if (tempInvoiceLines.get(z).itemName.equals(productNames[i])
                                && tempInvoiceLines.get(z).price == Double.parseDouble(prices[i])) {
                            tempInvoiceLines.get(z).count += Integer.parseInt(quantities[i]);
                            match = true;
                        }
                    }
                    if (!match) {
                        tempInvoiceLines.add(new InvoiceLine(invoiceHeaders.get(selectedRow).invoiceNumber, productNames[i],
                                Double.parseDouble(prices[i]), Integer.parseInt(quantities[i])));
                    }

                }
                newLine.setVisible(false);
                viewUnsavedLineTable();
            }
            else {
                JOptionPane.showMessageDialog(this, "Number of products, prices, and count don't match", "Error",
                        JOptionPane.WARNING_MESSAGE);
            }

        }
        if (e.getActionCommand().equals("Delete Line")){
            if(panel.table2.getSelectedRow()!=-1){
                tempInvoiceLines.remove(panel.table2.getSelectedRow());
                viewUnsavedLineTable();
            }
        }
    }

    public void loadHeaderContent(List<String> contents) {
        if (contents != null) {
            headerData = new String[contents.size()][4];
            invoiceHeaders = new ArrayList<>();

            for (int i = 0; i < contents.size(); i++) {
                String[] splitContent = contents.get(i).split(",");
                for (int j = 0; j < splitContent.length; j++)
                    headerData[i][j] = splitContent[j];
            }

            for (int i = 0; i < contents.size(); i++) {
                char[] date = headerData[i][1].toCharArray();
                if(date[2] != '-' || date[5] !='-') {
                    JOptionPane.showMessageDialog(this, "Date format for invoice number " +
                            headerData[i][0] + " is wrong", "Error", JOptionPane.WARNING_MESSAGE);
                }
                invoiceHeaders.add(new InvoiceHeader(headerData[i][0], headerData[i][1], headerData[i][2]));
            }
        } else {
            headerData = new String[invoiceHeaders.size()][4];
            for (int i = 0; i < invoiceHeaders.size(); i++) {
                headerData[i] = invoiceHeaders.get(i).getHeaderInfo();
            }
        }
        String[] cols = {"Invoice Number", "Date", "Customer", "Total"};

        panel.table1.setModel(new DefaultTableModel(headerData, cols));
        panel.table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && panel.table1.getSelectedRow() != -1) {
                    viewLineTable();
                }
            }
        });
    }

    void viewLineTable() {
        if (panel.table1.getSelectedRow() != -1)
            this.selectedRow = panel.table1.getSelectedRow();
        try {
            String[][] data = new String[invoiceHeaders.get(selectedRow).getFileLines().size()][4];
            for (int i = 0; i < invoiceHeaders.get(selectedRow).getFileLines().size(); i++) {
                data[i] = invoiceHeaders.get(selectedRow).getFileLines().get(i).getLineInfo();
            }
            String[] cols = {"Invoice Number", "Item Name", "Item Price", "Count", "Item Total"};

            panel.table2.setModel(new DefaultTableModel(data, cols));
            panel.invoiceNumberTF.setText(invoiceHeaders.get(selectedRow).invoiceNumber);
            panel.dateTF.setText(invoiceHeaders.get(selectedRow).date);
            panel.customerNameTF.setText(invoiceHeaders.get(selectedRow).customerName);
            panel.invoiceTotalTF.setText(Double.toString(invoiceHeaders.get(selectedRow).getTotalCost()));
            tempInvoiceLines=new ArrayList<>(invoiceHeaders.get(selectedRow).getFileLines());
        } catch (Exception e) {}
    }

    public void loadLineContent(List<String> contents) {
        lineData = new String[contents.size()][5];
        InvoiceLine[] invoiceLines = new InvoiceLine[contents.size()];

        for (int i = 0; i < contents.size(); i++) {
            String[] splitContent = contents.get(i).split(",");
            for (int j = 0; j < splitContent.length; j++)
                lineData[i][j] = splitContent[j];
        }

        for (int i = 0; i < invoiceLines.length; i++) {
            invoiceLines[i] = new InvoiceLine(lineData[i][0], lineData[i][1], Double.parseDouble(lineData[i][2]), Integer.parseInt(lineData[i][3]));
        }

        for (int i = 0; i < invoiceHeaders.size(); i++) {
            List<InvoiceLine> correspondingInvoiceLines = new ArrayList<>();
            for (int j = 0; j < invoiceLines.length; j++) {
                if (invoiceHeaders.get(i).invoiceNumber.equals(invoiceLines[j].getInvoiceNumber())) {
                    correspondingInvoiceLines.add(invoiceLines[j]);
                }
            }
            invoiceHeaders.get(i).setFileLines(correspondingInvoiceLines);
        }

        if (contents != null) {
            String[] cols = {"Invoice Number", "Date", "Customer", "Total"};
            for (int i = 0; i < invoiceHeaders.size(); i++) {
                headerData[i][3] = Double.toString(invoiceHeaders.get(i).getTotalCost());
            }
            panel.table1.setModel(new DefaultTableModel(headerData, cols));
        }
    }

    public List<String> readFile(String name) {
        fileChooserConfirmed =false;
        List<String> contents = new ArrayList<>();
        fileChooser.setDialogTitle(name);
        int fileChooserOption=fileChooser.showOpenDialog(this);

        if (fileChooserOption == JFileChooser.APPROVE_OPTION
                && fileChooser.getSelectedFile()!=null) {
            String path = fileChooser.getSelectedFile().getPath();
            if (path.endsWith(".csv")) {
                fileChooserConfirmed =true;
                Scanner sc = null;
                try {
                    sc = new Scanner(new File(path));

                    while (sc.hasNext()) {
                        contents.add(sc.next());
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    try {
                        sc.close();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            else {
                JOptionPane.showMessageDialog(this,"Wrong file format","Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        else if(fileChooser.getSelectedFile() == null){
            JOptionPane.showMessageDialog(this,"File not found","Error",
                    JOptionPane.WARNING_MESSAGE);
        }
        else if(fileChooserOption==1)
        {
            JOptionPane.showMessageDialog(this,"Did not choose a file","Error",
                    JOptionPane.WARNING_MESSAGE);
        }
        return contents;
    }

    void writeFile(String name, String[][] data) {
        FileWriter fileWriter = null;
        fileChooser.setDialogTitle(name);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION
                && fileChooser.getSelectedFile().exists()) {
            String path = fileChooser.getSelectedFile().getPath();
            if (path.endsWith(".csv")) {
                File csvFile = new File(path);
                try {
                    fileWriter = new FileWriter(csvFile);
                    for (String[] writeData : data) {
                        StringBuilder line = new StringBuilder();
                        for (int i = 0; i < writeData.length; i++) {
                            line.append(writeData[i]);
                            if (i != writeData.length - 1) {
                                line.append(',');
                            }
                        }
                        line.append("\n");
                        fileWriter.write(line.toString());
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    try {
                        fileWriter.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Wrong file format", "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else if (!fileChooser.getSelectedFile().exists()) {
            JOptionPane.showMessageDialog(this, "File not found", "Error",
                    JOptionPane.WARNING_MESSAGE);

        }
    }

    void viewUnsavedLineTable(){
        try {
            String[][] data = new String[tempInvoiceLines.size()][4];
            for (int i = 0; i < tempInvoiceLines.size(); i++) {
                data[i] = tempInvoiceLines.get(i).getLineInfo();
            }
            String[] cols = {"Invoice Number", "Item Name", "Item Price", "Count", "Item Total"};

            panel.table2.setModel(new DefaultTableModel(data, cols));

            double totalCost = 0;
            for (InvoiceLine invoiceLine : tempInvoiceLines) {
                totalCost += invoiceLine.getLineCost();
            }
            panel.invoiceTotalTF.setText(Double.toString(totalCost));
        } catch (Exception ex) {}
    }

    public static void main(String[] s) {
        FileOperations fileOperations = new FileOperations("SIG");

        if(fileOperations.fileChooserConfirmed) {
            for (InvoiceHeader header : fileOperations.invoiceHeaders) {
                System.out.println(header.invoiceNumber);
                System.out.println("{");
                System.out.println(header.customerName + " " + header.date);
                for (InvoiceLine line : header.getFileLines()) {
                    System.out.println(line.itemName + "," + line.price + "," + line.count);
                }
                System.out.println("}\n");
            }
        }
        System.exit(0);
    }
}