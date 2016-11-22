package Controllers;/**
 * Created by enspa on 13.11.2016.
 */

import Controllers.Models.TermModel;
import Controllers.Utils.FreqPos;
import Controllers.Utils.IndexingFiles;
import Controllers.Views.DictionaryController.DictionaryViewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    IndexingFiles indexingFiles;
    Map<String, FreqPos> index;
    /**
     * Data of dictionary
     */
    private ObservableList<TermModel> dictionary = FXCollections.observableArrayList();


    /**
     * Constructor
     */
    public MainApp() throws Exception {
        String docsPath ="test_data";
        List<String> docs_path = new LinkedList<>();
        IndexingFiles indexingFiles1 = new IndexingFiles();
        index = indexingFiles1.getIndex();
        docs_path = indexingFiles1.listFilesForFolder(docsPath);
        indexingFiles1.ParseFile();
        indexingFiles1.SortDictionary();
        indexingFiles1.gapEncode();
        indexingFiles1.VBEncode();
//        int size = indexingFiles1.getIndex().size();
        for(Map.Entry<String, FreqPos> entry: index.entrySet()){
            String word1 = entry.getKey();
            List<Integer> l = index.get(word1).postings_list;
            dictionary.add(new TermModel(word1, l));

        }

        indexingFiles = indexingFiles1;
//
    }


    public Map<String, FreqPos> getIndex() {
        return index;
    }




    public IndexingFiles getIndexingFiles() {
        return indexingFiles;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("IT project");


        //rootlayout
        InitRootLayout();

        //put dictionary into rootLayout
        InitDictionary();

    }


    /**
     Initialize rootlayout
     */
    private void InitRootLayout(){
        try{
            FXMLLoader RootLayoutLoader = new FXMLLoader();
            RootLayoutLoader.setLocation(this.getClass().getResource("Views/RootLayout.fxml"));
            rootLayout = RootLayoutLoader.load();

            Scene rootScene = new Scene(rootLayout);


            primaryStage.setScene(rootScene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize Dictionary.fxml into centre RootLayout
     */
    private void InitDictionary(){
        try{
            FXMLLoader DictionaryLoader = new FXMLLoader();
            DictionaryLoader.setLocation(this.getClass().getResource("Views/DictionaryController/Dictionary.fxml"));
            AnchorPane Dictionary = (AnchorPane)DictionaryLoader.load();

            rootLayout.setCenter(Dictionary);

            DictionaryViewController controller = DictionaryLoader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<TermModel> getDictionary(){
        return dictionary;
    }

}
