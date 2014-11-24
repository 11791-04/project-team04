package concept.prf;

public class PseudoRelevanceFeedbackFactory {

  public static FindingPseudoRelevanceFeedback getFindingPseudoRelevanceFeedback() {
    return new FindingPseudoRelevanceFeedback();
  }
  
  public static DocumentPseudoRelevanceFeedback getDocumentPseudoRelevanceFeedback() {
    return new DocumentPseudoRelevanceFeedback();
  }
  
}
