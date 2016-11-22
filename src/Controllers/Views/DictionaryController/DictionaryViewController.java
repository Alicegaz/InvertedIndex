package Controllers.Views.DictionaryController;

import Controllers.MainApp;
import Controllers.Models.TermModel;
import Controllers.Utils.FreqPos;
import Controllers.Utils.IndexingFiles;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by enspa on 13.11.2016.
 */
public class DictionaryViewController {

    private MainApp mainApp;
    /**
     * for dictionary and posting list for each words
     */

    @FXML
    private TableView<TermModel> dictionary;

    @FXML
    private TableColumn<TermModel, String> termTableColumn;

    @FXML
    private TextArea postingsTextArea;

    @FXML
    private Label termLabel;

    /**
     * For searching
     */

    @FXML
    private TextField searchingTerm;

    @FXML
    private TextArea forResult;


    private Boolean searchClicked = false;


    /**
     * for dictionary and posting list for each word
     */
    public DictionaryViewController() throws IOException, InterruptedException {    }

    @FXML
    private void initialize(){
        //initialization table column
        termTableColumn.setCellValueFactory(cellData -> cellData.getValue().termProperty());
        //clear extra data about dictionary
        showDictionaryDetails(null);

        dictionary.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> showDictionaryDetails(newValue));


    }

    private void showDictionaryDetails(TermModel model){
        if(model != null){
            termLabel.setText(model.getTerm());
            for(int i = 0; i < model.getPostings().size(); i++) {
                List<Integer> l = model.getPostings();
                String str = "";
                for (int j = 0; j < l.size(); j++)
                    str += (l.get(j) + " ");

                postingsTextArea.setText(str);
            }
        }else{
            termLabel.setText("");
            postingsTextArea.setText("");
        }

    }

    private boolean isSearchClicked(){
        return searchClicked;
    }

    @FXML
    private void searchHandle() throws IOException, InterruptedException {
        /**
         * ЗДЕСЬ БУДЕТ МЕТОД СЕРЧ
         */
        String str = searchingTerm.getText();
        String[] ar1 = str.split(" ");
        List<String> query = Arrays.asList(ar1);
        IndexingFiles indexingFiles = mainApp.getIndexingFiles();

//        mainApp.getIndex();
//        mainApp.getIndexingFiles().search(query);
//        indexingFiles.search(query);
        List<String> result = indexingFiles.search(query);
        System.out.print(result.size());
        if(result.size() == 0){
            forResult.setText("There is no such a word in our dictionary");
        }else{
            //String string = "";
            //for (int i = 0; i < result.size(); i++) {
              //  string += result.get(i);
            //}
            String string2 = String.join("\n", result);
            System.out.print(string2);
            forResult.setText(string2);
        }
        System.out.print(query);

    }

    public TextArea getForResult() {
        return forResult;
    }

    public void setMainApp(MainApp main){
        this.mainApp = main;

        dictionary.setItems(mainApp.getDictionary());

    }
}
