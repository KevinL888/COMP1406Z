/****************************************************************************************************
 * File name: SearchApplication.java
 * Author: Kevin Lai, 101288231
 * Course: COMP 1406Z
 * Assignment: Course Project
 * Date: 2022 November 30
 * Professor: Dave McKenney
 * Purpose: This class is the controller part of our MVC application it deals with the application
 *          logic and acts as the coordinator between the view and the model.
 ****************************************************************************************************/
package searchapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SearchApplication extends Application {
    private Crawler crawl;
    private Search model;
    private SearchView view;
    /**
     * Method is the main entry point for all JavaFX applications
     * @param stage - primary window of the JavaFx application
     */

    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {
        Path newDirectory = Paths.get(System.getProperty("user.dir"));
        Path crawlPath = Paths.get(newDirectory.toString(),"crawler_data");
        //read in Crawler object so can get list of urlToIndex- this will not start a new crawl
        crawl = (Crawler)Crawler.readInURLObject(crawlPath,"Crawler");

        if(crawl != null) {
            model = new Search(crawl);
        }

        view = new SearchView();
        stage.setTitle("Search Engine");
        Scene scene = new Scene(view.asParent(), 600,500, Color.rgb(30,129,176));
        String css = this.getClass().getResource("search.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setResizable(false);

        stage.show();
        view.getSearchButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                update(view.getSpinner().getValue());
            }
        });
    }

    public void update(int X) {
        if(model != null) {
            if(view.getBoostRank().isSelected()) {
                try {
                    view.setTopPages(model.search(view.getSearchField().getText(),true,X));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                try {
                    view.setTopPages(model.search(view.getSearchField().getText(),false,X));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            view.getTopTen().clear();
            for (SearchResult topText: view.getTopPages())
                view.getTopTen().appendText(topText.getTitle()+": ("+topText.getScore()+")\n");
        }
        else {
            view.getTopTen().clear();
            view.getTopTen().appendText("There is no crawled data to perform search on.");
        }
    }

    /**
     * Method to delete directories recursively before starting a new crawl process
     * @param filePath - starting file path "crawler_data"
     */
    public void deleteDirectory(File filePath) {
        if(filePath.isDirectory()) {
            for(File file: filePath.listFiles()) {
                if(file.isDirectory()) {
                    deleteDirectory(file);
                }
                file.delete();
            }
            filePath.delete();
        }
    }
    /**
     * Method to initialize all attributes. This method is invoked at the start of the application before the
     * crawl process. it ensures all data is deleted and any initialization  are made before the crawl.
     */
    public void initialize() {
        Path newDirectory = Paths.get(System.getProperty("user.dir"));
        Path crawlPath = Paths.get(newDirectory.toString(),"crawler_data");
        deleteDirectory(crawlPath.toFile());
        crawl = new Crawler();
        model = new Search(crawl);
    }
    /**
     * Method to get crawl attribute
     * @return crawl - Crawler object, our instance of Crawler
     */
    public Crawler getCrawl() {
        return crawl;
    }
    /**
     * Method to get model attribute
     * @return model - Search object, our instance of Search
     */
    public Search getModel() {
        return model;
    }
    /**
     * Method is the main entry point for the applications
     * @param args - arguments that are passed in from the terminal when executing application
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        launch();
    }
}