package jp.ac.jaist.srealizer.algorithms.mert;
/* This file is part of the Z-MERT Training Tool for MT systems.
 * 
 * Z-MERT is an open-source tool, licensed under the terms of the
 * GNU Lesser General Public License (LGPL). Therefore, it is free
 * for personal and scientific use by individuals and/or research
 * groups. It may not be modified or redistributed, publicly or
 * privately, unless the licensing terms are observed. If in doubt,
 * contact the author for clarification and/or an explicit
 * permission.
 *
 * If you use Z-MERT in your work, please cite the following paper:
 *
 *       Omar F. Zaidan. 2009. Z-MERT: A Fully Configurable Open
 *       Source Tool for Minimum Error Rate Training of Machine
 *       Translation Systems. The Prague Bulletin of Mathematical
 *       Linguistics, No. 91:79-88.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 */

import java.util.*;
import java.text.DecimalFormat;
import java.io.*;

public abstract class EvaluationMetric
{
  /* static data members */
  private static TreeMap<String,Integer> metricOptionCount; // maps metric names -> number of options for that metric
  protected static int numSentences; // number of sentences in the MERT set
  protected static int numDocuments; // number of documents in the MERT set
  //protected static int refsPerSen;
  protected static List<List<String>> refSentences;
  protected final static DecimalFormat f0 = new DecimalFormat("###0");
  protected final static DecimalFormat f4 = new DecimalFormat("###0.0000");
  protected static String tmpDirPrefix;

  /* non-static data members */
  protected int suffStatsCount; // number of sufficient statistics
  protected String metricName; // number of metric
  protected boolean toBeMinimized;
    // is this a metric that should be minimized?
    // e.g. toBeMinimized = true for 01LOSS, WER, TER
    //      toBeMinimized = false for BLEU

  /* static (=> also non-abstract) methods */
  public static void set_knownMetrics()
  {
    metricOptionCount = new TreeMap<String,Integer>();

    metricOptionCount.put("BLEU",2);
      // the "BLEU" metric expects an options array of length 2
    metricOptionCount.put("BLEU_SBP",2);
      // the "BLEU_SBP" metric expects an options array of length 2
    metricOptionCount.put("01LOSS",0);
      // the "01LOSS" metric expects an options array of length 0
    metricOptionCount.put("TER",6);
      // the "TER" metric expects an options array of length 5
//    metricOptionCount.put("METEOR",4);
      // the "METEOR" metric expects an options array of length 4
//    metricOptionCount.put("RYPT",5);
      // the "RYPT" metric expects an options array of length 5
    metricOptionCount.put("TER-BLEU",8);
      // the "TER-BLEU" metric expects an options array of length 7
//    metricOptionCount.put("WER",0);
      // the "WER" metric expects an options array of length 0
    metricOptionCount.put("BLEU_TER-th",9);
      // the "BLEU_TER-th" metric expects an options array of length 7
//    metricOptionCount.put("PP_BLEU",4);
  }

  public static EvaluationMetric getMetric(String metricName, String[] metricOptions)
  {
    EvaluationMetric retMetric = null;

    if (metricName.equals("BLEU")) {
      retMetric = new BLEU(metricOptions);          // the "BLEU" metric corresponds to the BLEU class
    } /**/

    return retMetric;
  }

  public static void set_numSentences(int x) { numSentences = x; }
  public static void set_numDocuments(int x) { numDocuments = x; }
  //public static void set_refsPerSen(int x) { refsPerSen = x; }
  public static void set_tmpDirPrefix(String S) { tmpDirPrefix = S; }
  public static void set_refSentences(List<List<String>> refs)
  {
	  refSentences = new ArrayList<List<String>>();
	    for (int i = 0; i < refs.size(); ++i) {
	    	refSentences.add(new ArrayList<String>());
	      for (int r = 0; r < refs.get(i).size(); ++r) {
	        refSentences.get(i).add(refs.get(i).get(r));
	      }
	    }
  }

  public static boolean knownMetricName(String name)
  {
    return metricOptionCount.containsKey(name);
  }

  public static int metricOptionCount(String name)
  {
    return metricOptionCount.get(name);
  }

  /* non-abstract, non-static methods */
  public int get_suffStatsCount() { return suffStatsCount; }
  public String get_metricName() { return metricName; }
  public boolean getToBeMinimized() { return toBeMinimized; }
  public boolean isBetter(double x, double y)
  {
    // return true if x is better than y
    if (toBeMinimized) {
      return (x < y);
    } else {
      return (x > y);
    }
  }

  public double score(String cand_str, int i)
  {
    List<String> SA = new ArrayList<String>(); SA.add(cand_str);
    List<Integer> IA = new ArrayList<Integer>(); IA.add(i);

    int[][] SS = suffStats(SA,IA);

    int[] stats = new int[suffStatsCount];
    for (int s = 0; s < suffStatsCount; ++s) { stats[s] = SS[0][s]; }

    return score(stats);
  }

  public double score(List<String> topCand_str)
  {
    int[] stats = suffStats(topCand_str);
    return score(stats);
  }

  public int[] suffStats(List<String> topCand_str)
  {
    //int[] IA = new int[numSentences];
    List<Integer> IA = new ArrayList<Integer>();
    for (int i = 0; i < numSentences; ++i) { IA.add(i); }

    int[][] SS = suffStats(topCand_str,IA);

    int[] totStats = new int[suffStatsCount];
    for (int s = 0; s < suffStatsCount; ++s) {
      totStats[s] = 0;
      for (int i = 0; i < numSentences; ++i) {
        totStats[s] += SS[i][s];
      }
    }

    return totStats;
  }

  public int[][] suffStats(List<String> cand_strings, List<Integer> cand_indices)
  {
    // calculate sufficient statistics for each sentence in an arbitrary set of candidates

    int candCount = cand_strings.size();
    if (cand_indices.size() != candCount) {
      System.out.println("Array lengths mismatch in suffStats(String[],int[]); returning null.");
      return null;
    }

    int[][] stats = new int[candCount][suffStatsCount];

    for (int d = 0; d < candCount; ++d) {
      int[] currStats = suffStats(cand_strings.get(d),cand_indices.get(d));

      for (int s = 0; s < suffStatsCount; ++s) { stats[d][s] = currStats[s]; }
    } // for (d)

    return stats;
  }

  public void createSuffStatsFile(String cand_strings_fileName, String cand_indices_fileName, String outputFileName)
  {
    // similar to the above suffStats(String[], int[])

    try {
      FileInputStream inStream_cands = new FileInputStream(cand_strings_fileName);
      BufferedReader inFile_cands = new BufferedReader(new InputStreamReader(inStream_cands, "utf8"));

      FileInputStream inStream_indices = new FileInputStream(cand_indices_fileName);
      BufferedReader inFile_indices = new BufferedReader(new InputStreamReader(inStream_indices, "utf8"));

      PrintWriter outFile = new PrintWriter(outputFileName);
      
     List<String> cand_strings = new ArrayList<String>();
      List<Integer> cand_indices = new ArrayList<Integer>();

      String line_cand = inFile_cands.readLine();
      String line_index = inFile_indices.readLine();

      while (line_cand != null) {
        int size = 0;
        while (line_cand != null) {
          cand_strings.add(line_cand);
          cand_indices.add(Integer.parseInt(line_index));
          ++size; // now size is how many were read for this currnet batch
         // if (size == maxBatchSize) break;

          line_cand = inFile_cands.readLine();
          line_index = inFile_indices.readLine();
        }
        System.out.println("Size: " + size );
   /*     if (size < maxBatchSize) { // last batch, and smaller than maxBatchSize
          String[] cand_strings_temp = new String[size];
          int[] cand_indices_temp = new int[size];
          for (int d = 0; d < size; ++d) {
            cand_strings_temp[d] = cand_strings[d];
            cand_indices_temp[d] = cand_indices[d];
          }
          cand_strings = cand_strings_temp;
          cand_indices = cand_indices_temp;
        }*/

        int[][] SS = suffStats(cand_strings, cand_indices);
        for (int d = 0; d < size; ++d) {
          String stats_str = "";

          for (int s = 0; s < suffStatsCount-1; ++s) { stats_str += SS[d][s] + " "; }
          stats_str += SS[d][suffStatsCount-1];

          outFile.println(stats_str);
        }

        line_cand = inFile_cands.readLine();
        line_index = inFile_indices.readLine();
      }

      inFile_cands.close();
      inFile_indices.close();
      outFile.close();

    } catch (IOException e) {
      System.err.println("IOException in EvaluationMetric.createSuffStatsFile(...): " + e.getMessage());
      System.exit(99902);
    }

  }

  public void printDetailedScore(List<String> topCand_str, boolean oneLiner)
  {
    int[] stats = suffStats(topCand_str);
    printDetailedScore_fromStats(stats,oneLiner);
  }

  public double score(int[][] stats)
  {
    // returns an average of document scores (aka the document-level score, as opposed to corpus-level score)
    // stats[][] is indexed [doc][s]

    double retVal = 0.0;
    for (int doc = 0; doc < numDocuments; ++doc) {
      retVal += score(stats[doc]);
    }
    return retVal / numDocuments;
  }

  public double score(int[][] stats, int firstRank, int lastRank)
  {
    // returns an average of document scores, restricted to the documents
    // ranked firstRank-lastRank, inclusive (ranks are 1-indexed, even though the docs are 0-indexed)

    double[] scores = docScores(stats);

    Arrays.sort(scores);
    // sorts into ascending order

    double retVal = 0.0;

    if (toBeMinimized) {
      // scores[0] is rank 1, scores[numDocuments-1] is rank numDocuments
      //   => scores[j] is rank j+1
      //   => rank r is scores[r-1]
      for (int j = firstRank-1; j < lastRank; ++j) {
        retVal += scores[j];
      }
    } else {
      // scores[numDocuments-1] is rank 1, scores[0] is rank numDocuments
      //   => scores[j] is rank numDocuments-j
      //   => rank r is scores[numDocuments-r]
      for (int j = numDocuments-firstRank; j >= numDocuments-lastRank; --j) {
        retVal += scores[j];
      }
    }

    return retVal / (lastRank-firstRank+1);

  }

  public double[] docScores(int[][] stats)
  {
    // returns an array of document scores
    // stats[][] is indexed [doc][s]

    double[] scores = new double[numDocuments];
    for (int doc = 0; doc < numDocuments; ++doc) {
      scores[doc] = score(stats[doc]);
    }
    return scores;
  }

  public void printDetailedScore_fromStats(int[][] stats, String[] docNames)
  {
    // prints individual document scores
    // stats[][] is indexed [doc][s]

    for (int doc = 0; doc < numDocuments; ++doc) {
      if (docNames == null) {
        System.out.print("Document #" + doc + ": ");
      } else {
        System.out.print(docNames[doc] + ": ");
      }
      printDetailedScore_fromStats(stats[doc],true);
    }
  }

  /* abstract (=> also non-static) methods */
  protected abstract void initialize();
  public abstract double bestPossibleScore();
  public abstract double worstPossibleScore();
  public abstract int[] suffStats(String cand_str, int i);
  public abstract double score(int[] stats);
  public abstract StringBuffer printDetailedScore_fromStats(int[] stats, boolean oneLiner);
}
                                                                                                                                                                                                                                                