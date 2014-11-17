package snippetextraction;

import docretrieval.DocInfo;

public class SentenceInfo {

  private String content;
  private int startIndex;
  private int endIndex;
  private DocInfo hostDoc;
  
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

  public SentenceInfo(String content, int startIndex, int endIndex, DocInfo hostDoc) {
    super();
    this.content = content;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.hostDoc = hostDoc;
  }
}

