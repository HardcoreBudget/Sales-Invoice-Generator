package InputOutput;
import java.util.ArrayList;
import java.util.List;

public class Header {
    String invoiceNumber;
    String name;
    String date;
    List<FileLine> fileLines = new ArrayList<>();

    public  Header(String invoiceNumber,String name, String date)
    {
        this.invoiceNumber =invoiceNumber;
        this.name=name;
        this.date=date;
    }

    public List<FileLine> getFileLines() {
        return fileLines;
    }

    public void setFileLines(List<FileLine> fileLines) {
        this.fileLines = fileLines;
    }

    public String[] getHeaderInfo()
    {
        return new String[] {invoiceNumber,name,date,Double.toString(getTotalCost())};
    }

    public String getInvoiceNumber()
    {
        return  invoiceNumber;
    }

    public double getTotalCost()
    {
        double totalCost=0;
        for(int i=0;i<fileLines.size();i++)
        {
            totalCost+= fileLines.get(i).getLineCost();
        }
        return totalCost;
    }
}
