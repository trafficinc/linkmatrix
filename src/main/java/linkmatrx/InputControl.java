package linkmatrx;

import jdk.jfr.internal.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class InputControl {

    private static int crawlDepth = 1;
    private static int crawlCounter = 1;
    private static int consoleViewCounter = 1;
    private static ArrayList<String> pagesthatneedtobevisited = new ArrayList<String>();
    private static ArrayList<String> pagevisited = new ArrayList<String>();
    private static String baseDomain;
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
            Logging.log("URL: " + startUrl + "- Error: " + e.toString());
        }
        setBaseDomain(urlTest.getHost());

        if (InputControl.getGenerateCSV() == 1) {
            ToCsvW tocsv = new ToCsvW(InputControl.baseDomain);
            tocsv.initcsv();
            InputControl.setCsv(tocsv);
        }

        // Crawl Type
        urlCrawl(startUrl);

    }

    public void urlCrawl(String startUrl) {
        boolean badURL = false;
        // filters for returned pages
        if (InputControl.doFilters(startUrl)) {
            badURL = true;
        }

        CrawlReturnData page = startCrawl(startUrl);

        if (!badURL) {

            // CSV View
            if (InputControl.getGenerateCSV() == 1) {
                CsvView.viewData(page.getsinglePageData(), page.getSinglePageCssData().get("css"), page.getSinglePageLinksData().get("links"), page.getSinglePageImageData());
            }

            // Console View
            System.out.print(consoleViewCounter++);
            System.out.println(".)");
            ConsoleView.viewData(page.getsinglePageData(), page.getSinglePageCssData().get("css"), page.getSinglePageLinksData().get("links"), page.getSinglePageImageData());
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
            InputControl.setPagesthatneedtobevisitedClean(page.getlinksOnPage());
        }

        // remove pages
        InputControl.removeFrompagesthatneedtobevisited(startUrl);
        InputControl.setPagevisited(startUrl);

        if (!InputControl.getPagesthatneedtobevisited().isEmpty()) {
            urlCrawl(InputControl.getPagesthatneedtobevisited().get(0));
        } else {
            if (InputControl.getGenerateCSV() == 1) {
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

    }

    private static void removeFrompagesthatneedtobevisited(String url) {
        InputControl.pagesthatneedtobevisited.remove(url);
    }

    public CrawlReturnData startCrawl(String startUrl) {
        if (!startUrl.isEmpty()) {

            try {
                TimeUnit.MILLISECONDS.sleep(100);
                CrawlThread cthread = new CrawlThread(startUrl, getBaseDomain());
                Thread t1 = new Thread(cthread);
                t1.start();
                t1.join();
            } catch (InterruptedException e1) {
                Logging.log(e1.toString());
            }
        }
        return new CrawlReturnData(CrawlThread.geturl(), CrawlThread.getLinksOnPage(), CrawlThread.getSinglePageData(), CrawlThread.getSinglePageLinksData(), CrawlThread.getSinglePageCssData(), CrawlThread.getSinglePageImageData());
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
        if (pages.size() > 0) {
            for (String page : pages) {
                if (!InputControl.pagesthatneedtobevisited.contains(page)) {
                    InputControl.setPagesthatneedtobevisited(page);
                }
            }
        }
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

}

// Output/view classes

final class CsvView {

    public static void viewData(HashMap<String, String> getsinglePageData, List<String> css, List<String> links, ArrayList<String> images) {
        String[] imageStr = GetStringArray(images);
        ToCsvW csv = InputControl.getCsv();
        getsinglePageData.put("css", css.toString());
        getsinglePageData.put("images", Arrays.toString(imageStr));
        getsinglePageData.put("onPageLinks", links.toString());
        csv.setArray(getsinglePageData);
    }

    public static String[] GetStringArray(ArrayList<String> arr) {
        // declaration and initialize String Array 
        String[] str = null;
        if (arr != null) {
            str = new String[arr.size()];
            // ArrayList to Array Conversion
            for (int j = 0; j < arr.size(); j++) {
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
