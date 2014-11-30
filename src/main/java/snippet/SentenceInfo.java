package snippet;

import document.DocInfo;

public class SentenceInfo {

  public String content;
  public int startIndex;
  public int endIndex;
  public DocInfo hostDoc;
  public String sectionIndex;
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
  
  public SentenceInfo(String content, String sectionIndex, int startIndex, int endIndex, DocInfo hostDoc, Double score) {
    super();
    this.content = content;
    this.sectionIndex = sectionIndex;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.hostDoc = hostDoc;
    this.score = score;
  }

  public SentenceInfo(String content, String sectionIndex, int startIndex, int endIndex, DocInfo hostDoc) {
    super();
    this.content = content;
    this.sectionIndex = sectionIndex;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.hostDoc = hostDoc;
  }
  
  
  public String toString() {
    String out = "";
    out += "<SENT " + startIndex + ":" + endIndex + " doc:" + hostDoc.uri + " -" + content.replace('\n', ' ') + " " + String.format("%.3f", score) + ">";
    return out;
  }
}

