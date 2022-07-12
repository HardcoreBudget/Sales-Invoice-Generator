package model;

public class InvoiceLine {
    public String invoiceNumber;
    public String itemName;
    public double price;
    public int count;

    public InvoiceLine(String invoiceNumber, String itemName, double price, int count)
    {
        this.invoiceNumber =invoiceNumber;
        this.itemName = itemName;
        this.price=price;
        this.count = count;
    }

    public String getInvoiceNumber()
    {
        return  invoiceNumber;
    }

    public double getLineCost()
    {
        return price * count;
    }

    public String[] getLineInfo()
    {
        return new String[] {invoiceNumber, itemName, Double.toString(price), Integer.toString(count), Double.toString(getLineCost())};
    }

    public String[] getLineInfoWithoutTotalCost()
    {
        return new String[] {invoiceNumber, itemName, Double.toString(price), Integer.toString(count)};
    }
}
