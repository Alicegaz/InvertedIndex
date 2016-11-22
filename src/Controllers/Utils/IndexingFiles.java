package Controllers.Utils;

/**
 * Created by Alice on 07.11.2016.
 */

import Controllers.Views.DictionaryController.DictionaryViewController;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import javafx.fxml.FXML;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;



import static java.lang.Math.log;

//import static com.oracle.jrockit.jfr.ContentType.Bytes;

/**
 * Created by enspa on 01.11.2016.
 */
public class IndexingFiles {


    //    IndexingFiles indexingFiles = new IndexingFiles();
    List<String> docs_paths = new LinkedList<>();
    String stopWords = Pattern.quote("\\p{Punct} , _ ");
    String needSymbols = Pattern.quote("[a-z]', [a-z]'[a-z], [a-z]-[a-z]");
    String needSymbols2 = Pattern.quote("[a-z0-9]-[a-z0-9], [a-z]'[a-z], [a-z0-9]");
    static Map<String, FreqPos> index = new HashMap<>();





    @FXML
    DictionaryViewController dvc = new DictionaryViewController();

    public IndexingFiles() throws IOException, InterruptedException {
    }

    public Map<String, FreqPos> getIndex() {
        return index;
    }

    public Map<String, FreqPos> getAlreadyExistIndex(Map<String, FreqPos> lel){
        return index = lel;
    }

    public static void setIndex(Map<String, FreqPos> index) {
        IndexingFiles.index = index;
    }

    /**
     * Indexing files
     * @param folder
     * @return
     */
    public List<String> listFilesForFolder(final String folder) {
        long startTime = System.currentTimeMillis();
        //reading all files
        try (Stream<Path> pathStream = Files.walk(Paths.get(String.valueOf(folder)))) {
            pathStream.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    docs_paths.add(String.valueOf(filePath));
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        long stopTime = System.currentTimeMillis();
        System.out.println("Time required: " + (stopTime - startTime) + "ms");

        return docs_paths;
    }

    /**
     * create dictionary
     * @throws IOException
     */
    public void ParseFile() throws IOException {
        PrintWriter out = new PrintWriter("src\\Dictionary");
        File file = new File("src\\dictionary");
        for (int i = 0; i < docs_paths.size(); i++) {
            int num = i;
            int pos = 0;

            BufferedReader reader = new BufferedReader(new FileReader(docs_paths.get(i)));
//            FileWriter fw = new FileWriter("src\\Dictionary");
//           out = new PrintWriter("src\\Dictionary");
            for (String ln = reader.readLine(); ln != null; ln = reader.readLine()) {
                //for (String _word : ln.split("\\W+")) {
                String ln2 = ln.replaceAll("[-|'|`]","");
                ln = ln2.replaceAll("[0-9]","");
                  for (String _word: ln.split("\\W+")){
                    String word = _word.toLowerCase();
                    pos++;
                      word = word.replaceAll("[\\p{Punct}]", "");
                      if ((word.compareTo("")!=0)) {
                        FreqPos idx = index.get(word);

                        out.write(word + " -> " + docs_paths.get(i) + "\n");
                        if (idx == null) {
                            idx = new FreqPos(num, pos);
                            index.put(word, idx);
                        } else {
                            idx.add(num, pos);
                         }
                        //TODO should i do index.put(word, idx)
                    }


                  }
            }
            System.out.println("adding " + docs_paths.get(i) + " " + pos + " words");
        }
        out.close();

    }

    /**
     * Sorting dictionary, creating dictionary with term -> postinglist
     * @throws IOException
     */
    public void SortDictionary() throws IOException{

        //for working with file
        BufferedReader reader1 = new BufferedReader(new FileReader("src\\dictionary"));
        BufferedReader reader2 = new BufferedReader(new FileReader("src\\dictionary"));
        PrintWriter out1 = new PrintWriter("src\\SortedDictionary");
        for (Map.Entry<String, FreqPos> entry : index.entrySet())
        {
            String word1 = entry.getKey();
            List<Integer> list = index.get(word1).postings_list;
            out1.write(word1 + " -> ");
            for (int i = 0; i<index.get(word1).postings_list.size(); i++)
            {
                out1.write(docs_paths.get(list.get(i)) + " | ");
            }
            out1.write("\n");
        }
        out1.write("\n");
        out1.flush();
        //postings.clear();
    }


    /**
     * First stage of encoding
     * @throws InterruptedException
     */
    public void gapEncode() throws InterruptedException
    {


        for (Map.Entry<String, FreqPos> entry : index.entrySet())
        {
            String word1 = entry.getKey();
            FreqPos inputDocIdsOutputGaps = index.get(word1);

            if (inputDocIdsOutputGaps.postings_list != null )
            {

                if (inputDocIdsOutputGaps.postings_list.size()!= 1)
                {
                    int temp = inputDocIdsOutputGaps.postings_list.get(1);
                    inputDocIdsOutputGaps.postings_list.set(1, (inputDocIdsOutputGaps.postings_list.get(1) - inputDocIdsOutputGaps.postings_list.get(0)));

                    for (int i = 1; i<(inputDocIdsOutputGaps.postings_list.size() - 1); i++)
                    {
                        int c = inputDocIdsOutputGaps.postings_list.get(i+1);
                        int p = inputDocIdsOutputGaps.postings_list.get(i+1) - temp;
                        temp = c;
                        inputDocIdsOutputGaps.postings_list.set(i+1, p);

                    }
                    index.replace(word1, inputDocIdsOutputGaps);

                }

            }

        }


    }

    /**
     * Second stage of decoding
     * @param postings_list
     * @return
     */
    public List<Integer> gapDecode(List<Integer> postings_list)
    {
        if (postings_list!=null)
        {
            //FreqPos
            //inputGapsOutputDocIds = index.get(word1);

            List<Integer> numbers = new LinkedList<Integer>();
            numbers.add(postings_list.get(0));
            if(postings_list.size()!=1)
            {
                numbers.add(1, postings_list.get(1) + postings_list.get(0));
                for (int i = 1; i< postings_list.size() - 1; i++)
                {
                    numbers.add(i+1, postings_list.get(i+1) + numbers.get(i)) ;
                }

                    //index.replace(word1, inputGapsOutputDocIds);
            }
            return numbers;
        }
        else return null;
    }


    /**
     * Variable byte encoding integers value
     * @param gap
     * @return
     */
    public static byte[] VBEncodeInteger(int gap)
    {
        if (gap == 0)
        {
            return new byte[]{0};
        }
        int i = (int) (log(gap) / log(128)) + 1;
        byte[] postingsByte = new byte[i];
        int j = i -1;
        do
        {
            postingsByte[j--] = (byte) (gap % 128);
            gap /= 128;
        }
        while (j >= 0);
        postingsByte[i - 1] += 128;
        return postingsByte;
    }


    /**
     * VB encoding posting list
     */
    public static void VBEncode() throws Exception
    {


        byte[] encodedInt = new byte[Integer.SIZE / 7 + 1];

        for (Map.Entry<String, FreqPos> entry : index.entrySet())
        {
            //ByteArrayOutputStream VBOutputStream = new ByteArrayOutputStream();
            String word1 = entry.getKey();
            FreqPos list = index.get(word1);
            ByteBuffer bf = ByteBuffer.allocate(list.postings_list.size() * (Integer.SIZE/Byte.SIZE));


            if (list != null )
            {

                if (word1.compareTo("dog") == 0)
                {
                    System.out.println("Incoming list of gaps :");
                    for (int i = 0; i<list.postings_list.size(); i++)
                    {
                        System.out.print(list.postings_list.get(i)+ " ");
                    }
                }
                for (int i = 0; i<list.postings_list.size(); i++)
                {
                    int posting = list.postings_list.get(i);
                    bf.put(VBEncodeInteger(posting));

                }
                bf.flip();
                byte [] encodedArr = new byte[bf.limit()];
                bf.get(encodedArr);
                List<Byte> byteList = Bytes.asList(encodedArr);
                List<Integer> listOfIntegers = Ints.asList(Ints.toArray(byteList));
                list.reset_postingsList(listOfIntegers);
                index.replace(word1, list);
            }



        }
    }


    /**
     * first stage of decoding
     * decode posting list
     * @param word
     * @return
     */

    public static List<Integer> VBDecode(String word)
    {
        int n = 0;
        if (index.get(word)!=null)
        {
            FreqPos id = index.get(word);
            List<Byte> byteList = Bytes.asList(Bytes.toArray(id.postings_list));
            byte[] postingsByte = Bytes.toArray(byteList);
            List<Integer> numbers = new LinkedList<>();
            for (byte b: postingsByte)
            {
                if((b & 0xff) < 128)
                {
                    n = 128 * n + b;
                }
                else
                {
                    int num1 = (128 * n + ((b - 128) & 0xff));
                    numbers.add(num1);
                    n = 0;
                }
            }
            return numbers;
        }
        return null;
    }


    /**
     * Search term into dictionary
     * @param words
     * @throws InterruptedException
     */
    public List<String> search(List<String> words) throws InterruptedException
    {
        List<List<Integer>> fulltext = new LinkedList<List<Integer>>();
        int count = -1;
        List<Integer> listfinal = new LinkedList<>();
        List<String> resultList = new LinkedList<>();

        for(String _word : words)
        {
            count += 1;
            Set<String> ans = new HashSet<String>();
            String word = _word.toLowerCase();
            String word1 = word.replaceAll("[\\p{Punct}]", "");
            word = word1.replaceAll("[0-9]", "");
            if (index.get(word)!=null)
            {
                List<Integer> postings_list = VBDecode(word);
                List<Integer> list = gapDecode(postings_list);
                fulltext.add(list);
                int i = 0;
                if(postings_list!=null)
                {
                    for ( int t1 : list)
                    {
                        ans.add(docs_paths.get(t1));
                    }
                }
                else System.out.println("Nothing was found");

                if(stopWords.contains(word))
                    continue;
                else
                {
                    System.out.println(word);
                    if ((ans.toString() == null) || (ans.toString() == "") || (ans.toString() == " "))
                    {
                        System.out.println("Nothing was found");
                    }
                    else
                    {
                        for (String f : ans)
                        {
                            resultList.add(f);
                            System.out.print(" " + f);
                        }
                    }
                    System.out.println("");
                }
            }
            else
            {
                System.out.println(word);
                System.out.println("There is no such a word in the database books");
            }

            if (fulltext.size() == 1)
            {
                listfinal = fulltext.get(0);
            }
            if (fulltext.size()>1)
            {
                List<Integer> listtemp = new LinkedList<>();
                for ( int l: fulltext.get(count))
                {
                    if (listfinal.contains(l))
                        listtemp.add(l);
                }
                listfinal = listtemp;
            }


        }

        /**System.out.println("In every document ");
         for (int l: listfinal)
         {

         System.out.print(l + " - "+ docs_paths.get(l)+ "   ");
         }
         **/
        for (int i = 0; i < resultList.size(); i++)
            System.out.print(resultList.get(i) + " ");
        return resultList;

    }


}

