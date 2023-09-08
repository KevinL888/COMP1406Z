/****************************************************************************************************
 * File name: Webpage.java
 * Author: Kevin Lai, 101288231
 * Course: COMP 1406Z
 * Assignment: Course Project
 * Date: 2022 November 30
 * Professor: Dave McKenney
 * Purpose: Class used to simulate a webpage.
 ****************************************************************************************************/
package searchapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Webpage implements Serializable, SearchResult {
    private String title;
    private List<String> outgoingLinks;
    private List<String> incomingLinks;

    private double score;

    /**
     * Default Constructor for Webpage , instantiates outgoingLinks,incomingLinks ArrayList, tf and tf_idf HashMaps.
     * Assigns default value for page rank
     */
    public Webpage() {
        title = "";
        outgoingLinks = new ArrayList<>();
        incomingLinks = new ArrayList<>();
    }
    /**
     * Method to get webpage title
     * @return title - the webpage title
     */
    public String getTitle(){
        return title;
    }
    /**
     * Method to set score attribute
     * @param score: given page rank score to set
     */
    public void setScore(double score) {
        this.score = score;
    }
    /**
     * Method to get score attribute
     * @return score: The page rank score of the webpage
     */
    @Override
    public double getScore() {
        return score;
    }

    /**
     * Method to set webpage title
     * @return title - the webpage title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Method to get outgoing links for webpage
     * @return outgoingLinks - outgoing links for webpage
     */
    public List<String> getOutgoingLinks() {
        return outgoingLinks;
    }

}
