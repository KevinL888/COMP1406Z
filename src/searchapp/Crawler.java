/****************************************************************************************************
 * File name: Crawler.java
 * Author: Kevin Lai, 101288231
 * Course: COMP 1406Z
 * Assignment: Course Project
 * Date: 2022 November 30
 * Professor: Dave McKenney
 * Purpose: Class that is used to represent a web crawler
 ****************************************************************************************************/
package searchapp;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Crawler implements Serializable{
    //maps urls to an index
    private Map<String,Integer> urlToNum;
    /**
     * Default Constructor for Crawler , instantiates HashMap.
     */
    public Crawler(){
        urlToNum = new HashMap<>();
    }
    /**
     * Method is the core function and starting point in the application. The seed URL is passed in and a queue is created
     * to start the crawl process. Each webpage is only parsed once. The seed URL is the starting point and all outgoing URL
     * that have not been crawled yet are added to the queue. The crawl process is done when all webpages have been crawled and
     * the data have been stored
     * @param seed - the seed url to start the crawl process
     * @return numPages - number of pages that have been crawled
     */
    public int crawl(String seed) throws IOException, ClassNotFoundException {
        int numPages = 0;

        LinkedList<String> queue = new LinkedList<>();
        String page = "";
        ArrayList<String> fruitList = new ArrayList<>();
        queue.add(seed);
        String nextURL = "";
        Map<String,Integer> idfDict = new HashMap<>();
        Webpage webpage;

        Path newDirectory = Paths.get(System.getProperty("user.dir"));
        Path crawlPath = Paths.get(newDirectory.toString(),"crawler_data");
        crawlPath.toFile().mkdirs();

        while(queue.size() != 0){
            Path filePath = Paths.get(crawlPath.toString(),Integer.toString(numPages));
            //creates a directory within the crawl data and the name is indexes starting from 0
            filePath.toFile().mkdirs();
            //#grabs the next url from the queue to be parsed
            nextURL = queue.pop();
            urlToNum.put(nextURL,numPages);
            //maps the current url to an index
            webpage = new Webpage();

            //coverts a webpage into a string of html
            page = WebRequester.readURL(nextURL);

            //parse the string of html
            parsePage(page, webpage,fruitList);

            calculateTf(filePath,fruitList,idfDict);

            ListIterator<String> iterator = webpage.getOutgoingLinks().listIterator();

            while(iterator.hasNext()){
                String stringUrl = iterator.next();

                if(stringUrl.charAt(0) == '.') {
                    nextURL = nextURL.substring(0,nextURL.lastIndexOf("/"));
                    nextURL += stringUrl.substring(1);
                    iterator.set(nextURL);
                }
                if(!urlToNum.containsKey(nextURL)){
                    queue.add(nextURL);
                    urlToNum.put(nextURL,0);
                }
                addToFile(filePath,"outgoing_links.txt",nextURL);
            }
            writeOutURLObject(filePath,"Webpage.txt",webpage);
            fruitList.clear();
            numPages +=1;
        }
        saveIncomingLinks(crawlPath);
        calculateIDF(crawlPath,idfDict);
        calculateTFIDF(crawlPath);
        calculatePageRanks(crawlPath);
        writeOutURLObject(crawlPath,"Crawler",this);

        return numPages;
    }
    /**
     * Method is used to create new files based on the given file path and file name. if the file already
     * exists then the content is just appended to the file.
     * @param filePath - file path to add new file to
     * @param fileName - name of new file
     * @param content - contents to be appended to file
     */
    public void addToFile(Path filePath, String fileName, String content) throws IOException {
        Path newFilePath = Paths.get(filePath.toString(), fileName);
        FileWriter outFile = new FileWriter(newFilePath.toFile(),true);
        outFile.append(content+"\n");
        outFile.close();
    }
    /**
     * Method is used to read data from a given file
     * @param file - file path to retrieve data from
     * @return value - a floating point value
     */
    public double readDataFromFile(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(file);
        double value = reader.nextDouble();
        reader.close();
        return value;
    }
    /**
     * Method to write object out to a file
     * @param filePath - file path to add new file to
     * @param fileName - name of new file
     * @param obj - object to been written to file
     */
    public void writeOutURLObject (Path filePath, String fileName, Object obj) throws IOException {
        Path newFilePath = Paths.get(filePath.toString(), fileName);
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(newFilePath.toFile()));
        os.writeObject(obj);
        os.close();
    }
    /**
     * Method to read in object from a file
     * @param filePath - file path to add new file to
     * @param fileName - name of new file
     * @return obj - Object
     */
    public static Object readInURLObject(Path filePath, String fileName) throws IOException, ClassNotFoundException {
        Path newFilePath = Paths.get(filePath.toString(), fileName);
        if(newFilePath.toFile().exists()) {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(newFilePath.toFile()));
            Object obj = is.readObject();
            is.close();
            return obj;
        }
        return null;

    }
    /**
     * Method parses the string representation of the webpage that was generated by the readURL function from the WebRequester class
     * @param page - String representation of a webpage
     * @param webpage - URL object
     * @param fruitList - Array to hold the paragraph section of the webpage each word is saved as an element in the Array
     */
    public void parsePage(String page, Webpage webpage,ArrayList<String> fruitList) {
        int titleIndex = 0;
        int paragraphIndex = 0;
        int hrefIndex = 0;
        //keep track of index of last parsed tag(this helps to not start from the starting of the string each time)
        // This is in case we have multiple title, paragraph, href tags.
        int currentTitleIndex = 0;
        int currentParagraphIndex = 0;
        int currentHrefIndex = 0;

        while(true){
            titleIndex = page.indexOf("<title",currentTitleIndex);
            paragraphIndex = page.indexOf("<p",currentParagraphIndex);
            hrefIndex = page.indexOf("<a",currentHrefIndex);

            if(titleIndex  != -1) {
                int startIndex = page.indexOf(">",titleIndex);
                int endIndex = page.indexOf("</title>",startIndex);
                webpage.setTitle(page.substring(startIndex+1,endIndex));
                currentTitleIndex = endIndex+"</title>".length();
            }
            if(paragraphIndex != -1) {
                int startIndex = page.indexOf(">",paragraphIndex);
                int endIndex = page.indexOf("</p>",startIndex);
                String[] fruitArray = page.substring(startIndex+1,endIndex).split("[\\n\\s]");

                for(String word : fruitArray) {
                    if(word != ""){
                        fruitList.add(word);
                    }
                }
                currentParagraphIndex = endIndex+"</p>".length();
            }
            if(hrefIndex != -1) {
                int startIndex = page.indexOf('"',hrefIndex);
                int endIndex = page.indexOf('"',startIndex+1);
                webpage.getOutgoingLinks().add(page.substring(startIndex+1,endIndex));
                currentHrefIndex = endIndex+"</a>".length();
            }
            else
                break;
        }
    }
    /**
     * Method loops through all the outgoing links for each of the webpages and assigns the current webpage as an incoming link
     * @param filePath - file path to add URL object to
     */
    public void saveIncomingLinks(Path filePath) throws IOException {
        //loop through all the webpages
        for(Map.Entry<String,Integer> entry: urlToNum.entrySet()) {
            Path linksPath = Paths.get(filePath.toString(), entry.getValue().toString(),"outgoing_links.txt");

            Scanner reader = new Scanner(linksPath.toFile());
            //check all outgoing links for that url
           while(reader.hasNextLine()) {

               Path outgoingLinkPath = Paths.get(filePath.toString(), urlToNum.get(reader.nextLine()).toString());

               //add the current url as an incoming link
               addToFile(outgoingLinkPath,"incoming_links.txt",entry.getKey());
           }

        }
    }
    /**
     * Method calculates the term frequency for each webpage by storing the count of each unique word in a text file
     * @param filePath - current webpage directory
     * @param fruitList - list of unique words on the current webpage
     * @param idfDict - dictionary that holds the counts for the unique words across all URLs
     */
    public void calculateTf(Path filePath, ArrayList<String> fruitList, Map<String,Integer> idfDict) throws IOException {
        Map<String,Integer> fruitDict = new HashMap<>();
        Path tfDirectory = Paths.get(filePath.toString(), "tf");
        tfDirectory.toFile().mkdirs();

        for (String word : fruitList) {
            if(fruitDict.containsKey(word)) {
                fruitDict.put(word,fruitDict.get(word).intValue()+1);
            }
            else{
                fruitDict.put(word,1);
                //calculate idf at the same time as tf by only adding the unique words from the paragraph for that url
                if(idfDict.containsKey(word)) {
                    idfDict.put(word,idfDict.get(word).intValue()+1);
                }
                else{
                    idfDict.put(word,1);
                }
            }
        }
        for(Map.Entry<String,Integer> entry:fruitDict.entrySet()) {
            double tf = (double)entry.getValue()/(double)fruitList.size();
            addToFile(tfDirectory, entry.getKey()+".txt",String.valueOf(tf));
        }

    }
    /**
     * Method calculates the inverse document frequency of each unique word contained in all the webpages (URLs) then stores
     * the data as text files for each unique word in the idf directory which is in the crawler_data directory.
     * @param filePath - current webpage directory
     * @param idfDict - dictionary that holds the counts for the unique words across all URLs
     */
    public void calculateIDF(Path filePath,Map<String,Integer> idfDict) throws IOException {
        Path idfDirectory = Paths.get(filePath.toString(), "idf");
        idfDirectory.toFile().mkdirs();

        //calculate the idf for each of the unique words and store in text files under the idf directory which is in crawler_data directory
        for(Map.Entry<String,Integer> entry:idfDict.entrySet()) {
            double idf = Math.log((double)urlToNum.size()/(entry.getValue()+1))/Math.log(2.0);

            addToFile(idfDirectory, entry.getKey()+".txt",String.valueOf(idf));
        }
    }
    /**
     * Method calculates the tf-idf weight of each unique word in all the webpages (URLs) and stores this data in a directory
     * named tf_idf in each of the webpages directories
     * @param filePath - starting file path "crawler_data"
     */
    public void calculateTFIDF(Path filePath) throws IOException {
        Path idfDirectory = Paths.get(filePath.toString(), "idf");

        //loop through all the webpages
        for(Integer entry: urlToNum.values()){
            Path indexPath = Paths.get(filePath.toString(), entry.toString());
            Path tfIDFDirectory = Paths.get(indexPath.toString(), "tf_idf");
            tfIDFDirectory.toFile().mkdirs();

            Path tfDirectory = Paths.get(filePath.toString(),entry.toString(), "tf");

            //loop through all tf words for each webpage
            for(File file : tfDirectory.toFile().listFiles()) {
                Path idfFile = Paths.get(idfDirectory.toString(), file.getName());
                double idf = readDataFromFile(idfFile.toFile());
                double tf = readDataFromFile(file);

                //calculate tf_idf for current word
                double tfIDF = (Math.log(1+(tf))/Math.log(2)) *idf;
                addToFile(tfIDFDirectory,file.getName(),String.valueOf(tfIDF));
            }
        }
    }
    /**
     * Method calculates the page rank of every webpage (URLs) following the algorithm for computing page rank from
     * the lecture videos. The alpha value used is 0.1
     * @param filePath - starting file path "crawler_data"
     */
    public void calculatePageRanks(Path filePath) throws IOException {
        ArrayList<ArrayList<Double>> adjacencyMatrix = new ArrayList<>();
        ArrayList<ArrayList<Double>> t = new ArrayList<>();
        ArrayList<ArrayList<Double>> oldT;
        int numOfOnes = 0;
        double alpha = 0.1;
        t.add(new ArrayList<>(Collections.nCopies(urlToNum.size(), (double)1/urlToNum.size())));

        for (int i = 0; i<urlToNum.size();i++) {
            adjacencyMatrix.add(new ArrayList<>(Collections.nCopies(urlToNum.size(), 0.0)));
            Path linksPath = Paths.get(filePath.toString(), String.valueOf(i), "outgoing_links.txt");

            //check outgoing links, for each one find the index corresponding to it and assign 1
            Scanner reader = new Scanner(linksPath.toFile());
            while(reader.hasNextLine()) {
                String link = reader.nextLine();
                adjacencyMatrix.get(i).set(urlToNum.get(link),1.0);
                numOfOnes+=1;
            }
            reader.close();
            for (int j = 0; j <adjacencyMatrix.get(i).size(); j++) {
                //Initial transition probability matrix (dividing rows by # of 1s):
                if(numOfOnes == 0){
                    adjacencyMatrix.get(i).set(j,0.0);
                }
                else{
                    adjacencyMatrix.get(i).set(j,adjacencyMatrix.get(i).get(j)/numOfOnes);
                }
                //Scaled Adjacency Matrix (1-alpha * adjacency)
                adjacencyMatrix.get(i).set(j,adjacencyMatrix.get(i).get(j) * (1-alpha));
                //Adding alpha/N
                adjacencyMatrix.get(i).set(j,adjacencyMatrix.get(i).get(j) + (alpha/adjacencyMatrix.get(i).size()));
            }
            numOfOnes = 0;
        }

        //power iteration continues until the euclidean distance between the current vector and previous is less than 0.0001
        while(true){
            oldT =(ArrayList<ArrayList<Double>>) t.clone();
            t = MatMult.multMatrix(t,adjacencyMatrix);

            if(t != null){
                if(MatMult.euclideanDist(t,oldT) <= 0.0001){
                    break;
                }
            }
        }
        for(Integer url :urlToNum.values()){
            Path indexPath = Paths.get(filePath.toString(), url.toString());
            addToFile(indexPath, "page_rank.txt",String.valueOf(t.get(0).get(url)));
        }
    }
    /**
     * Method to get urlToNum attribute
     * @return urlToNum: HashMap that contains url as the key and index as a value
     */
    public Map<String, Integer> getUrlToNum() {
        return urlToNum;
    }
}
