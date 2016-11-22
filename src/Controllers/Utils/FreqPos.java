package Controllers.Utils;

/**
 * Created by Alice on 07.11.2016.
 */
import java.util.LinkedList;
import java.util.List;

public class FreqPos
{
    //private int num;
    //private int position;
    //static Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
    public List<Integer> postings_list;
    List<List<Integer>> positionsLists;
    int count = 0;
    public FreqPos(int num, int pos)
    {
        //if (postings_list == null)
        //{
        postings_list = new LinkedList<>();
        positionsLists = new LinkedList<List<Integer>>();
        //}
        //if(num!=postings_list.get(count))
        //{
        //List<Integer> positions = new LinkedList<>();
        //postings_list.add(num);
        //count++;
        //positions.add(pos);
        //positionsLists.add(count, positions);
        //}
        //else
        //{
        List<Integer> positions = new LinkedList<>();
        positions.add(pos);
        positionsLists.add(count, positions);
        postings_list.add(num);
        //}
    }
    public void add(int num, int pos)
    {
        if (postings_list == null)
        {
            postings_list = new LinkedList<>();
        }
        if(postings_list.get(count)!=null)
        {
            if(num!=postings_list.get(count))
            {
                List<Integer> positions = new LinkedList<>();
                postings_list.add(num);
                count++;
                positions.add(pos);
                positionsLists.add(count, positions);
            }
            else
            {
                List<Integer> positions = positionsLists.get(count);
                positions.add(pos);
                positionsLists.add(count, positions);
            }
        }
    }

    public void reset_postingsList(List<Integer> list)
    {
        postings_list = list;
    }
 /*public FreqPos(int freq, int pos)
 {
  if(map.get(freq)!=null)
  {
   List<Integer> ex = map.get(freq);
            ex.add(pos);
  }
  else
  {
   List<Integer> positions = new LinkedList<>();
   positions.
   positions.add(pos);
   map.put(freq, positions);
  }
 }*/
 /*public int get()
 {
  return num;
 }
 public void set(int n)
 {
  num = n;
 }
   */
}