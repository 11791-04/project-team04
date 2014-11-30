package util.text.ner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.uima.resource.ResourceInitializationException;

import util.text.lm.Ngram;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.util.AbstractExternalizable;

import abner.Tagger;

public class BioNER {

  private static class GeneMentionTag{
    public int begin;
    public int end;
    public String tagValue;

    public GeneMentionTag(int begin, int end, String tagValue) {
      super();
      this.begin = begin;
      this.end = end;
      this.tagValue = tagValue;
    }

  }


  private static PosTagNamedEntityRecognizer posTagger = null;
  static {
    try {
      posTagger =  new PosTagNamedEntityRecognizer();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("PosTagNamedEntityRecognizer failed to initialize!");
    }
  }


  private static Tagger abnerTager = new Tagger(Tagger.BIOCREATIVE);
  private static Chunker  chunker = null;
  static {
    try {
      chunker = (Chunker) AbstractExternalizable.readResourceObject(BioNER.class, "/models/ne-en-bio-genetag.HmmChunker");
    } catch (Exception e) {
      // e.printStackTrace();
      System.err.println("LingPipe failed to initialize!");
    }  
  }
  private static ExactDictionaryChunker dictionaryChunkerTF;
  static {
    MapDictionary<String> dictionary = new MapDictionary<String>();
    InputStream is = ExactDictionaryChunker.class.getResourceAsStream("/models/dictionary");
    String ln;

    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
      while ((ln = br.readLine()) != null) {
        //System.out.println(ln);
        dictionary.addEntry(new DictionaryEntry<String>(ln.trim(), "GENE",1.0));
      }
      br.close();
      is.close();
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    dictionaryChunkerTF = new ExactDictionaryChunker(dictionary,
            IndoEuropeanTokenizerFactory.INSTANCE,
            true,false);
  }


  public static Set<String> getBioTags(String content){
    Set<String> ret = new HashSet<String>();

    HashMap<String, Integer> tag_poll = new HashMap<String, Integer>();

    Set<GeneMentionTag> abnerTags = getAbnerNER(content);
    Set<GeneMentionTag> lingPipeStatTags = getLingPipeStatNER(content);
    Set<GeneMentionTag> lingPipeDictTags = getLingPipeDictNER(content);
    Set<GeneMentionTag> POSTags = getPOSNER(content);

    for(GeneMentionTag tag: abnerTags) {
      ret.add(tag.tagValue);
    }
    for(GeneMentionTag tag: POSTags) {
      ret.add(tag.tagValue);
    }

    return ret;
  }

  public static Set<GeneMentionTag> getAbnerNER(String content){

    Set<GeneMentionTag> ret = new HashSet<GeneMentionTag>();

    String tagRes = abnerTager.tagABNER(content);

    // try to find out the begin and end index from the ABNER tag result format
    String[] token_type_Array = tagRes.trim().split("[ \n]+");
    //System.out.println(Tools.join(token_type_Array));


    int state = 0;
    ArrayList<String> tokenBuf = new ArrayList<String>();
    ArrayList<ArrayList<String>> NER_List = new ArrayList<ArrayList<String>>();
    for(int i=0; i<token_type_Array.length; i++) {
      String token = token_type_Array[i].split("\\|")[0];
      String type = token_type_Array[i].split("\\|")[1];

      //System.out.println(token+"~"+type);

      if(type.startsWith("B") && state==0) {

        state = 1;
        tokenBuf.add(token);
      }else if(type.startsWith("I") && state==1) {
        tokenBuf.add(token);
      }else if(type.startsWith("O") && state==1){
        state = 0;
        if(tokenBuf.size()>0) {
          NER_List.add(tokenBuf);
        }
        tokenBuf = new ArrayList<String>();
      }else if(type.startsWith("B") && state==1) {
        NER_List.add(tokenBuf);
        tokenBuf = new ArrayList<String>();
        tokenBuf.add(token);
      }
    }
    if(tokenBuf.size()>0) { // In case if it ends with BII or B
      NER_List.add(tokenBuf);
    }

    //    for(ArrayList<String> ner: NER_List) {
    //      System.out.println("=====: "+Tools.join(ner.toArray(new String[ner.size()])));
    //    }
    //System.out.println(content);

    int indexLowerBound = 0;
    for(ArrayList<String> ner: NER_List) {

      int startIndex = content.indexOf(ner.get(0), indexLowerBound);
      if(startIndex == -1) {
        System.err.println("Can't find startIndex of: "+ner.get(0)+", in: "+content);
        continue;
      }
      indexLowerBound = startIndex;
      int endIndex = content.indexOf(ner.get(ner.size()-1), indexLowerBound);
      if(endIndex == -1) {
        System.err.println("Can't find endIndex of: "+ner.get(0)+", in: "+content);
        continue;
      }
      endIndex += ner.get(ner.size()-1).length();
      indexLowerBound = startIndex+1;
      //System.out.println(startIndex+"-"+endIndex);

      String tagValue = content.substring(startIndex, endIndex);


      GeneMentionTag tag = new GeneMentionTag(startIndex, endIndex, tagValue);

      ret.add(tag);
    }

    return ret;
  }

  public static Set<GeneMentionTag> getLingPipeStatNER(String content){
    Set<GeneMentionTag> ret = new HashSet<GeneMentionTag>();
    Chunking chunking = chunker.chunk(content);

    for(Chunk e: chunking.chunkSet()) {

      String tagValue = content.substring(e.start(), e.end());
      if(tagValue.matches("[A-Za-z]")) {continue;}

      GeneMentionTag tag = new GeneMentionTag(e.start(), e.end(), tagValue);
      ret.add(tag);
    }
    return ret;
  }

  public static Set<GeneMentionTag> getLingPipeDictNER(String content){
    Chunking chunking = dictionaryChunkerTF.chunk(content);
    Set<GeneMentionTag> ret = new HashSet<GeneMentionTag>();

    for(Chunk e: chunking.chunkSet()) {

      String tagValue = content.substring(e.start(), e.end());
      if(tagValue.matches("[A-Za-z]")) {continue;}

      GeneMentionTag tag = new GeneMentionTag(e.start(), e.end(), tagValue);
      ret.add(tag);
    }
    return ret;
  }


  public static Set<GeneMentionTag> getPOSNER(String content){
    Set<GeneMentionTag> ret = new HashSet<GeneMentionTag>();

    Map<Integer, Integer> tagLocations = posTagger.getGeneSpans(content);

    for(Entry<Integer, Integer> e: tagLocations.entrySet()) {

      String tagValue = content.substring(e.getKey(), e.getValue());
      if(tagValue.matches("[A-Za-z]")) {continue;}

      GeneMentionTag tag = new GeneMentionTag(e.getKey(), e.getValue(), tagValue);
      ret.add(tag);
    }
    return ret;
  }




  /**
   * Test client
   */
  public static void main(String[] args) {

    Set<String> tags = getBioTags("Tumors of which three people are classically associated with the multiple endocrine neoplasia type 1 syndrome?");
    for(String tag: tags) {
      System.out.println(tag+" "+Ngram.getUnigram(tag));
      
    }

  }
}
