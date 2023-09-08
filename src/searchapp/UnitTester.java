/****************************************************************************************************
 * File name: UnitTester.java
 * Author: Kevin Lai, 101288231
 * Course: COMP 1406Z
 * Assignment: Course Project
 * Date: 2022 November 30
 * Professor: Dave McKenney
 * Purpose: Class that implements the ProjectTester Class used for unit testing
 ****************************************************************************************************/
package searchapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class UnitTester implements ProjectTester{
    private SearchApplication searchApp;
    private SearchData searchData;
    /**
     * Default Constructor for UnitTester , instantiates searchApp
     */
    public UnitTester() {
        searchApp = new SearchApplication();
    }

    /**
     * Method to initialize all attributes. This method is invoked at the start of the application before the
     * crawl process. it ensures all data is deleted and any initialization  are made before the crawl.
     */
    @Override
    public void initialize() {
        searchApp.initialize();
    }
    /**
     * This method performs a crawl starting at the given seed URL. It should visit each page it can find once.
     * It should not stop until it has visited all reachable pages. All data required for later search queries
     * should be saved in files once this completes.
     * @param seedURL - starting url to crawl from.
     */
    @Override
    public void crawl(String seedURL) {
        try {
            searchApp.getCrawl().crawl(seedURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Returns a list of the outgoing links of the page with the given URL.That is, the URLs that the
     * page with the given URL links to. If no page with the given URL exists, returns null.
     * @param url - the given url to retrieve outgoing links for.
     * @return searchData.getOutgoingLinks(url) - list of outgoing urls for the given url. null - if given url is not found
     */
    @Override
    public List<String> getOutgoingLinks(String url) {
        searchData = new SearchData(searchApp.getCrawl());
        try {
            return searchData.getOutgoingLinks(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Returns a list of the incoming links for the page with the given URL. That is, the URLs that link to
     * the page with the given URL If no page with the given URL exists, returns null.
     * @param url - the given url to retrieve incoming links for.
     * @return searchData.getIncomingLinks(url) - list of incoming urls for the given url. null - if given url is not found
     */
    @Override
    public List<String> getIncomingLinks(String url) {
        searchData = new SearchData(searchApp.getCrawl());
        try {
            return searchData.getIncomingLinks(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Returns the PageRank value for the page with the given URL. If no page with the given URL exists, returns -1.
     * @param url - the given url to retrieve outgoing links for.
     * @param url - the url to retrieve page_rank for
     * @return searchData.getPageRank(url) - the page rank for the url given: -1: if the given url is not found
     */
    @Override
    public double getPageRank(String url) {
        searchData = new SearchData(searchApp.getCrawl());
        try {
            return searchData.getPageRank(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Returns the IDF value for the given word. A word that did not show up during the crawl should have an IDF of 0.
     * @param word - the word to fetch idf
     * @return searchData.getIDF(word) - idf value for the given word
     */
    @Override
    public double getIDF(String word) {
        searchData = new SearchData(searchApp.getCrawl());
        try {
            return searchData.getIDF(word);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Returns the term frequency of the given word within the page with the given URL. If the word did not
     * appear on the given page, the TF should be 0.
     * @param url - url to fetch td
     * @param word - word to get term frequency
     * @return searchData.getTF(url,word) - the term frequency for the word on the current url webpage:
     *         0 - if the url or tf file is not found
     */
    @Override
    public double getTF(String url, String word) {
        searchData = new SearchData(searchApp.getCrawl());
        try {
            return searchData.getTF(url,word);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Returns the TF-IDF value of the given word within the page with the given URL.
     * @param url - url to fetch tf_idf
     * @param word - word to get tf_idf
     * @return searchData.getTFIDF(url,word) - the tf_idf for the word on the current url webpage:
     *         0 - if the url or tf_idf file is not found
     */
    @Override
    public double getTFIDF(String url, String word) {
        searchData = new SearchData(searchApp.getCrawl());
        try {
            return searchData.getTFIDF(url,word);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Performs a search using the given query.
     * If boost is true, the search score for a page should be boosted by the page's PageRank value.
     * If boost is false, the search score for a page will be only based on cosine similarity.
     * This method must return a list of objects that implement the SearchResult interface.
     * The list should return the top X search results for the given query/boost values.
     * Results should be sorted from highest to lowest score.
     * @param query - the search query
     * @param boost - if page rank should be used
     * @param X - number of top pages to display
     * @return topPages - list of top X webpages based off cosine similarity score
     */
    @Override
    public List<SearchResult> search(String query, boolean boost, int X) {
        try {
            return searchApp.getModel().search(query,boost,X);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
