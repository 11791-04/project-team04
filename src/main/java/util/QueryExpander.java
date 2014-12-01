package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import util.text.lm.Ngram;
import document.stemmer.KrovetzStemmer;
import document.stemmer.Stemmer;

/**
 * 
 * @author pyadapad
 *
 */
public class QueryExpander {

  /**
   * Expands the query by creating bigrams out of the input text List of stopwords are created.
   * 
   * @param question
   * @param stemmer
   * @return
   */
  public static String expandQuery(String question, Stemmer stemmer) {
    FileReader fileReader;
    String line = null;
    HashSet<String> stopWords = new HashSet<String>();
    try {
      fileReader = new FileReader("src/main/resources/stoppers");
      BufferedReader bf = new BufferedReader(fileReader);
      while ((line = bf.readLine()) != null) {
        stopWords.add(line);
      }
      bf.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return expandQuery(question, stopWords, stemmer);
  }

  /**
   * Expands the query by creating bigrams out of the input text
   * 
   * @param question
   * @param stopwords
   * @param stemmer
   * @return
   */
  public static String expandQuery(String question, Set<String> stopwords, Stemmer stemmer) {
    String newQuery = stemmer.stem(question);
    String finalQuery = "";
    String biGramQuery = "";
    for (String s : newQuery.split("\\s")) {
      if (!stopwords.contains(s)) {
        finalQuery += s + " ";
      }
    }
    String[] temp = finalQuery.trim().split("\\s");
    for (int i = 0; i < temp.length - 1; i++) {
      if (Ngram.getUnigram(temp[i]) <= Ngram.QECutoff && Ngram.getUnigram(temp[i + 1]) <= Ngram.QECutoff) {
        biGramQuery += temp[i] + " AND " + temp[i + 1];
        if (i != temp.length - 2)
          biGramQuery += " ";
      }
    }
    return biGramQuery.trim();
  }

  public static void main(String[] args) {
    Stemmer stemmer = new KrovetzStemmer();
    HashSet<String> stopwords = new HashSet<String>();
    stopwords.add("are");
    stopwords.add("of");
    stopwords.add("for");
    System.out.println(expandQuery(
            "Which acetylcholinesterase inhibitors are used for treatment of myasthenia gravis?",
            stemmer));
  }

}
