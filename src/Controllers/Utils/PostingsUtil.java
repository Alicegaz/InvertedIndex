package Controllers.Utils;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

import java.util.List;

/**
 * Created by enspa on 13.11.2016.
 */
public class PostingsUtil {

    private static List<Integer> postingList;
    private static ListProperty<Integer> propertylist;

    public List<Integer> Converter(ListProperty<Integer> listProperty){
        for (Integer aListProperty : listProperty) postingList.add(aListProperty);
        return postingList;
    }

    public ListProperty<Integer> deconverter(List<Integer> postingList){
        for (Integer aPostingList : postingList) propertylist.add(aPostingList);
        return propertylist;
    }

}
