package snippetextraction;

import docretrieval.DocInfo;

public class SentenceInfo {

  String content;
  int startIndex;
  int endIndex;
  DocInfo hostDoc;
  
  public SentenceInfo(String content, int startIndex, int endIndex, DocInfo hostDoc) {
    super();
    this.content = content;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.hostDoc = hostDoc;
  }
}

