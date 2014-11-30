package snippets.scoring.strategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import snippet.scoring.adapter.CandidateAnswer;
import snippet.scoring.factory.Question;
import snippet.scoring.factory.Similarity;

public class DiceSimilarityStrategy implements Similarity {

  @Override
  public Double computeSimilarity(Question query, CandidateAnswer ans) {
    return diceCoefficient(query.getDocText(), ans.getDocText());
  }

  // Note that this implementation is case-sensitive!
  public static double diceCoefficient(String s1, String s2) {
    Set<String> nx = new HashSet<String>();
    Set<String> ny = new HashSet<String>();

    for (int i = 0; i < s1.length() - 1; i++) {
      char x1 = s1.charAt(i);
      char x2 = s1.charAt(i + 1);
      String tmp = "" + x1 + x2;
      nx.add(tmp);
    }
    for (int j = 0; j < s2.length() - 1; j++) {
      char y1 = s2.charAt(j);
      char y2 = s2.charAt(j + 1);
      String tmp = "" + y1 + y2;
      ny.add(tmp);
    }

    Set<String> intersection = new HashSet<String>(nx);
    intersection.retainAll(ny);
    double totcombigrams = intersection.size();

    return (2 * totcombigrams) / (nx.size() + ny.size());
  }

  /**
   * SOURCE: http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Dice's_coefficient
   */
  /**
   * Here's an optimized version of the dice coefficient calculation. It takes advantage of the fact
   * that a bigram of 2 chars can be stored in 1 int, and applies a matching algorithm of
   * O(n*log(n)) instead of O(n*n).
   * 
   * <p>
   * Note that, at the time of writing, this implementation differs from the other implementations
   * on this page. Where the other algorithms incorrectly store the generated bigrams in a set
   * (discarding duplicates), this implementation actually treats multiple occurrences of a bigram
   * as unique. The correctness of this behavior is most easily seen when getting the similarity
   * between "GG" and "GGGGGGGG", which should obviously not be 1.
   * 
   * @param s
   *          The first string
   * @param t
   *          The second String
   * @return The dice coefficient between the two input strings. Returns 0 if one or both of the
   *         strings are {@code null}. Also returns 0 if one or both of the strings contain less
   *         than 2 characters and are not equal.
   * @author Jelle Fresen
   */
  public static double diceCoefficientOptimized(String s, String t) {
    // Verifying the input:
    if (s == null || t == null)
      return 0;
    // Quick check to catch identical objects:
    if (s == t)
      return 1;
    // avoid exception for single character searches
    if (s.length() < 2 || t.length() < 2)
      return 0;

    // Create the bigrams for string s:
    final int n = s.length() - 1;
    final int[] sPairs = new int[n];
    for (int i = 0; i <= n; i++)
      if (i == 0)
        sPairs[i] = s.charAt(i) << 16;
      else if (i == n)
        sPairs[i - 1] |= s.charAt(i);
      else
        sPairs[i] = (sPairs[i - 1] |= s.charAt(i)) << 16;

    // Create the bigrams for string t:
    final int m = t.length() - 1;
    final int[] tPairs = new int[m];
    for (int i = 0; i <= m; i++)
      if (i == 0)
        tPairs[i] = t.charAt(i) << 16;
      else if (i == m)
        tPairs[i - 1] |= t.charAt(i);
      else
        tPairs[i] = (tPairs[i - 1] |= t.charAt(i)) << 16;

    // Sort the bigram lists:
    Arrays.sort(sPairs);
    Arrays.sort(tPairs);

    // Count the matches:
    int matches = 0, i = 0, j = 0;
    while (i < n && j < m) {
      if (sPairs[i] == tPairs[j]) {
        matches += 2;
        i++;
        j++;
      } else if (sPairs[i] < tPairs[j])
        i++;
      else
        j++;
    }
    return (double) matches / (n + m);
  }

}
