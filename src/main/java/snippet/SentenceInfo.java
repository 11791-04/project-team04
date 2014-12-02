package snippet;

import document.DocInfo;

/**
 * The SentenceInfo class represent one sentence in the full text of a document retrieved from
 * pubmed.
 * 
 * @author josephcc
 *
 */
public class SentenceInfo {

  /**
   * The textual content of the sentence.
   */
  public String content;

  /**
   * the start index in the full text
   */
  public int startIndex;

  /**
   * the end index in the full text
   */
  public int endIndex;

  /**
   * the corresponding document object
   */
  public DocInfo hostDoc;

  /**
   * the section index in the full text. we do not support sentences (snippets) that cross multiple
   * sections.
   */
  public String sectionIndex;

  /**
   * Similarity score comparing to the question
   */
  public Double score;

  public String getContent() {
    return content;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public int getEndIndex() {
    return endIndex;
  }

  public DocInfo getHostDoc() {
    return hostDoc;
  }

  public SentenceInfo(String content, String sectionIndex, int startIndex, int endIndex,
          DocInfo hostDoc, Double score) {
    super();
    this.content = content;
    this.sectionIndex = sectionIndex;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.hostDoc = hostDoc;
    this.score = score;
  }

  public SentenceInfo(String content, String sectionIndex, int startIndex, int endIndex,
          DocInfo hostDoc) {
    super();
    this.content = content;
    this.sectionIndex = sectionIndex;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.hostDoc = hostDoc;
  }

  /**
   * pretty print function
   */
  public String toString() {
    String out = "";
    out += "<SENT " + startIndex + ":" + endIndex + " doc:" + hostDoc.uri + " -"
            + content.replace('\n', ' ') + " " + String.format("%.3f", score) + ">";
    return out;
  }
}
