

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.oaqa.type.retrieval.Document;


public class RetrievalEvaluator extends CasConsumer_ImplBase {

  @Override
  public void initialize() throws ResourceInitializationException {
    super.initialize();
    
  }

  /**
   * TODO :: 1. construct the global word dictionary 2. keep the word
   * frequency for each sentence
   */
  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {
    //System.out.println("RetrievalEvaluator -> processCas");

    JCas jcas;
    try {
      jcas =aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

//    FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
//
//    if (it.hasNext()) {
//      Document doc = (Document) it.next();
//      System.out.println(doc.getUri());
//      
//    }
//    
//    FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
//    if (iter.isValid()) {
//      iter.moveToNext();
//      Document doc = (Document) iter.get();
//      createTermFreqVector(jcas, doc);
//    }

  }

  /**
   * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
   * Compute the MRR metric
   */
  @Override
  public void collectionProcessComplete(ProcessTrace arg0)
          throws ResourceProcessException, IOException {
    //System.out.println("RetrievalEvaluator -> collectionProcessComplete");
    super.collectionProcessComplete(arg0);




  }

}
