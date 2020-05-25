package linkmatrx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrawlReturnData {

    private String url;
    private ArrayList<String> linksOnPage;
    private HashMap<String, String> singlePageData;
    private HashMap<String, List<String>> singlePageLinksData;
    private HashMap<String, List<String>> singlePageCssData;
    private ArrayList<String> singlePageImages;

    public CrawlReturnData(String url, ArrayList<String> linksOnPage, HashMap<String, String> singlePageData, HashMap<String, List<String>> links, HashMap<String, List<String>> css, ArrayList<String> images) {
        this.url = url;
        this.linksOnPage = linksOnPage;
        this.setsinglePageData(singlePageData);
        this.setSinglePageLinksData(links);
        this.setSinglePageCssData(css);
        this.setSinglePageImageData(images);

    }

    private void setSinglePageImageData(ArrayList<String> images) {
        this.singlePageImages = images;
    }

    public ArrayList<String> getSinglePageImageData() {
        return this.singlePageImages;
    }

    public ArrayList<String> getlinksOnPage() {
        return this.linksOnPage;
    }

    public String getUrl() {
        return this.url;
    }

    public HashMap<String, String> getsinglePageData() {
        return singlePageData;
    }

    public void setsinglePageData(HashMap<String, String> singlePageData) {
        this.singlePageData = singlePageData;
    }

    public HashMap<String, List<String>> getSinglePageLinksData() {
        return singlePageLinksData;
    }

    public void setSinglePageLinksData(HashMap<String, List<String>> singlePageLinksData) {
        this.singlePageLinksData = singlePageLinksData;
    }

    public HashMap<String, List<String>> getSinglePageCssData() {
        return singlePageCssData;
    }

    public void setSinglePageCssData(HashMap<String, List<String>> singlePageCssData) {
        this.singlePageCssData = singlePageCssData;
    }

}
