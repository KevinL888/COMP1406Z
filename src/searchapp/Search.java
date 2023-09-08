/****************************************************************************************************
 * File name: Search.java
 * Author: Kevin Lai, 101288231
 * Course: COMP 1406Z
 * Assignment: Course Project
 * Date: 2022 November 30
 * Professor: Dave McKenney
 * Purpose: This class is responsible for answering search queries by utilizing the methods in
 * the SearchData class in order to rank the top X searches based on the query from highest to
 * lowest. I mainly worked on optimizing my search function to ensure that I minimized the time
 * complexity. All the data that are used to calculate the top X most relevant pages are stored
 * in a URL object from the crawl process and retrieved from the SearchData Class. This is the model
 * part of our MVC Application
 ****************************************************************************************************/
package searchapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Search {
    private Crawler crawl;
    private SearchData searchData;

    public Search(Crawler crawl) {
        this.crawl = crawl;
        this.searchData = new SearchData(crawl);
    }
    /**
     * Method is responsible for answering search queries by invoking methods to calculate the Cosine Similarity
     * in order to rank the top X searches based on the query from highest to lowest.
     * @param query - the search query
     * @param boost - if page rank should be used
     * @param X - number of top pages to display
     * @return topPages - list of top X webpages based off cosine similarity score
     */
    public List<SearchResult> search(String query, boolean boost, int X) throws IOException, ClassNotFoundException {
        List<SearchResult> listOfDocuments = new ArrayList<>();
        List<SearchResult> topPages = new ArrayList<>();
        List<String> phraseIntoList =  new ArrayList<>(Arrays.asList(query.split(" ")));
        Map<String,Double> queryDict = new HashMap<>();
        double leftDenominator = 0;

        leftDenominator = calculateLeftDenominator(phraseIntoList,queryDict,leftDenominator);
        calculateCosineSimilarity(queryDict,leftDenominator,boost,listOfDocuments);

        //sort the list of webpages by their cosine similarity score from ascending to descending order
        // if two page have the same score within 3 decimal places then compare using lexicographical order of their page titles

        Collections.sort(listOfDocuments, new DataDisplayComparator());

        for(int i = 0;i<X;i++){
            topPages.add(listOfDocuments.get(i));
        }

        return topPages;
    }
    /**
     * Method is responsible for calculating the left Denominator for our cosine similarity equation.
     * @param phraseIntoList - the search query words split into an array
     * @param queryDict - used to store unique words for the search query
     * @param leftDenominator - leftDenominator for our cosine similarity equation
     * @return leftDenominator - leftDenominator for our cosine similarity equation
     */
    public double calculateLeftDenominator(List<String> phraseIntoList, Map<String,Double> queryDict, double leftDenominator) {
        //create dictionary to store unique words for the search phrase
        for(String word :phraseIntoList) {
            if(queryDict.containsKey(word)){
                queryDict.put(word,queryDict.get(word) +1.0);
            }
            else{
                queryDict.put(word,1.0);
            }
        }
        //calculate query tf_idf for each word
        for (Map.Entry<String,Double> entry:queryDict.entrySet()){
            double idf;
            try {
                idf = searchData.getIDF(entry.getKey());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            double queryTFIDF = Math.log(1+ (entry.getValue()/phraseIntoList.size()))/Math.log(2) * idf;
            queryDict.put(entry.getKey(),queryTFIDF);
            leftDenominator+=(queryTFIDF*queryTFIDF);
        }
        return leftDenominator;
    }

    /**
     * Method is responsible for calculating cosine similarity equation used to rank our top X pages.
     * @param queryDict - used to store unique words for the search query
     * @param leftDenominator - leftDenominator for our cosine similarity equation
     * @param boost - if page rank should be used
     * @param listOfDocuments - array of page results with the format title - page score(cosine similarity score)
     */
    public void calculateCosineSimilarity(Map<String,Double> queryDict,double leftDenominator, boolean boost, List<SearchResult> listOfDocuments) throws IOException, ClassNotFoundException {
        //loop through all the webpages(URLs) directories to get the tf_idf of the words from the query
        double numerator = 0;
        double rightDenominator = 0;

        for (Map.Entry<String,Integer> url:crawl.getUrlToNum().entrySet()) {

            for(Map.Entry<String,Double> word:queryDict.entrySet()) {
                double tfIDF = searchData.getTFIDF(url.getKey(), word.getKey());
                numerator+= (word.getValue() * tfIDF);
                rightDenominator +=(tfIDF*tfIDF);
            }
            double denominator = Math.sqrt(leftDenominator)*Math.sqrt(rightDenominator);

            Webpage dataDisplay = new Webpage();
            dataDisplay.setTitle(searchData.getTitle(url.getKey()));

            if(denominator != 0) {
                if(boost){
                    dataDisplay.setScore(searchData.getPageRank(url.getKey())*(numerator/denominator));
                }
                else{
                    dataDisplay.setScore(numerator/denominator);
                }
            }
            else{
                dataDisplay.setScore(0.0);
            }
            listOfDocuments.add(dataDisplay);
            numerator = 0;
            rightDenominator = 0;
        }
    }
}
/**
 * Class implements Comparator and used to compare SearchResult Objects for our ordering of top 10 pages
 */
class DataDisplayComparator implements Comparator<SearchResult> {

    /**
     * Method is responsible for comparing search results by determining if search result object 1 should come
     * before search result object 2 and in the case where they are the same then compare using lexicographical
     * order by their page titles.
     * @param o1 - first search result object
     * @param o2 - second search result object
     * @return 1, -1 - based off if one object should come before the other.
     */
    @Override
    public int compare(SearchResult o1, SearchResult o2) {
        double score1 = BigDecimal.valueOf(o1.getScore()).setScale(3, RoundingMode.HALF_UP).doubleValue();
        double score2 = BigDecimal.valueOf(o2.getScore()).setScale(3, RoundingMode.HALF_UP).doubleValue();


        if(score1 == score2) {
            if(o1.getTitle().compareTo(o2.getTitle())>0){
                return 1;
            }
            else {
                return -1;
            }
        }

        if(score1 > score2){
            return -1;
        }
        else{
            return 1;
        }
    }
}