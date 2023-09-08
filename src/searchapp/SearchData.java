/****************************************************************************************************
 * File name: SearchData.java
 * Author: Kevin Lai, 101288231
 * Course: COMP 1406Z
 * Assignment: Course Project
 * Date: 2022 November 30
 * Professor: Dave McKenney
 * Purpose: Class is responsible for fetching of all the data needed for the search engine.
 *          Since most of the data processing was done in the Crawler Class, I was able to reduce
 *          the time complexity of my SearchData Methods. The Methods retrieve data by opening
 *          a file path and reading in a URL object.
 ****************************************************************************************************/
package searchapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchData {
    private Crawler crawl;
    /**
     * Default Constructor for SearchData , Assigns crawl attribute.
     */
    public SearchData(Crawler crawl) {
        this.crawl = crawl;

    }
    /**
     * Method retrieves the outgoing links for the given url and returns a list of the outgoing links
     * @param url - the given url to retrieve outgoing links for
     * @return links - list of outgoing urls for the given url. null - if given url is not found
     */
    List<String> getOutgoingLinks(String url) throws IOException, ClassNotFoundException {
        List<String> links = new ArrayList<>();
        Path rootDirectory = Paths.get(System.getProperty("user.dir"));
        if(crawl.getUrlToNum().containsKey(url)) {
            Path outgoingLinks = Paths.get(rootDirectory.toString(), "crawler_data", String.valueOf(crawl.getUrlToNum().get(url)),"outgoing_links.txt");

            if(outgoingLinks.toFile().exists()) {
                Scanner reader = new Scanner(outgoingLinks.toFile());

                while(reader.hasNextLine()) {
                    links.add(reader.nextLine());
                }
                return links;
            }
        }
        return null;
    }
    /**
     * Method retrieves the incoming links for the given url and returns a list of the incoming links
     * @param url - the given url to retrieve incoming links for
     * @return links - list of incoming urls for the given url. null - if given url is not found
     */
    List<String> getIncomingLinks(String url) throws IOException, ClassNotFoundException {
        List<String> links = new ArrayList<>();

        Path rootDirectory = Paths.get(System.getProperty("user.dir"));
        if(crawl.getUrlToNum().containsKey(url)) {
            Path incomingLinks = Paths.get(rootDirectory.toString(), "crawler_data", String.valueOf(crawl.getUrlToNum().get(url)),"incoming_links.txt");

            if(incomingLinks.toFile().exists()) {
                Scanner reader = new Scanner(incomingLinks.toFile());
                while(reader.hasNextLine()) {
                    links.add(reader.nextLine());
                }
                return links;
            }
        }
        return null;
    }
    /**
     * Method retrieves the page rank for the given url and returns a double representing the page rank score
     * @param url - the url to retrieve page_rank for
     * @return webpage.getPageRank() - the page rank for the url given: -1: if the given url is not found
     */
    double getPageRank(String url) throws IOException, ClassNotFoundException {
        Path rootDirectory = Paths.get(System.getProperty("user.dir"));
        if(crawl.getUrlToNum().containsKey(url)) {
            Path IndexDirectory = Paths.get(rootDirectory.toString(), "crawler_data", String.valueOf(crawl.getUrlToNum().get(url)));
            Path pageRankDirectory = Paths.get(IndexDirectory.toString(),"page_rank.txt");

            if(pageRankDirectory.toFile().exists()){
                return crawl.readDataFromFile(pageRankDirectory.toFile());
            }
        }
        return -1.0f;
    }
    /**
     * Method retrieves the data for the inverse document frequency of a given word. The idf directory is
     * in the crawler_data directory and the data to be returned is in a text file where the name of the
     * file is the word given as a parameter.
     * @param word - the word to fetch idf
     * @return idf - idf value for the given word
     */
    double getIDF(String word) throws FileNotFoundException {
        Path rootDirectory = Paths.get(System.getProperty("user.dir"));
        Path idfFile = Paths.get(rootDirectory.toString(),"crawler_data","idf",word+".txt");
        double idf = 0.0;
        if(idfFile.toFile().exists()){
            idf = crawl.readDataFromFile(idfFile.toFile());
        }
        return idf;
    }
    /**
     * Method fetches the data by accessing the directories corresponding to the URL. The method then navigates to the tf directory
     * to retrieve the tf value for the given word
     * from a hash map corresponding to the word given in the parameter.
     * @param url - url to fetch td
     * @param word - word to get term frequency
     * @return tf - the term frequency for the word on the current url webpage: 0 - if the url or tf file is not found
     */
    double getTF(String url, String word) throws IOException, ClassNotFoundException {
        Path rootDirectory = Paths.get(System.getProperty("user.dir"));
        if(crawl.getUrlToNum().containsKey(url)) {
            Path IndexDirectory = Paths.get(rootDirectory.toString(), "crawler_data", String.valueOf(crawl.getUrlToNum().get(url)));
            Path tfFile = Paths.get(IndexDirectory.toString(),"tf",word+".txt");

            if (tfFile.toFile().exists()) {
                return crawl.readDataFromFile(tfFile.toFile());
            }
        }
        return 0.0f;
    }
    /**
     * Method fetches the data by accessing the directories corresponding to the URL. The method then navigates to the tf_idf directory
     * and retrieve the tf_idf value for the given word.
     * @param url - url to fetch tf_idf
     * @param word - word to get tf_idf
     * @return tf - the tf_idf for the word on the current url webpage: 0 - if the url or tf_idf file is not found
     */
    double getTFIDF(String url, String word) throws IOException, ClassNotFoundException {
        Path rootDirectory = Paths.get(System.getProperty("user.dir"));
        if(crawl.getUrlToNum().containsKey(url)) {
            Path IndexDirectory = Paths.get(rootDirectory.toString(), "crawler_data", String.valueOf(crawl.getUrlToNum().get(url)));
            Path tfIDFFile = Paths.get(IndexDirectory.toString(),"tf_idf",word+".txt");

            if (tfIDFFile.toFile().exists()) {
                return crawl.readDataFromFile(tfIDFFile.toFile());
            }
        }
        return 0.0f;
    }
    /**
     * Method fetches the data by accessing the directories corresponding to the URL. The method then reads in a URL object to retrieve the title of the page
     * @param url - url to fetch page title
     * @return title - page title : "" - if the url is not found
     */
    public String getTitle(String url) throws IOException, ClassNotFoundException {
        Path rootDirectory = Paths.get(System.getProperty("user.dir"));
        Path IndexDirectory = Paths.get(rootDirectory.toString(),"crawler_data",String.valueOf(crawl.getUrlToNum().get(url)));
        String title = "";

        if(crawl.getUrlToNum().containsKey(url)) {
            Webpage webpage = (Webpage)crawl.readInURLObject(IndexDirectory, "Webpage.txt");
            if(webpage != null)
                title = webpage.getTitle();
        }

        return title;
    }
}
