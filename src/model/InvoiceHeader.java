package model;
import java.util.ArrayList;
import java.util.List;

public class InvoiceHeader {
    public String invoiceNumber;
    public String customerName;
    public String date;
    List<InvoiceLine> invoiceLines = new ArrayList<>();

    public InvoiceHeader(String invoiceNumber, String date, String customerName)
    {
        this.invoiceNumber =invoiceNumber;
        this.customerName = customerName;
        this.date=date;
    }

    public List<InvoiceLine> getFileLines() {
        return invoiceLines;
    }

    public void setFileLines(List<InvoiceLine> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }

    public String[] getHeaderInfo()
    {
        return new String[] {invoiceNumber, customerName,date,Double.toString(getTotalCost())};
    }


    public double getTotalCost()
    {
        double totalCost=0;
        for (InvoiceLine invoiceLine : invoiceLines) {
            totalCost += invoiceLine.getLineCost();
        }
        return totalCost;
    }
}
