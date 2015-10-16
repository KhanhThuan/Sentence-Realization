package jp.ac.jaist.srealizer.test;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import jp.ac.jaist.realizer.data.builder.ModelBuilder;
import jp.ac.jaist.realizer.evaluation.EvalTool;
import jp.ac.jaist.srealizer.algorithms.linearization.LinearizationDependencyTree;
import jp.ac.jaist.srealizer.algorithms.mert.MertCore;
import jp.ac.jaist.srealizer.data.model.Candidate;
import jp.ac.jaist.srealizer.data.model.DependencyTree;
import jp.ac.jaist.srealizer.data.model.TreeNode;
import jp.ac.jaist.srealizer.properties.Properties;
import jp.ac.jaist.srealizer.utils.CommonUtils;



public class Test {

	public static void main(String[] args) {
		
		String line= "Versions of Stanford Dependencies have also been developed by outside groups for a number of other languages. Two prominent examples are Finnish (the Turku Dependency Treebank) and Persian (the Uppsala Persian Dependency Treebank). There is now a multi-site effort to produce dependency treebanks over a broad range of languages adopting a compatible dependency taxonomy. More details about this Universal Dependency Treebank can be found in the LREC 2014 paper mentioned above, in the current treebank release, and in new documentation.";
		String[] ws = line.split("([.][\\s\\t\n]+|[?]|!|[.$])");
		for(String w:ws){
			System.out.println(w);
		}

		Properties.getProperties().setMode("en"); /* Remove comment to test with english*/
		
	/*	PunctuationRemover.remove(ModelBuilder.getDependencyTreeFileWithPunct(), ModelBuilder.getDependencyTreeFile());
		System.exit(1);*/
		ModelBuilder.restoreWordModel();
		ModelBuilder.makeSentence(ModelBuilder.getDataFile(), ModelBuilder.getSentenceFile());
	    ModelBuilder.ngramAllStatistics(ModelBuilder.getSentenceFile(), new int[]{2,3});
	    
	    
	   
		DependencyTree  tree = new DependencyTree();
		ModelBuilder.makeDependencyStatistics(ModelBuilder.getDependencyTreeFile(), tree,new int[]{2,3}, 3);
		//System.exit(1);
       // 
		Properties.getProperties().getNgramWordStats().convertToLongStatistics();
        Properties.getProperties().getNgramHeadWordStats().convertToLongStatistics();
        Properties.getProperties().getNgramRDsStats().convertToLongStatistics();
       System.out.println(Properties.getProperties().getNgramHeadWordStats().getWordCount());
       System.out.println(Properties.getProperties().getNgramRDsStats().getWordCount());

       System.out.println(Properties.getProperties().getSearchStats().getWordCount());
        
        int pivot = (int)(tree.getSentences().size() * 0.1);
		System.out.println("Train Sentence: " + pivot);
		pivot = 499;
		/*try {
			SmartCollectedData.collect("en/data/test.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		List<TreeNode> trainSentences= tree.getSentences().subList(0,pivot);
	      CommonUtils.setParams(new double[]{0,0.1,0.2,-0.1}, "params.txt");

		try {
			int ops = 0;
			double[] optimizedLambda;
			while(true){
				 List<List<Candidate>> candidates = LinearizationDependencyTree.linearizeAll(trainSentences, tree.getDependency(), tree.getDependencyRatio(), true);
					
					//CommonUtils.printList(Properties.getProperties().getNbestsSize());
					
					 MertCore mert = new MertCore("ZMERT_cfg.txt");
				      mert.run_MERT(); // optimize lambda[]!!!

				      mert.finish();
				      for(int i = 0; i < candidates.size(); i++){
				    	  for(Candidate c : candidates.get(i)){
					    		double score = 0.0;
					    		for(int j = 0; j < c.getFeats().length; j++){
					    			 score += mert.getLambda()[j] * c.getFeats()[j];

					    		}
					    		c.setAvgScore(score);
					    	}
					      int[] idx = LinearizationDependencyTree.sort(candidates.get(i));
					      Candidate c = candidates.get(i).get(idx[0]);
					      trainSentences.get(c.getIndexSentence()).getNodes().get(c.getIndexNode()).setHasOptimized(true);
					      System.out.println(c.getContent() +" : " +trainSentences.get(c.getIndexSentence()).getNodes().get(c.getIndexNode()).getRefWordSequence() );
					      trainSentences.get(c.getIndexSentence()).getNodes().get(c.getIndexNode()).setOptimizedSequence(c.getContent());
					      

				      }
				      
				      CommonUtils.setParams(mert.getLambda(), "params.txt");
				      ops = 0;
						for(TreeNode t: trainSentences){
							if(t.isHasOptimized()) ops++;
							else break;
						}
						System.out.println("(OP Lamda) : [" + mert.getLambda()[1] + "," +mert.getLambda()[2] + "," + mert.getLambda()[3] + "]");
						optimizedLambda = new double[]{mert.getLambda()[1],mert.getLambda()[2],mert.getLambda()[3]};
	                  if(ops == trainSentences.size()) break;
			
		}
			
			// test
			List<TreeNode> testSentences= new ArrayList<TreeNode>();
			for(int i = pivot +1; i < tree.getSentences().size(); i++){
				if(tree.getSentences().get(i).getNodes().size() <=30){
					testSentences.add(tree.getSentences().get(i));
				}
				if(testSentences.size() == 526) break;
			}
			
			Properties.getProperties().setLambda(optimizedLambda);
			 LinearizationDependencyTree.linearizeAll(testSentences, tree.getDependency(), tree.getDependencyRatio(), false);
			 
			 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("cand_database_test.txt"),"UTF-8"));
			BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ref_database_test.txt"),"UTF-8"));
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
			 for(TreeNode t: testSentences){
				 ref.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
				 src.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
				 cand.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");

				 bw.write(i+ "|||" + t.getOptimizedSequence());
				 bw.newLine();
				 bw1.write(i+ "\t" + t.getRefWordSequence() );
				 ref.append(StringEscapeUtils.escapeXml( t.getRefWordSequence()) + "\n\t\t</seg>\n\t</p>");
				 src.append(StringEscapeUtils.escapeXml(t.getRefWordSequence()) + "\n\t\t</seg>\n\t</p>");

				 cand.append(StringEscapeUtils.escapeXml(t.getOptimizedSequence()) + "\n\t\t</seg>\n\t</p>");
				 
				 bw1.newLine();
				 i++;
					//if(t.isHasOptimized()) ops++;
					//else break;
			}
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
	         
			 bw.flush();
			 bw1.flush();
			 bw.close();
			 bw1.close();
			 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode()+ "data/meta/src.xml"),"UTF-8"));
			 bw.write(src.toString());
			 bw.close();
			 //
			 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode()+  "data/meta/tst.xml"),"UTF-8"));
			 bw.write(cand.toString());
			 bw.close();
			 //
			 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode()+  "data/meta/ref.xml"),"UTF-8"));
			 bw.write(ref.toString());
			 bw.close();
			 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode()+  "data/results/bleu_result.txt",true),"UTF-8"));
			 bw.write("train:" + trainSentences.size() + " sentences\n");
			 bw.write("=> Optimizing Lambda: [" + Properties.getProperties().getLambda()[0] + "," + Properties.getProperties().getLambda()[1] + "," +  Properties.getProperties().getLambda()[2] +"]\n");
			 bw.write("test:" + testSentences.size() + " sentences\n");
			 bw.write("=> Bleu Summary:\n");
			
			 EvalTool.processArgsAndInitialize(args);
			 bw.write("Evaluating set of " + 1 + "'th candidate realization " + "...\n");
			 String candFileName = "cand_database_test.txt";
			 StringBuffer  bs = EvalTool.evaluateCands_nbest(candFileName,1);
			 bw.write(bs.toString());
			 bw.write("\n ");
			 bw.write("\n\t========================================================\n");

			 bw.close();
			 /* MertCore mert = new MertCore("ZMERT_cfg_test.txt");
		     mert.run_MERT(); // optimize lambda[]!!!

		     mert.finish();*/
		      
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
		

	}


}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                