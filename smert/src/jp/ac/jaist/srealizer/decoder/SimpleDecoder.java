package jp.ac.jaist.srealizer.decoder;
import java.util.*;
import java.io.*;
import java.text.DecimalFormat;


public class SimpleDecoder
{
  private static DecimalFormat f3 = new DecimalFormat("###0.000");
  public static void main(String[] args) throws Exception
  {
    String configFileName = args[0];
    String outputFileName = args[1];
    System.out.println("Simple Decoder ------------: " + args.toString());
    int numParams = 3;

    double[] weights = new double[numParams];
    String candsFileName = "";
    //int cps = 0;
   int N = 0;

    InputStream inStream = new FileInputStream(new File(configFileName));
    BufferedReader inFile = new BufferedReader(new InputStreamReader(inStream, "utf8"));

    String line = inFile.readLine();
    while (line != null) {
      if (line.startsWith("cands_file")) {
        candsFileName = (line.substring(line.indexOf("=")+1)).trim();
      } else if (line.startsWith("cands_per_sen")) {
        //cps = Integer.parseInt((line.substring(line.indexOf("=")+1)).trim());
      } else if (line.startsWith("top_n")) {
        N = Integer.parseInt((line.substring(line.indexOf("=")+1)).trim());
      } else if (line.startsWith("RD")) {
        weights[0] = Double.parseDouble((line.substring(2+1)).trim());
      } else if (line.startsWith("Word Model")) {
        weights[1] = Double.parseDouble((line.substring(11+1)).trim());
      } else if (line.startsWith("Headword Model")) {
        weights[2] = Double.parseDouble((line.substring(12+1)).trim());
      } else if (line.startsWith("#")) {
      } else if (line.length() > 0) {
        println("Wrong format in config file.");
        System.exit(1);
      }
      line = inFile.readLine();
    }

    inFile.close();
 
    
    List<List<Candidate>> candidates = new ArrayList<List<Candidate>>();

    inStream = new FileInputStream(new File(candsFileName));
    inFile = new BufferedReader(new InputStreamReader(inStream, "utf8"));
    
    line =  null;
    int count = 0;
    String prevIndex = null;
    while((line = inFile.readLine()) != null){
    	/*
    	line format:

    	i ||| words of candidate translation . ||| feat-1_val feat-2_val ... feat-numParams_val .*

    	*/
  	  String[] s = line.split("([|]+)");
  	  String curIndex = s[0].trim();
  	  if(prevIndex == null || !prevIndex.equals(curIndex)){
  		  count++;
  		  candidates.add(new ArrayList<Candidate>());

  	  }
  	  Candidate candidate = new SimpleDecoder(). new Candidate();
  	  
  	  double[] feats = new double[3];
  	  String[] nums = s[2].trim().split("[\\s]+");
  	  double score = 0.0;
  	  for(int c =0; c < 3; c++){
  		feats[c] = Double.parseDouble(nums[c]);
  		score += weights[c] * feats[c];
  	  }
  	  String[] indicies = s[0].trim().split("-");
	  
  	  candidate.setIndexSentence(Integer.parseInt(indicies[0]));
  	  
  	  if(indicies.length > 1) 
  		  candidate.setIndexNode(Integer.parseInt(indicies[1]));
  	  candidate.setId(s[0].trim());
  	  candidate.setAvgScore(score);
  	  candidate.setContent(s[1].trim());
  	  candidate.setFeats(feats);
  	  candidates.get(count-1).add(candidate);
  	  prevIndex = curIndex;
	         
	     
    
    }

    

    FileOutputStream outStream = new FileOutputStream(outputFileName, false); // false: don't append
    OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "utf8");
    BufferedWriter outFile = new BufferedWriter(outStreamWriter);
    for (int i = 0; i < candidates.size(); ++i) {
      int[] indices = sort(candidates.get(i));
      for (int n = 0; n < (candidates.get(i).size() >  N ?N :candidates.get(i).size()); ++n) {
        String str = "" + candidates.get(i).get(indices[n]).getId() + " ||| " + candidates.get(i).get(indices[n]).getContent() + " ||| ";
        for (int c = 0; c < numParams; ++c) {
          str += " " + f3.format(candidates.get(i).get(indices[n]).getFeats()[c]);
        }
        str += " ||| " + f3.format(candidates.get(i).get(indices[n]).getAvgScore());
        writeLine(str, outFile);
        //System.out.println("$$ -decoder: " + str);
      }

    }
    outFile.flush();
    outFile.close();
    System.exit(0);

  }

  private static int[] sort(List<Candidate> candidates) {

	    int numCands = candidates.size();
	    int[] retA = new int[numCands];
	    double[] sc = new double[numCands];

	    for (int n = 0; n < numCands; ++n) {
	      retA[n] = n;
	      sc[n] = candidates.get(n).getAvgScore();
	    }

	    for (int j = 0; j < numCands; ++j) {
	      int best_k = j;
	      double best_sc = sc[j];
	      for (int k = j+1; k < numCands; ++k) {
	        if (sc[k] > best_sc) {
	          best_k = k;
	          best_sc = sc[k];
	        }
	      }

	      // switch j and best_k
	      int temp_n = retA[best_k];
	      retA[best_k] = retA[j];
	      retA[j] = temp_n;

	      double temp_sc = sc[best_k];
	      sc[best_k] = sc[j];
	      sc[j] = temp_sc;
	    }

	    return retA;
}

private static int[] sort(double[][] scores, int i)
  {
    int numCands = scores[i].length;
    int[] retA = new int[numCands];
    double[] sc = new double[numCands];

    for (int n = 0; n < numCands; ++n) {
      retA[n] = n;
      sc[n] = scores[i][n];
    }

    for (int j = 0; j < numCands; ++j) {
      int best_k = j;
      double best_sc = sc[j];
      for (int k = j+1; k < numCands; ++k) {
        if (sc[k] > best_sc) {
          best_k = k;
          best_sc = sc[k];
        }
      }

      // switch j and best_k
      int temp_n = retA[best_k];
      retA[best_k] = retA[j];
      retA[j] = temp_n;

      double temp_sc = sc[best_k];
      sc[best_k] = sc[j];
      sc[j] = temp_sc;
    }

    return retA;
  }

  private static void sort(int[] keys, double[] vals, int start, int end)
  {
    if (end-start > 1) {
      int mid = (start+end)/2;
      sort(keys,vals,start,mid);
      sort(keys,vals,mid+1,end);

    }
  }

  private static int countLines(String fileName) throws Exception
  {
    BufferedReader inFile = new BufferedReader(new FileReader(fileName));

    String line;
    int count = 0;
    do {
      line = inFile.readLine();
      if (line != null) ++count;
    }  while (line != null);

    inFile.close();

    return count;
  }

  private static void writeLine(String line, BufferedWriter writer) throws IOException
  {
    writer.write(line, 0, line.length());
    writer.newLine();
    writer.flush();
  }

  private static void println(Object obj) { System.out.println(obj); }
  private static void print(Object obj) { System.out.print(obj); }
  class Candidate {
		
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public double[] getFeats() {
			return feats;
		}
		public void setFeats(double[] feats) {
			this.feats = feats;
		}
		public double getAvgScore() {
			return avgScore;
		}
		public void setAvgScore(double avgScore) {
			this.avgScore = avgScore;
		}
		public List<String> getRefs() {
			return refs;
		}
		public void setRefs(List<String> refs) {
			this.refs = refs;
		}
		
		public int getIndexSentence() {
			return indexSentence;
		}
		public void setIndexSentence(int indexSentence) {
			this.indexSentence = indexSentence;
		}
		public int getIndexNode() {
			return indexNode;
		}
		public void setIndexNode(int indexNode) {
			this.indexNode = indexNode;
		}

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}

		private String content;
		private double[] feats;
	    private double avgScore;
	    private List<String> refs;
	    private int indexSentence;
	    private int indexNode;
	    private String id;
	}


}
                                                                                                                                                                                                                                                                                