package linkmatrx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/*
 * -d = depth, -u = site url 
 * -m url -u https://www.site.com -o csv  -d 1
 * java -jar linkmatrix.jar -m url -u https://www.site.com -o console  -d 1
 * */

public class InputControl {
	
	private static int crawlDepth = 1;
	private static int crawlCounter = 1;
	private static int consoleViewCounter = 1;
	private static ArrayList<String> pagesthatneedtobevisited = new ArrayList<String>();
	private static ArrayList<String> pagevisited = new ArrayList<String>();
	private static String baseDomain;
	//private static String fullUrl;
	private static ToCsvW csv;
	private static int generateCSV = 0; // 0 = no, 1 = yes

	public InputControl(String startUrl, int docsv, int crawlDepth) {
		InputControl.setcrawlDepth(crawlDepth);
		InputControl.setGenerateCSV(docsv);
		
		// set original base url, for use in filter
		URL urlTest = null;
		try {
			urlTest = new URL(startUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		setBaseDomain(urlTest.getHost());
		
		//fullUrl = urlTest.getProtocol() + "://" + urlTest.getHost();
		
//		boolean respectRobotsTxt = false; // todo
//		
//		if (respectRobotsTxt) {
//			boolean robotsFileExists = false;
//			try {
//				robotsFileExists = checkRobotsFile(fullUrl);
//				if (robotsFileExists) {
//					// then read file and add urls to not scan
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		//System.out.println(robotsFile);
		//System.out.println(fullUrl);
		//System.exit(0);
		
		if (InputControl.getGenerateCSV() == 1){
			ToCsvW tocsv = new ToCsvW(InputControl.baseDomain);
	 		tocsv.initcsv();
			InputControl.setCsv(tocsv);
		}
		
		// Crawl Type
		urlCrawl(startUrl);

	}

	// todo
//	private boolean checkRobotsFile(String fullUrl2) throws IOException {
//		URL url;
//		try {
//			HttpURLConnection.setFollowRedirects(false);
//			HttpURLConnection con = (HttpURLConnection) new URL(fullUrl2 + "/robots.txt").openConnection();
//			con.setRequestMethod("HEAD");
//			//System.out.println(con.getURL());
//		    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
//		    	return true;
//		    } else {
//		    	return false;
//		    }
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    return false;
//		
//	}

	public void urlCrawl(String startUrl) {
		
//		CrawlReturnData page = startCrawl(startUrl);
		//############### DATA #########################
//		System.out.println("PAGE DATA");
//		System.out.println(page.getsinglePageData());
//		System.out.println("CSS");
//		System.out.println(page.getSinglePageCssData().get("css"));
//		System.out.println("LINKS");
//		System.out.println(page.getSinglePageLinksData().get("links"));
		boolean badURL = false;
		// filters for returned pages
		if ( InputControl.doFilters(startUrl) ) {
			//System.out.println("Bad URL");
			badURL = true;
			// remove pages
			//InputControl.removeFrompagesthatneedtobevisited(startUrl);
			//InputControl.setPagevisited(startUrl);
			
//			if (!InputControl.getPagesthatneedtobevisited().isEmpty()) {
////				System.out.println("InputControl.getPagesthatneedtobevisited().get(0)");
////				System.out.println(InputControl.getPagesthatneedtobevisited().size());
////				System.out.println(InputControl.getPagesthatneedtobevisited().get(0));
//				urlCrawl(InputControl.getPagesthatneedtobevisited().get(0));
//			} else {
//				if (InputControl.getGenerateCSV() == 1){
//					ToCsvW csv = InputControl.getCsv();
//					csv.towritefile();
//				}
//				//System.out.println("Done! #1");
//				System.out.println("Done!");
//				System.exit(0);
//			}
			//System.out.println("Done! #1");
			//return;
		}
		
		//System.out.println(badURL);
		
			
		CrawlReturnData page = startCrawl(startUrl);
		
		if (!badURL) {
			
		// CSV View
		if (InputControl.getGenerateCSV() == 1){
			CsvView.viewData(page.getsinglePageData(),page.getSinglePageCssData().get("css"),page.getSinglePageLinksData().get("links"), page.getSinglePageImageData());
		}
		
		// Console View
		System.out.print(consoleViewCounter++);
		System.out.println(".)");
		ConsoleView.viewData(page.getsinglePageData(),page.getSinglePageCssData().get("css"),page.getSinglePageLinksData().get("links"),page.getSinglePageImageData());
		System.out.println("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
		System.out.println("");
		
		// Just do 1 url, uncomment below.
		//System.exit(0);
		
		}
		
		// filters only return html pages, css, js, png, jpg, php, etc.
		// pass filters into [setPagesthatneedtobevisitedClean]
		
			int crawlCount = crawlCounter++;
			if (crawlCount > InputControl.getcrawlDepth()) {
				//System.out.println("now should stop");
			} else {
				//System.out.println("run...");
				InputControl.setPagesthatneedtobevisitedClean(page.getlinksOnPage());
			}
		
//			InputControl.setPagesthatneedtobevisitedClean(page.getlinksOnPage());
			
//			System.out.println(InputControl.getPagesthatneedtobevisited());
//			System.out.println(InputControl.getPagesthatneedtobevisited().size());
			
			// remove pages
			InputControl.removeFrompagesthatneedtobevisited(startUrl);
			InputControl.setPagevisited(startUrl);
			
			if (!InputControl.getPagesthatneedtobevisited().isEmpty()) {
//				System.out.println("not empty yet...");
				
				
				//System.out.println(startUrl);
//				System.out.println(InputControl.getPagesthatneedtobevisited());
//				System.out.println(InputControl.getPagesthatneedtobevisited().size());
//				System.out.println(InputControl.getPagesthatneedtobevisited().get(0));
				urlCrawl(InputControl.getPagesthatneedtobevisited().get(0));
				//System.exit(0);
			} else {
				if (InputControl.getGenerateCSV() == 1){
					ToCsvW csv = InputControl.getCsv();
					csv.towritefile();
				}
				System.out.println("Done!");
				System.exit(0);
			}
			
		
				
	}
	

	private static boolean doFilters(String startUrl) {
		// filter by extension
		String getExt = "";
		if (startUrl.length() > 3) {
			getExt = startUrl.substring(startUrl.length() - 3);
		} else {
		  // whatever is appropriate in this case
		  throw new IllegalArgumentException("word has less than 3 characters!");
		}
		
		//String[] exts = new String[]{"pdf","jpg","gif","png"};
		ArrayList<String> exts = ConfigManager.getIgnorelist();
		ArrayList<Integer> skipTrack = new ArrayList<Integer>();
		for (int i = 0; i < exts.size(); i++) {
			if (getExt.contentEquals(exts.get(i))) {
				skipTrack.add(1);
			}
		}
		if (skipTrack.size() > 0) {
			return true;
		} else {
			return false;
		}
//		System.out.println(skipTrack);
//		return false;
		
	}

	private static void removeFrompagesthatneedtobevisited(String url) {
		 InputControl.pagesthatneedtobevisited.remove(url);
	}

	
	public CrawlReturnData startCrawl(String startUrl) {
		if (!startUrl.isEmpty()) {
			
			//Random rnd = new Random();
			try {
				TimeUnit.MILLISECONDS.sleep(100);
				
				CrawlThread cthread = new CrawlThread(startUrl, getBaseDomain());
				Thread t1 = new Thread(cthread);
				t1.start();
				//System.out.println(" starts " + t1.getName() + ".");
				
				//TimeUnit.MILLISECONDS.sleep(500);
				 
				t1.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

			
//			try {
//				t1.join(); // wait until thread is done
//			} catch (InterruptedException e) {
//				Logging.log(e.toString());
//				e.printStackTrace();
//			}
			
			// Return results from crawl
	//		System.out.println(counter++);
	//		System.out.println(CrawlThread.geturl());
	//		System.out.println(CrawlThread.getLinksOnPage());
			
		}
//		System.out.println( "CrawlThread.getSinglePageData()" );
//		System.out.println( CrawlThread.getSinglePageData() );
		return new CrawlReturnData(CrawlThread.geturl(),CrawlThread.getLinksOnPage(), CrawlThread.getSinglePageData(), CrawlThread.getSinglePageLinksData(), CrawlThread.getSinglePageCssData(), CrawlThread.getSinglePageImageData());
	}

	public static ArrayList<String> getPagevisited() {
		return InputControl.pagevisited;
	}

	public static void setPagevisited(String pagevisited) {
		InputControl.pagevisited.add(pagevisited);
	}

	public static ArrayList<String> getPagesthatneedtobevisited() {
		return InputControl.pagesthatneedtobevisited;
	}

	public static void setPagesthatneedtobevisited(String pagesthatneedtobevisited) {
		InputControl.pagesthatneedtobevisited.add(pagesthatneedtobevisited);
	}
	
	public static void setPagesthatneedtobevisitedAll(ArrayList<String> pagesthatneedtobevisited) {
		InputControl.pagesthatneedtobevisited.addAll(pagesthatneedtobevisited);
	}
	
	// clean urls check if not already in collection, if not - add them
	public static void setPagesthatneedtobevisitedClean(ArrayList<String> pages) {	
		//System.out.println(InputControl.pagesthatneedtobevisited);
		//String page = page.getlinksOnPage();
		if (pages.size() > 0) {
			for(String page : pages) {
				//System.out.println(page);
				if ( !InputControl.pagesthatneedtobevisited.contains(page) ) {
					InputControl.setPagesthatneedtobevisited(page);
				}
			}
		}
		//System.out.println(InputControl.pagesthatneedtobevisited);
	}

	public static String getBaseDomain() {
		return baseDomain;
	}

	public static void setBaseDomain(String baseDomain) {
		InputControl.baseDomain = baseDomain;
	}

	public static ToCsvW getCsv() {
		return csv;
	}

	public static void setCsv(ToCsvW csv) {
		InputControl.csv = csv;
	}

	public static int getGenerateCSV() {
		return generateCSV;
	}

	public static void setGenerateCSV(int generateCSV) {
		InputControl.generateCSV = generateCSV;
	}
		
	public static int getcrawlDepth() {
		return crawlDepth;
	}

	public static void setcrawlDepth(int crawlDepth) {
		InputControl.crawlDepth = crawlDepth;
	}
	
	
//	public void getPageLinks(String url) {
//		
//		int connStatus = 404;
//		Document htmlDocumentGET = null;
//        Connection connection = null;
//		
//        try
//        {  
//            connection = Jsoup.connect(url).ignoreContentType(true).userAgent(USER_AGENT).timeout(20000);
//            htmlDocumentGET = connection.get();
//            connStatus = connection.response().statusCode();  
//            
//         }
//        catch(IOException e)
//        {
//        	System.out.println("IOException: " + e.toString());
//        }
//        
//        this.setHtmlDocument(htmlDocumentGET);
//        
//        System.out.println(counter++);
//        System.out.println(htmlDocumentGET.getElementsByTag("title"));
//        System.out.println("*" + htmlDocumentGET.select("a[href]") + "*");
//	}
//
//	public Document getHtmlDocument() {
//		return htmlDocument;
//	}
//
//	public void setHtmlDocument(Document htmlDocument) {
//		this.htmlDocument = htmlDocument;
//	}

}

// Output/view classes

final class CsvView {

	public static void viewData(HashMap<String, String> getsinglePageData, List<String> css, List<String> links,  ArrayList<String> images) {
		String[] imageStr = GetStringArray(images);
		ToCsvW csv = InputControl.getCsv();
		getsinglePageData.put("css",css.toString());	
		getsinglePageData.put("images",Arrays.toString(imageStr));
		getsinglePageData.put("onPageLinks",links.toString());
		csv.setArray(getsinglePageData);	
	}
	
    public static String[] GetStringArray(ArrayList<String> arr) { 
        // declaration and initialize String Array 
    	String[] str = null;
    	if (arr != null ) {
	        str = new String[arr.size()]; 
	        // ArrayList to Array Conversion 
	        for (int j = 0; j < arr.size(); j++) {
	        	System.out.println(j);
	            str[j] = arr.get(j); 
	        }   
    	}
    	return str;
    
    } 
	
}

final class ConsoleView {

	public static void viewData(HashMap<String, String> getsinglePageData, List<String> css, List<String> links, ArrayList<String> images) {
		for (Entry<String, String> page : getsinglePageData.entrySet()) {
			System.out.println(page.getKey() + " : " + page.getValue());
		}
		System.out.println("Links: " + links);
		System.out.println("CSS: " + css);
		if (images != null) {
			System.out.println("Images: " + images);
		}
		
	}

}
