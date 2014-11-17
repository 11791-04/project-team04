package snippetextraction;

import docretrieval.DocInfo;

public class SentenceInfo {

  public String content;
  public int startIndex;
  public int endIndex;
  public DocInfo hostDoc;
  public int sectionIndex;
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

  public SentenceInfo(String content, int sectionIndex, int startIndex, int endIndex, DocInfo hostDoc) {
    super();
    this.content = content;
    this.sectionIndex = sectionIndex;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.hostDoc = hostDoc;
    score = null;
  }
  
  public String toString() {
    String out = "";
    out += "<SENT " + startIndex + ":" + endIndex + " doc:" + hostDoc + " " + content + ">";
    return out;
  }
}

