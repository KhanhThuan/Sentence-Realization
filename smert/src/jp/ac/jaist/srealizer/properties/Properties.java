package jp.ac.jaist.srealizer.properties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import jp.ac.jaist.realizer.data.builder.ModelBuilder;
import jp.ac.jaist.realizer.evaluation.EvalTool;
import jp.ac.jaist.srealizer.algorithms.linearization.LinearizationDependencyTree;
import jp.ac.jaist.srealizer.algorithms.mert.MertCore;
import jp.ac.jaist.srealizer.data.model.Candidate;
import jp.ac.jaist.srealizer.data.model.DependencyTree;
import jp.ac.jaist.srealizer.data.model.NGramStatistics;
import jp.ac.jaist.srealizer.data.model.TreeNode;
import jp.ac.jaist.srealizer.utils.CommonUtils;


public class Properties {
	public static final int ADD_ONE = 1;
	public static final int KNEY = 2;
	public static final int KYLM = 3;

	private Properties(){
		ngramWordStats = new NGramStatistics();
		ngramHeadWordStats = new NGramStatistics();
		ngramRDsStats = new NGramStatistics();
		searchStats = new NGramStatistics();
		nbestsSize = new ArrayList<Integer>();

	}
	public static Properties getProperties(){
		return p;
	}
	private static int smooth = 1;
	private static  NGramStatistics ngramWordStats;
	private static NGramStatistics ngramHeadWordStats;
	private static NGramStatistics ngramRDsStats;
	private static NGramStatistics searchStats;

	private static List<Integer> nbestsSize;
    private static int numberOfTrainSentence;
    private static int gramRD =3;
    private static int gramWord = 2;
    private static int gramHeadWord = 3;
    private static int counter= 0;
    private static double[] lambda;
    private static double expectedSept;
    private static Properties  p = new Properties();
    public static void setGrams(int rd, int w, int h){
    	gramHeadWord = h;
    	gramRD = rd;
    	gramWord = w;
    }
    private static String mode ="";
    
	public  int getGramRD() {
		return gramRD;
	}

	public int getGramWord() {
		return gramWord;
	}

	public  int getGramHeadWord() {
		return gramHeadWord;
	}

	public void setNgramWordStats(NGramStatistics ngramWordStats) {
		Properties.ngramWordStats = ngramWordStats;
	}

	public  void setNgramHeadWordStats(NGramStatistics ngramHeadWordStats) {
		Properties.ngramHeadWordStats = ngramHeadWordStats;
	}

	public void setNgramRDsStats(NGramStatistics ngramRDsStats) {
		Properties.ngramRDsStats = ngramRDsStats;
	}

	public  NGramStatistics getNgramWordStats() {
		return ngramWordStats;
	}

	public NGramStatistics getNgramHeadWordStats() {
		return ngramHeadWordStats;
	}

	public NGramStatistics getNgramRDsStats() {
		return ngramRDsStats;
	}

	public  void setProp(NGramStatistics ngramWordStatistics,
			NGramStatistics ngramHeadWordStatistics, NGramStatistics ngramRDsStatistics ){
		ngramWordStats = ngramWordStatistics;
		ngramHeadWordStats = ngramHeadWordStatistics;
	    ngramRDsStats = ngramRDsStatistics;
		
	}

	public  String getMode() {
		return mode.trim().length()>0 ?mode+ "/":"";
	}

	public  void setMode(String m) {
		Properties.mode = (m.equals("en") ? "en":"");
	}

	public  NGramStatistics getSearchStats() {
		return searchStats;
	}

	public  void setSearchStats(NGramStatistics searchStats) {
		Properties.searchStats = searchStats;
	}

	public  int getNumberOfTrainSentence() {
		return numberOfTrainSentence;
	}

	public  void setNumberOfTrainSentence(int numberOfTrainSentence) {
		Properties.numberOfTrainSentence = numberOfTrainSentence;
	}

	public  List<Integer> getNbestsSize() {
		return nbestsSize;
	}

	public void setNbestsSize(List<Integer> nbestsSize) {
		Properties.nbestsSize = nbestsSize;
	}
	public void setLamda(double[] lambda) {
		
		
	}
	public double[] getLambda() {
		return lambda;
	}
	public void setLambda(double[] lambda) {
		Properties.lambda = lambda;
	}
	public Double getExpectedSept() {
		return expectedSept;
	}
	public  void setExpectedSept(double expectedSept) {
		Properties.expectedSept = expectedSept;
	}
	public int getCounter() {
		counter++;
		return counter;
	}
	public int getSmooth() {
		return smooth;
	}
	public void setSmooth(int smooth) {
		Properties.smooth = smooth;
	}
	public String getSmoothSTR() {
		// TODO Auto-generated method stub
		return (smooth == ADD_ONE ? "Add one Smoothing": smooth == KNEY ? "Kneser-ney Smoothing" :"Modified Kneser-ney Smothing by Kylm");
	}
	public void realizeSentences(int numberTrainSentences, String testFile, String refFile){
		Properties.getProperties().setSmooth(Properties.ADD_ONE);
	
	   
		DependencyTree  tree = new DependencyTree();

		ModelBuilder.makeDependencyStatistics(ModelBuilder.getDependencyTreeFile(), tree,new int[]{2,3}, 3);
		
		Properties.getProperties().setExpectedSept(0.7);
		
		
		
       System.out.println(Properties.getProperties().getNgramHeadWordStats().getWordCount());
       System.out.println(Properties.getProperties().getNgramRDsStats().getWordCount());
      
       System.out.println(Properties.getProperties().getSearchStats().getWordCount());
       Map<Integer,Integer> indices = new HashMap<Integer, Integer>();
       List<TreeNode> corpara = new ArrayList<TreeNode>();
        for(int i =0; i < tree.getSentences().size(); i++){
        	indices.put((int) tree.getSentences().get(i).getIndexSentence(), i);
        	
        		corpara.add(tree.getSentences().get(i));
        	
        }
       
        // Collections.shuffle(corpara); //Remove comment to select training set randomly.
     
      //  int pivot = (int)(corpara.size() * 0.1);
		int pivot = numberTrainSentences; // Number of training sentences;
		System.out.println("Train Sentence: " + pivot);
		List<TreeNode> trainSentences= corpara.subList(0,pivot);
		for(int i =0; i < trainSentences.size(); i++){
        	indices.put((int) trainSentences.get(i).getIndexSentence(), i);
        	
        }
		double[] intitialLambda = new double[]{0,0.1,0.2,-0.1};
	      CommonUtils.setParams(intitialLambda, "params.txt");
	      CommonUtils.setSDconfig("SDecoder_cfg.txt","cand_database_.txt", intitialLambda);
	      double[] optimizedLambda = null;
		try {
			/*******************************Training**********************************/
			int ops = 0;
			List<double[]> allLamdas = new ArrayList<double[]>();
			while(true){
				  CommonUtils.setParams(intitialLambda, "params.txt");
			      CommonUtils.setSDconfig("SDecoder_cfg.txt","cand_database_.txt", intitialLambda);
				 List<List<Candidate>> candidates = LinearizationDependencyTree.linearizeAll(trainSentences,tree, tree.getDependency(), tree.getDependencyRatio(), true);
				System.out.println("Number of Candidates: " + candidates.size());
				if(candidates.size() == 0) break;
					//CommonUtils.printList(Properties.getProperties().getNbestsSize());
					
					 MertCore mert = new MertCore("ZMERT_cfg.txt");
				      mert.run_MERT(); // optimize lambda[]!!!

				      mert.finish();
				      for(int i = 0; i < candidates.size(); i++){
				    	  for(Candidate c : candidates.get(i)){
					    		double score = 0.0;
					    		for(int j = 0; j < c.getFeats().length; j++){
					    			 score += mert.getLambda()[j+1] * c.getFeats()[j];

					    		}
					    		c.setAvgScore(score);
					    	}
					      int[] idx = LinearizationDependencyTree.sort(candidates.get(i));
					      Candidate c = candidates.get(i).get(idx[0]);
					     // System.out.println(c.getIndexSentence() + ": " + trainSentences.size());
					      trainSentences.get(indices.get(c.getIndexSentence())).getNodes().get(c.getIndexNode()).setHasOptimized(true);
					   //   System.out.println(c.getContent() +" : " +trainSentences.get(indices.get(c.getIndexSentence())).getNodes().get(c.getIndexNode()).getRefWordSequence() );
					      trainSentences.get(indices.get(c.getIndexSentence())).getNodes().get(c.getIndexNode()).setOptimizedSequence(c.getContent());
					      

				      }
				      
				      CommonUtils.setParams(mert.getLambda(), "params.txt");
				      CommonUtils.setSDconfig("SDecoder_cfg.txt","cand_database_.txt", mert.getLambda());
				      ops = 0;
						for(TreeNode t: trainSentences){
							if(t.isHasOptimized()) ops++;
							else break;
						}
						System.out.println("(OP Lamda) : [" + mert.getLambda()[1] + "," +mert.getLambda()[2] + "," + mert.getLambda()[3] + "]");
						optimizedLambda = new double[]{mert.getLambda()[1],mert.getLambda()[2],mert.getLambda()[3]};
					      allLamdas.add(optimizedLambda);

						if(ops == trainSentences.size()) break;
			
		}
			
			System.out.println("Training Model");
			/*******************************END_Training**********************************/

			// taking average of all optimal lambdas during each linearization round (Should it be)
			boolean byMean = false; 
			double[] lamdas = new double[3];
			if(byMean){
				for(double[] d: allLamdas){
					for(int i = 0; i < d.length; i++){
						lamdas[i] += d[i];
					}
				}
				for(int i =0; i < 3; i++){
					lamdas[i] /= allLamdas.size();
				}
			}else{
				
				// Or by median
			   double[][] x = CommonUtils.sort(allLamdas);
			   for(int i =0; i < 3; i++){
					lamdas[i] =x[i][allLamdas.size()/2];
				}
			}
			
			/*******************************Testing**********************************/
			System.out.println("Training Model finished.");
			System.out.println("Testing.....");

				DependencyTree testTree = new DependencyTree();
				 ModelBuilder.makeTestSentences(testFile, testTree,new int[]{2,3}, 3);
				List<TreeNode> testSentences=testTree.getSentences();
			//	nTest += incsSize;
				Properties.getProperties().setLambda(/*optimizedLambda*/ lamdas);
				try{
					LinearizationDependencyTree.linearizeAll(testSentences,tree, tree.getDependency(), tree.getDependencyRatio(), false);
				}catch(Exception e){
					e.printStackTrace();
				}
				 System.out.println("Priting result......");
				 String candFileName =  "results/calculated_result.txt";
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(candFileName),"UTF-8"));
				//BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ref_database_test.txt"),"UTF-8"));
	             int i = 0;
	         	StringBuilder  ref= new StringBuilder("");
	         	StringBuilder  cand= new StringBuilder("");
	
	         	StringBuilder  src= new StringBuilder("");
	         	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE mteval SYSTEM \"ftp://jaguar.ncsl.nist.gov/mt/resources/mteval-xml-v1.3.dtd\">";
	         	ref.append(xml);
	         	src.append(xml);
	         	cand.append(xml);
	         	
	    		ref.append("<mteval>\n<refset setid=\"example_set\" srclang=\"Eng\" trglang=\"Eng\" refid=\"ref1\">");
	    		ref.append("<doc docid=\"doc1\" genre=\"nw\">");
	    		//
	    		cand.append("<mteval>\n<tstset setid=\"example_set\" srclang=\"Eng\" trglang=\"Eng\" sysid=\"sample_system\">");
	    		cand.append("<doc docid=\"doc1\" genre=\"nw\">");
	    		//
	    		src.append("<mteval>\n<srcset setid=\"example_set\" srclang=\"Eng\"  >");
	    		src.append("<doc docid=\"doc1\" genre=\"nw\">");
	    		BufferedReader	br = new BufferedReader(new InputStreamReader(new FileInputStream(refFile ),"UTF-8"));

				 for(TreeNode t: testSentences){
				
					 ref.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
					 src.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
					 cand.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
	
					 bw.write(i+ "|||" + t.getOptimizedSequence());
					 bw.newLine();
					 String[] s = br.readLine().split("\t");
					 int sen = 1;
					 if(s.length == 1)sen =0;
					 ref.append(StringEscapeUtils.escapeXml(s[sen].trim()) + "\n\t\t</seg>\n\t</p>");
					 src.append(StringEscapeUtils.escapeXml(s[sen].trim()) + "\n\t\t</seg>\n\t</p>");
	
					 cand.append(StringEscapeUtils.escapeXml(t.getOptimizedSequence()) + "\n\t\t</seg>\n\t</p>");
					
				
					 i++;
						//if(t.isHasOptimized()) ops++;
						//else break;
				}
				br.close();
				 bw.flush();
				
				 bw.close();
				 ref.append("</doc>");
				 ref.append("</refset>");
				 //
				 cand.append("</doc>");
				 cand.append("</tstset>");
				 //
				 src.append("</doc>");
				 src.append("</srcset>");
				 //
				 ref.append("</mteval>");
		         src.append("</mteval>");
		         cand.append("</mteval>");
		         
				
				 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode()+ "results/mteval/src.xml"),"UTF-8"));
				 bw.write(src.toString());
				 bw.close();
				 //
				 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode()+  "results/mteval/tst.xml"),"UTF-8"));
				 bw.write(cand.toString());
				 bw.close();
				 //
				 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode()+  "results/mteval/ref.xml"),"UTF-8"));
				 bw.write(ref.toString());
				 bw.close();
				 bw.close();
				 
				 StringBuffer rs =  new StringBuffer();
				 rs.append("<=====================================================>\n");

				 rs.append("\t\tSummary\n");
				 rs.append("train:" + trainSentences.size() + " sentences\n");
				 rs.append("NGRAM SMOOTH Method: " + Properties.getProperties().getSmoothSTR() + "\n");
				 rs.append("Pre/Post sept: " + Properties.getProperties().getExpectedSept() + "\n");
				 rs.append("Mean Optimal: " + byMean + "\n");

				 rs.append("=> Optimizing Lambda: [" + Properties.getProperties().getLambda()[0] + "," + Properties.getProperties().getLambda()[1] + "," +  Properties.getProperties().getLambda()[2] +"]\n");
				 rs.append("test:" + testSentences.size() + " sentences\n");
				 rs.append("=> Bleu Summary:\n");
				 EvalTool.processArgsAndInitialize(candFileName,refFile);
				 EvalTool.setCandFileName(candFileName);
				 EvalTool.setRefFileName(refFile);
				 rs.append("Evaluating set of " + 1 + "'th candidate realization " + "...\n");
				
				 StringBuffer  bs = EvalTool.evaluateCands_nbest(candFileName,1);
				 rs.append(bs.toString());
				 rs.append("\n ");
				 rs.append("\n\t========================================================\n");
				 
				
				 System.out.println(rs.toString());
				 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode()+  "results/builtin-eval/bleu.txt", true),"UTF-8"));
				
				 System.out.println("==> save to results/builtin-eval/bleu.txt");
				 bw.write(rs.toString());
				 bw.close();
				 
				 System.out.println("See: results/calculated_result.txt");
				 System.out.println("See: results/mteval for testing BLEU with mteval by NIST");

		
		      
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     