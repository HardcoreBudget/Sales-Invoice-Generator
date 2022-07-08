package InputOutput;

public class FileLine {
    String invoiceNumber;
    String productName;
    double price;
    int quantity;

    public FileLine(String invoiceNumber, String productName, double price, int quantity)
    {
        this.invoiceNumber =invoiceNumber;
        this.productName=productName;
        this.price=price;
        this.quantity=quantity;
    }

    public String getInvoiceNumber()
    {
        return  invoiceNumber;
    }

    public double getLineCost()
    {
        return price * quantity;
    }

    public String[] getLineInfo()
    {
        return new String[] {invoiceNumber,productName, Double.toString(price), Integer.toString(quantity), Double.toString(getLineCost())};
    }

    public String[] getLineInfoWithoutTotalCost()
    {
        return new String[] {invoiceNumber,productName, Double.toString(price), Integer.toString(quantity)};
    }
}
