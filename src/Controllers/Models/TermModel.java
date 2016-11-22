package Controllers.Models;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by enspa on 13.11.2016.
 */
public class TermModel {
    private final StringProperty term;
    private final List<Integer> postings;

    public TermModel(String word, List<Integer> posList){
        this.term = new SimpleStringProperty(word);
        this.postings = posList;
    }

    public TermModel(){
        this(null,null);
    }

    public List<Integer> getPostings() {
        return postings;
    }

    public String getTerm() {
        return term.get();
    }

    public StringProperty termProperty() {
        return term;
    }


}
