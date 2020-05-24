package linkmatrx;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import com.opencsv.CSVWriter;

public class ToCsvW {

	private String csvname;
    private CSVWriter csvwriter;
    private List<String[]> csvline = new ArrayList<String[]>();
    
   // private static final Logger LOGGER = Logger.getLogger(ToCsvW.class.getName());
    
    ToCsvW(String csvname) 
    {
        this.setfilename(csvname);   
    }
    
    public void initcsv() {
        try {
            this.csvwriter = new CSVWriter(new FileWriter("reports/"+getfilename()));
        } catch (IOException ex) {
        	Logging.log(ex.toString());
           // System.out.println(ex);
        }
        //write first line of csv, header rows - labels
        //
        //String csvtext = "Link #,Status Code,URL,Links on Page,Title Tag,Title Tag Count,Meta Description,Meta Description Count,Meta Keywords,Meta Keywords Count,H1 Tag(s),H1 Tag Count,H2 Tag(s),H2 Tag Count,Robots,Canonical,Page Speed,Last Modified";
        this.csvline.add(new String[] {"Date","Server","CSS","Images","On Page Links","H2 Title","Page Title","Canonical","Links On Page","Meta Description","Meta Keyword Count","Url","Meta Robots","Meta Description Count","Meta Keywords","Page Title Count","H1","Connection","Content Type","Status"});
    }
    
    public void setArray(HashMap<String, String> getsinglePageData)
    {
        //System.out.println("Data is HERE: " + data.isEmpty());
        //replace linebreaks with *
        //String repl = data.replaceAll("(\\r|\\n|\\r\\n)+", "\\*");
        //String[] segs = repl.split("\\*");
        //System.out.println(data);
        ArrayList<String> csvlineEach = new ArrayList<String>();
        
        for (Entry<String, String> page : getsinglePageData.entrySet()) {
        	// page.getValue()
        	csvlineEach.add(page.getValue());
        }
        
        try {
        //build string
        this.csvline.add(new String[] {csvlineEach.get(0), csvlineEach.get(1),csvlineEach.get(2),csvlineEach.get(3),csvlineEach.get(4),csvlineEach.get(5),csvlineEach.get(6),csvlineEach.get(7),csvlineEach.get(8),csvlineEach.get(9),csvlineEach.get(10),csvlineEach.get(11),csvlineEach.get(12),csvlineEach.get(13),csvlineEach.get(14),csvlineEach.get(15),csvlineEach.get(16),csvlineEach.get(17),csvlineEach.get(18),csvlineEach.get(19) });
        
        } catch (IndexOutOfBoundsException e) {
        	Logging.log("Error: CSV Line Caught Here");
        	Logging.log(e.toString());
            //System.out.println("Error: CSV Line Caught Here\n");
            //System.out.println(e.toString() + "\n");
                    
        }
    }
    
    public void towritefile() {
        
        this.csvwriter.writeAll(this.csvline); 
        try {
            this.csvwriter.close();
        } catch (IOException ex) {
        	Logging.log(ex.toString());
            //System.out.println(ex);
        }
        System.out.println("\nCSV File Created!");
    }
   
    
    private String getfilename()
    {
        return this.csvname;
    }
    
    public void setfilename(String weburl) {
        Date today = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddhhmm");
        String date = DATE_FORMAT.format(today);
        this.csvname = weburl + date + ".csv";
    }
    
}
