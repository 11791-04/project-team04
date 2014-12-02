package util.text.ner;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.uima.resource.ResourceInitializationException;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * A wrapper for StanfordCoreNLP logics, include: sentence splitter, tokenization and POS tagging.
 * @author josephcc
 *
 */
public class PosTagNamedEntityRecognizer {

  private StanfordCoreNLP pipeline;

  /**
   * The initializaer initializes the Stanford NLP pipeline.
   * @throws ResourceInitializationException
   */
  public PosTagNamedEntityRecognizer() throws ResourceInitializationException {
    // this is your print stream, store the reference
    PrintStream err = System.err;

    // now make all writes to the System.err stream silent 
    System.setErr(new PrintStream(new OutputStream() {
      public void write(int b) {
      }
    }));

    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos");
    pipeline = new StanfordCoreNLP(props);
    // set everything back to its original state afterwards
    System.setErr(err);  
  }
  
  public Map<Integer, Integer> getGeneSpans(String text) {
    Map<Integer, Integer> begin2end = new HashMap<Integer, Integer>();
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
    for (CoreMap sentence : sentences) {
      List<CoreLabel> candidate = new ArrayList<CoreLabel>();
      for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
        String pos = token.get(PartOfSpeechAnnotation.class);
        if (pos.startsWith("NN")) {
          candidate.add(token);
        } else if (candidate.size() > 0) {
          int begin = candidate.get(0).beginPosition();
          int end = candidate.get(candidate.size() - 1).endPosition();
          begin2end.put(begin, end);
          candidate.clear();
        }
      }
      if (candidate.size() > 0) {
        int begin = candidate.get(0).beginPosition();
        int end = candidate.get(candidate.size() - 1).endPosition();
        begin2end.put(begin, end);
        candidate.clear();
      }
    }
    return begin2end;
  }
}
