package descriptorimpl;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import com.google.common.collect.Lists;

import json.JsonCollectionReaderHelper;
import json.gson.TestQuestion;
import json.gson.TestSet;

/**
 * CollectionReader for reading questions and gold standard answers from the input file.
 * 
 * @author josephc1
 *
 */
public class QuestionReader extends CollectionReader_ImplBase {

  private List<TestQuestion> inputs;

  private int numberOfQuestions;

  @Override
  /**
   * The initializer loads the single input file to memory and performs some syntax clean up.
   */
  public void initialize() throws ResourceInitializationException {
    super.initialize();
    String filePath = "/BioASQ-SampleData1B.json";

    inputs = Lists.newArrayList();

    Object value = filePath;
    if (String.class.isAssignableFrom(value.getClass())) {
      inputs = TestSet.load(getClass().getResourceAsStream(String.class.cast(value))).stream()
              .collect(toList());
    } else if (String[].class.isAssignableFrom(value.getClass())) {
      inputs = Arrays.stream(String[].class.cast(value))
              .flatMap(path -> TestSet.load(getClass().getResourceAsStream(path)).stream())
              .collect(toList());
    }
    // trim question texts
    inputs.stream().filter(input -> input.getBody() != null)
            .forEach(input -> input.setBody(input.getBody().trim().replaceAll("\\s+", " ")));

    numberOfQuestions = inputs.size();
  }

  /**
   * Create one CAS for each questions in the input file. All gold standard answers, if existed, are
   * also added to the index.
   */
  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }

    TestQuestion question = inputs.remove(0);
    jcas.setDocumentText(question.getBody());
    JsonCollectionReaderHelper.addQuestionToIndex(question, jcas);
  }

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return inputs.size() > 0;
  }

  /**
   * The unit for progress is number of questions processed out of the total number of questions in
   * the input file.
   */
  @Override
  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(numberOfQuestions - inputs.size(), numberOfQuestions,
            Progress.ENTITIES) };
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }

}
