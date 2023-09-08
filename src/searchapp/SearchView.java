/****************************************************************************************************
 * File name: SearchView.java
 * Author: Kevin Lai, 101288231
 * Course: COMP 1406Z
 * Assignment: Course Project
 * Date: 2022 November 30
 * Professor: Dave McKenney
 * Purpose: Class that is used to create the UI and display data to the user. this is the
 *          view part to our MVC application.
 ****************************************************************************************************/
package searchapp;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchView {

    private Pane root;
    private TextField searchField;
    private Label logo;
    private Text text;
    private Text spinnerText;
    private Button searchButton;
    private CheckBox boostRank;
    private TextArea topTen;

    private Spinner<Integer> spinner;
    private List<SearchResult> topPages;
    /**
     * Default Constructor for SearchView , instantiates an ArrayList to store the top pages to display.
     */
    public SearchView(){
        configureUI();
        topPages = new ArrayList<>();
    }
    /**
     * Method to return root pane.
     * @return root - the root pane
     */
    public Parent asParent(){
        return root;
    }
    /**
     * Method to configure UI to display data to user
     */
    private void configureUI(){
        root = new Pane();
        root.setStyle("-fx-background-color: #1e81b0; " + "-fx-border-color: gray; " + "-fx-padding: 4 4;");

        //Search Bar
        searchField = new TextField();
        searchField.relocate(100,100);
        searchField.setPrefSize(400,40);

        //Logo
        Image image = new Image(getClass().getResourceAsStream("icons8-spiderweb-100.png"));
        ImageView imageView = new ImageView(image);

        logo = new Label("",imageView);
        logo.setStyle("-fx-font: 22 arial; -fx-base: rgb(170,0,0); -fx-text-fill: rgb(255,255,255);");
        logo.relocate(15, 15);
        logo.setPrefSize(80, 30);

        //Title
        text = new Text("Kargle");
        text.setStroke(Color.BLACK);
        text.setStrokeWidth(2);
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 38));
        text.setFill(Color.WHITE);
        text.relocate(225, 30);

        //Spinner Text
        spinnerText = new Text("Results Size: ");
        spinnerText.setFont(Font.font("Verdana",11));
        spinnerText.setFill(Color.WHITE);
        spinnerText.relocate(220, 159);

        //Search Button
        searchButton = new Button("Search");
        searchButton.relocate(383,153);
        searchButton.setPrefSize(100,25);

        //PageRank CheckBox
        boostRank = new CheckBox("PageRank\t:");
        boostRank.relocate(120,154);
        boostRank.setPrefSize(100,25);
        boostRank.setStyle("-fx-font: 11 Verdana; -fx-base: rgb(255,255,255); -fx-text-fill:rgb(255,255,255);");

        //Top 10 Results TextArea
        topTen = new TextArea();
        topTen.relocate(20,195);
        topTen.setPrefSize(560,285);
        topTen.setEditable(false);

        //Spinner
        spinner = new Spinner<>(0,Integer.MAX_VALUE,10);
        spinner.relocate(295,153);
        spinner.setPrefSize(75,25);
        spinner.setStyle("-fx-background-color: #1e81b0");
        spinner.setEditable(true);
        spinner.editorProperty().get().setAlignment(Pos.CENTER);

        root.getChildren().addAll(searchField,logo,text,searchButton,boostRank,topTen,spinner,spinnerText);

    }
    /**
     * Method to get searchButton attribute
     * @return searchButton - the search button
     */
    public Button getSearchButton() {
        return searchButton;
    }
    /**
     * Method to get boostRank attribute
     * @return boostRank - checkbox to see if page rank is used
     */
    public CheckBox getBoostRank() {
        return boostRank;
    }
    /**
     * Method to get topTen attribute
     * @return topTen - TextArea display the Top X results
     */
    public TextArea getTopTen() {
        return topTen;
    }
    /**
     * Method to get topPages attribute
     * @return topTen - List of top pages.
     */
    public List<SearchResult> getTopPages() {
        return topPages;
    }
    /**
     * Method to set topPages attribute
     * @return topTen - List of top pages.
     */
    public void setTopPages(List<SearchResult> topPages) {
        this.topPages = topPages;
    }
    /**
     * Method to get searchField attribute
     * @return searchField - the search query
     */
    public TextField getSearchField() {
        return searchField;
    }

    /**
     * Method to get spinner attribute
     * @return spinner - the spinner
     */
    public Spinner<Integer> getSpinner() {
        return spinner;
    }
}
