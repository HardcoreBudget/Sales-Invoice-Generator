package InputOutput;

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

public class ReadFile extends JFrame implements ActionListener {
    JMenu fileMenu;
    JMenuItem loadFile;
    JMenuItem saveFile;
    JMenuBar menuBar;
    JFileChooser fileChooser;
    List<Header> headers;
    CreateInvoice createInvoice;
    SIG panel;
    int selectedRow;
    String[][] headerData;
    String[][] lineData;
    public ReadFile(String title) {
        super("SIG");
        panel=new SIG();
        panel.createNewInvoiceButton.addActionListener(this);
        panel.deleteInvoiceButton.addActionListener(this);
        panel.saveButton.addActionListener(this);
        panel.cancelButton.addActionListener(this);

        createInvoice= new CreateInvoice();
        createInvoice.cancelButton.addActionListener(this);
        createInvoice.okButton.addActionListener(this);

        setContentPane(panel.mainFrame);
        setSize(1000,500);
        setLocation(100,100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        menuBar=new JMenuBar();
        fileMenu=new JMenu("File");
        fileChooser = new JFileChooser();

        loadFile=new JMenuItem("Load",'L');
        loadFile.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
        loadFile.addActionListener(this);
        loadFile.setActionCommand("Load");
        fileMenu.add(loadFile);

        saveFile=new JMenuItem("Save",'S');
        saveFile.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
        saveFile.addActionListener(this);
        saveFile.setActionCommand("Save File");
        fileMenu.add(saveFile);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Load")) {
            loadHeaderContent(getFileContents("Choose Header File"));
            loadLineContent(getFileContents("Choose Line File"));
        }
        if (e.getActionCommand().equals("Create")) {
            createInvoice.InvoiceNumber.setText("");
            createInvoice.Date.setText("");
            createInvoice.CustomerName.setText("");
            createInvoice.ProductName.setText("");
            createInvoice.Price.setText("");
            createInvoice.Quantity.setText("");
            createInvoice.setVisible(true);
        }
        if (e.getActionCommand().equals("Cancel Invoice")) {
            createInvoice.setVisible(false);
        }
        if (e.getActionCommand().equals("New Invoice")) {
            if (headers == null) {
                headers = new ArrayList<>();
            }
            headers.add(new Header(createInvoice.InvoiceNumber.getText().toString(),
                    createInvoice.Date.getText().toString(), createInvoice.CustomerName.getText().toString()));
            List<FileLine> newFileLines = new ArrayList<>();
            String[] productNames = createInvoice.ProductName.getText().toString().split(",");
            String[] prices = createInvoice.Price.getText().toString().split(",");
            String[] quantities = createInvoice.Quantity.getText().toString().split(",");

            for (int i = 0; i < productNames.length; i++) {
                newFileLines.add(new FileLine(createInvoice.InvoiceNumber.getText().toString(), productNames[i],
                        Double.parseDouble(prices[i]), Integer.parseInt(quantities[i])));
            }

            headers.get(headers.size() - 1).setFileLines(newFileLines);
            loadHeaderContent(null);
            createInvoice.setVisible(false);
        }
        if (e.getActionCommand().equals("Delete")) {
            if (headers != null && panel.table1.getSelectedRow() != -1) {
                headers.remove(panel.table1.getSelectedRow());
                panel.table2.setModel(new DefaultTableModel());
                panel.invoiceNumberTF.setText("");
                panel.invoiceTotalTF.setText("");
                panel.customerNameTF.setText("");
                panel.dateTF.setText("");
                loadHeaderContent(null);
                if (headers.size() == 0) {
                    headers = null;
                }
            }
        }
        if (e.getActionCommand().equals("Save Line")) {
            if (!panel.table2.isEditing() && headers != null) {
                List<FileLine> fileLines = headers.get(selectedRow).getFileLines();
                headers.set(selectedRow, new Header(panel.invoiceNumberTF.getText().toString(),
                        panel.customerNameTF.getText().toString(), panel.dateTF.getText().toString()));

                for (int i = 0; i < fileLines.size(); i++) {
                    if (panel.invoiceNumberTF.getText().toString().equals(panel.table2.getValueAt(i, 0).toString())) {
                        fileLines.set(i, new FileLine(panel.table2.getValueAt(i, 0).toString()
                                , panel.table2.getValueAt(i, 1).toString()
                                , Double.parseDouble(panel.table2.getValueAt(i, 2).toString())
                                , Integer.parseInt(panel.table2.getValueAt(i, 3).toString())));
                    } else {

                        for (int j = 0; j < headers.size(); j++) {
                            if (headers.get(j).invoiceNumber.equals(panel.table2.getValueAt(i, 0).toString())) {
                                fileLines.set(i, new FileLine(panel.table2.getValueAt(i, 0).toString()
                                        , panel.table2.getValueAt(i, 1).toString()
                                        , Double.parseDouble(panel.table2.getValueAt(i, 2).toString())
                                        , Integer.parseInt(panel.table2.getValueAt(i, 3).toString())));
                                List<FileLine> additionalLine = headers.get(j).getFileLines();
                                boolean match = false;
                                for (int z = 0; z < additionalLine.size(); z++) {
                                    if (additionalLine.get(z).productName.equals(fileLines.get(i).productName)
                                            && additionalLine.get(z).price == fileLines.get(i).price) {
                                        additionalLine.get(z).quantity += fileLines.get(i).quantity;
                                        match = true;
                                    }
                                }
                                if (!match) {
                                    additionalLine.add(fileLines.get(i));
                                }
                                headers.get(j).setFileLines(additionalLine);
                            }
                        }
                        fileLines.remove(i);
                    }
                }
                headers.get(selectedRow).setFileLines(fileLines);
                //headers.get(panel.table1.getSelectedRow()).setFileLines();
                loadHeaderContent(null);
                viewLineTable();
            }
        }
        if (e.getActionCommand().equals("Cancel Changes")) {
            if (headers != null && !panel.table2.isEditing())
                viewLineTable();
        }
        if (e.getActionCommand().equals("Save File")) {
            if(headerData!=null && lineData!=null) {
                setFileContents("Choose Header Save Location", headerData);
                List<String[]> temp =new ArrayList<>();

                for(int i =0; i< headers.size() ;i++) {
                    for (int j = 0; j < headers.get(i).getFileLines().size(); j++) {
                        temp.add(headers.get(i).getFileLines().get(j).getLineInfoWithoutTotalCost());

                    }
                }
                lineData =new String[temp.size()][4];
                for(int i =0; i< temp.size();i++)
                {
                    lineData[i] = temp.get(i);
                }
                setFileContents("Choose Line Save Location", lineData);
            }
        }

    }
    void loadHeaderContent(List<String> contents) {
        if(contents!= null) {
            headerData = new String[contents.size()][4];
            headers = new ArrayList<>();

            for (int i = 0; i < contents.size(); i++) {
                String[] splitContent= contents.get(i).split(",");
                for(int j=0;j<splitContent.length;j++)
                    headerData[i][j] = splitContent[j];
            }

            for (int i = 0; i < contents.size(); i++) {
                headers.add(new Header(headerData[i][0], headerData[i][1], headerData[i][2]));
            }
        }
        else
        {
            headerData= new String[headers.size()][4];
            for(int i =0;i<headers.size();i++)
            {
                headerData[i]=headers.get(i).getHeaderInfo();
            }
        }
        String[] cols = {"Invoice Number", "Date" , "Customer","Total"};

        panel.table1.setModel(new DefaultTableModel(headerData,cols));
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
        if(panel.table1.getSelectedRow() != -1)
            this.selectedRow=panel.table1.getSelectedRow();

        String[][] data = new String[headers.get(selectedRow).getFileLines().size()][4];
        for (int i =0; i<headers.get(selectedRow).getFileLines().size();i++)
        {
            data[i] = headers.get(selectedRow).getFileLines().get(i).getLineInfo();
        }
        String[] cols = {"Invoice Number", "Item Name", "Item Price", "Count","Item Total"};

        panel.table2.setModel(new DefaultTableModel(data,cols));
        panel.invoiceNumberTF.setText(headers.get(selectedRow).getInvoiceNumber());
        panel.dateTF.setText(headers.get(selectedRow).date);
        panel.customerNameTF.setText(headers.get(selectedRow).name);
        panel.invoiceTotalTF.setText(Double.toString(headers.get(selectedRow).getTotalCost()));
    }
    void loadLineContent(List<String> contents) {
        lineData = new String[contents.size()][5];
        FileLine[] fileLines = new FileLine[contents.size()];

        for (int i =0; i<contents.size();i++)
        {
            String[] splitContent= contents.get(i).split(",");
            for(int j=0;j<splitContent.length;j++)
                lineData[i][j] = splitContent[j];
        }

        for(int i =0; i<fileLines.length;i++)
        {
            fileLines[i]= new FileLine(lineData[i][0],lineData[i][1], Double.parseDouble(lineData[i][2]),Integer.parseInt(lineData[i][3]));
        }

        for(int i =0 ; i< headers.size();i++)
        {
            List<FileLine> correspondingFileLines = new ArrayList<>();
            for(int j =0; j< fileLines.length;j++)
            {
                if(headers.get(i).getInvoiceNumber().equals(fileLines[j].getInvoiceNumber()))
                {
                    correspondingFileLines.add(fileLines[j]);
                }
            }
            headers.get(i).setFileLines(correspondingFileLines);
        }

        if(contents!=null)
        {
            String[] cols = {"Invoice Number", "Date" , "Customer","Total"};
            for(int i=0; i<headers.size();i++)
            {
                headerData[i][3] = Double.toString(headers.get(i).getTotalCost());
            }
            panel.table1.setModel(new DefaultTableModel(headerData,cols));
        }
    }
    List<String> getFileContents(String name) {
        List<String> contents = new ArrayList<>();
        fileChooser.setDialogTitle(name);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getPath();
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
        return contents;
    }
    void setFileContents(String name,String[][] data) {
        FileWriter fileWriter = null;
        fileChooser.setDialogTitle(name);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getPath();
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

        }
    }
}
