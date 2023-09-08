/****************************************************************************************************
 * File name: MatMult.java
 * Author: Kevin Lai, 101288231
 * Course: COMP 1406Z
 * Assignment: Course Project
 * Date: 2022 November 30
 * Professor: Dave McKenney
 * Purpose: Class that is used for matrix multiplication and calculating Euclidean Distance
 *          between two vectors
 ****************************************************************************************************/
package searchapp;

import java.util.ArrayList;

public class MatMult {
    /**
     * Method that takes a given matrix and multiplies it by another given matrix and returns a new 2DArrayList containing the results
     * @param a - list representing 2D matrices
     * @param b - list representing 2D matrices
     * @return newList - new 2DArrayList with results of the given matrix multiplied by another given matrix
     */
    public static ArrayList<ArrayList<Double>> multMatrix(ArrayList<ArrayList<Double>> a, ArrayList<ArrayList<Double>> b) {
        ArrayList<ArrayList<Double>> newList = new ArrayList<>();
        if(a.get(0).size() == b.size()){
            for(int i = 0;i<a.size();i++) {
                newList.add(new ArrayList<>());
                for(int j = 0;j<b.get(0).size();j++){
                    newList.get(i).add(0.0);
                    for(int k = 0; k<b.size(); k++){
                        newList.get(i).set(j, newList.get(i).get(j)+(a.get(i).get(k)*b.get(k).get(j)));
                    }
                }
            }
        }
        else{
            return null;
        }
        return newList;
    }
    /**
     * Method that takes  two vectors and calculates the Euclidean distance between the two vectors
     * @param a - list representing a vector
     * @param b - list representing 2D matrices
     * @return Math.sqrt(total) - the Euclidean distance between the two vectors
     */
    public static double euclideanDist(ArrayList<ArrayList<Double>> a, ArrayList<ArrayList<Double>> b){
        if(a.isEmpty()||b.isEmpty()) {
            return 0.0;
        }
        if(a.size() != b.size()){
            return 0.0;
        }

        double total = 0.0;

        for(int i = 0; i<a.get(0).size();i++){
            total += Math.pow(a.get(0).get(i)-b.get(0).get(i),2);
        }
        return Math.sqrt(total);
    }
}
