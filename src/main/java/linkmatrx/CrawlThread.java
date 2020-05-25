package linkmatrx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlThread implements Runnable {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static String baseDomain;
    static ArrayList<String> newLinksOnPageScanned = new ArrayList<String>();
    static HashMap<String, String> singlePageData = new HashMap<>();
    static HashMap<String, List<String>> singlePageLinksData = new HashMap<>();
    static HashMap<String, List<String>> singlePageCssData = new HashMap<>();
    static ArrayList<String> singlePageImageData;
    static HashMap<String, String> outBoundLinks = new HashMap<>();
    private static String url;
    private static String startUrl;

    public CrawlThread(String startUrl, String baseUrl) {
        // startUrl
        if (!startUrl.isEmpty()) {
            CrawlThread.startUrl = startUrl;
            setBaseDomain(baseUrl);
        }
    }

    @Override
    public void run() {
        scanSite();
    }

    public void scanSite() {

        String[] schemes = {"http", "https"}; // DEFAULT schemes = "http", "https", "ftp"
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(CrawlThread.startUrl)) {
            //		   System.out.println("URL is valid");
        } else {
            return;
        }
        // scan url and get data
        // add links to scan
        int connStatus = 404;
        Document htmlDocumentGET = null;
        Connection connection = null;

        try {
            connection = Jsoup.connect(CrawlThread.startUrl).ignoreContentType(true).userAgent(USER_AGENT).timeout(20000);
            htmlDocumentGET = connection.get();
            connStatus = connection.response().statusCode();
        } catch (IOException e) {
            System.out.println("Error: 404. URL: " + CrawlThread.startUrl);
            return;
        }

        // PageScan is the structure for outputting data to file, csv, etc.
        String header = connection.response().header("Content-Type");

        // return Links on the page
        Elements linksOnPage = htmlDocumentGET.select("a[href]");

        HashMap<String, String> onePageData = new HashMap<>();
        onePageData.put("contentType", header);
        onePageData.put("url", CrawlThread.startUrl);
        onePageData.put("status", Integer.toString(connStatus));
        onePageData.put("date", connection.response().header("Date"));
        onePageData.put("connection", connection.response().header("Connection"));
        onePageData.put("server", connection.response().header("Server"));
        onePageData.put("PageTitle", htmlDocumentGET.getElementsByTag("title").text());
        onePageData.put("PageTitleChrCount", Integer.toString(htmlDocumentGET.getElementsByTag("title").text().length()).toString());
        onePageData.put("h1Title", htmlDocumentGET.getElementsByTag("h1").text());
        onePageData.put("h2Title", htmlDocumentGET.getElementsByTag("h2").text());
        onePageData.put("metaDescription", getMetaTag(htmlDocumentGET, "description"));
        onePageData.put("metaDescriptionCount", Integer.toString(getMetaTag(htmlDocumentGET, "description").length()).toString());
        onePageData.put("metaKeywords", getMetaTag(htmlDocumentGET, "keywords"));
        onePageData.put("metaKeywordsCount", Integer.toString(getMetaTag(htmlDocumentGET, "keywords").length()).toString());
        onePageData.put("metaRobots", getMetaTag(htmlDocumentGET, "robots"));
        onePageData.put("rawLinksOnPageCount", Integer.toString(linksOnPage.size()).toString());
        onePageData.put("canonical", getLinkRel(htmlDocumentGET, "canonical"));

        HashMap<String, List<String>> onePageCssData = new HashMap<>();
        List<String> pageCss = new ArrayList<String>();
        for (Element css : htmlDocumentGET.select("link[rel=stylesheet]")) {
            pageCss.add(css.attr("href").toString());
        }
        onePageCssData.put("css", pageCss);

        // add links
        HashMap<String, List<String>> onePageLinksData = new HashMap<>();
        List<String> pageLinks = new ArrayList<String>();

        for (Element linkOnPage : linksOnPage) {
            pageLinks.add(linkOnPage.absUrl("href"));
            // true/false whether to add links, if outbound links add to outbound links list
            boolean urlFilter = preLinkFilter(linkOnPage.absUrl("href"), urlValidator, CrawlThread.startUrl);
            if (urlFilter) {
                CrawlThread.newLinksOnPageScanned.add(linkOnPage.absUrl("href"));
            }
        }

        onePageLinksData.put("links", pageLinks);

        boolean showImgs = ConfigManager.getshowImages();
        if (showImgs) {
            ArrayList<String> pageImages = new ArrayList<String>();
            // Get Images
            Elements img = htmlDocumentGET.getElementsByTag("img");
            for (Element el : img) {
                String src = el.absUrl("src");
                pageImages.add(src);
            }
            CrawlThread.setSinglePageImageData(pageImages);
        }

        CrawlThread.setSinglePageData(onePageData);
        CrawlThread.setSinglePageCssData(onePageCssData);
        CrawlThread.setSinglePageLinksData(onePageLinksData);

        PageScan ps = new PageScan(CrawlThread.startUrl);
        url = ps.getUrl();

    }

    private static void setSinglePageImageData(ArrayList<String> pageImages) {
        CrawlThread.singlePageImageData = pageImages;
    }

    public static ArrayList<String> getSinglePageImageData() {
        return singlePageImageData;
    }

    private static void setSinglePageLinksData(HashMap<String, List<String>> onePageLinksData) {
//    	System.out.println(onePageLinksData);
        CrawlThread.singlePageLinksData.putAll(onePageLinksData);
    }

    private static void setSinglePageCssData(HashMap<String, List<String>> onePageCssData) {
        CrawlThread.singlePageCssData.putAll(onePageCssData);
    }

    public static HashMap<String, List<String>> getSinglePageLinksData() {
        return singlePageLinksData;
    }

    public static HashMap<String, List<String>> getSinglePageCssData() {
        return singlePageCssData;
    }

    public static HashMap<String, String> getSinglePageData() {
        return singlePageData;
    }

    //get rel links
    private static String getLinkRel(Document document, String attr) {
        Elements elements = document.select("link[rel=" + attr + "]");
        for (Element element : elements) {
            final String s = element.attr("href");
            if (s != null) return s;
        }
        elements = document.select("link[property=" + attr + "]");
        for (Element element : elements) {
            final String s = element.attr("href");
            if (s != null) return s;
        }
        return "";
    }

    //get meta links
    private static String getMetaTag(Document document, String attr) {
        Elements elements = document.select("meta[name=" + attr + "]");
        for (Element element : elements) {
            final String s = element.attr("content");
            if (s != null) return s;
        }
        elements = document.select("meta[property=" + attr + "]");
        for (Element element : elements) {
            final String s = element.attr("content");
            if (s != null) return s;
        }
        return "";
    }

    // todo - test output order
    private static void setSinglePageData(HashMap<String, String> onePageAppendedData) {
        CrawlThread.singlePageData.putAll(onePageAppendedData);
    }

    // Testing only - todo
    public static String convertWithStream(Map<String, ?> map) {
        return map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private boolean preLinkFilter(String absUrl, UrlValidator urlValidator, String parentPageUrl) {
        //String domainName = "site.com";
        String domainName = getBaseDomain();
        // regex filters here
        String domainFilter = ".*" + domainName + ".*";
        // stay on domain

        if (!urlValidator.isValid(absUrl)) {
            // is the URL a valid one?
            return false;
        }
        // get rid of #
        if (Pattern.matches(".*#.*", absUrl)) {
            return false;
        }
        // eliminate long double directories of CMS's
        if (Pattern.matches("^.*?(\\/.+?\\/).*?\\1.*$|^.*?\\/(.+?\\/)\\2.*$", absUrl)) {
            return false;
        }
        // eliminate repeating directories, common names
        if (Pattern.matches("^.*(\\/misc|\\/sites|\\/all|\\/themes|\\/modules|\\/profiles|\\/css|\\/field|\\/node|\\/theme){3}.*$", absUrl)) {
            return false;
        }

        if (Pattern.matches("\b" + domainName + "\b", absUrl)) {
            outBoundLinks.put(absUrl, parentPageUrl);
        }
        if (!Pattern.matches(domainFilter, absUrl)) {
            // is this the same domain?
            return false;
        }
        return true;


    }

    public static ArrayList<String> getLinksOnPage() {
        return CrawlThread.newLinksOnPageScanned;
    }

    public static String geturl() {
        return url;
    }

    public static String getBaseDomain() {
        return baseDomain;
    }

    public static void setBaseDomain(String baseDomain) {
        CrawlThread.baseDomain = baseDomain;
    }

}
