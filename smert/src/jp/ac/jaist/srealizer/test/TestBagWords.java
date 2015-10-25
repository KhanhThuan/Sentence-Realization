package jp.ac.jaist.srealizer.test;
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















import jp.ac.jaist.realizer.data.builder.ModelBuilder;
import jp.ac.jaist.realizer.evaluation.EvalTool;
import jp.ac.jaist.srealizer.algorithms.linearization.LinearizationDependencyTree;
import jp.ac.jaist.srealizer.algorithms.mert.MertCore;
import jp.ac.jaist.srealizer.data.model.Candidate;
import jp.ac.jaist.srealizer.data.model.DependencyTree;
import jp.ac.jaist.srealizer.data.model.TreeNode;
import jp.ac.jaist.srealizer.properties.Properties;
import jp.ac.jaist.srealizer.utils.CommonUtils;

import org.apache.commons.lang.StringEscapeUtils;



public class TestBagWords {

	public static void main(String[] args) {
		
		String line= "Versions of Stanford Dependencies have also been developed by outside groups for a number of other languages. Two prominent examples are Finnish (the Turku Dependency Treebank) and Persian (the Uppsala Persian Dependency Treebank). There is now a multi-site effort to produce dependency treebanks over a broad range of languages adopting a compatible dependency taxonomy. More details about this Universal Dependency Treebank can be found in the LREC 2014 paper mentioned above, in the current treebank release, and in new documentation.";
		String[] ws = line.split("([.][\\s\\t\n]+|[?]|!|[.$])");
		for(String w:ws){
			System.out.println(w);
		}

		//Properties.getProperties().setMode("en"); /* Remove comment to test with english*/
		int[] vals = new int[]{ Properties.KNEY,Properties.ADD_ONE, Properties.KYLM};
		Map<Integer,Integer> nextRun= new HashMap<Integer, Integer>();
		int run = 0;
		for(int type : vals){
			run++;
			Properties.getProperties().setSmooth(type);
			boolean useKylm = Properties.getProperties().getSmooth() == Properties.KYLM;	
		/*	PunctuationRemover.remove(ModelBuilder.getDependencyTreeFileWithPunct(), ModelBuilder.getDependencyTreeFile());
			System.exit(1);*/
		//	ModelBuilder.restoreWordModel();
			//ModelBuilder.makeSentence(ModelBuilder.getDataFile(), ModelBuilder.getSentenceFile());
		   // ModelBuilder.ngramAllStatistics(ModelBuilder.getSentenceFile(), new int[]{2,3});
		    
		    
		   
			DependencyTree  tree = new DependencyTree();
		    System.out.println("Test Cumulative: " + Properties.getProperties().getNgramWordStats().getStatistics().size());

			ModelBuilder.makeDependencyStatistics(ModelBuilder.getDependencyTreeFile(), tree,new int[]{2,3}, 3);
			/*System.out.println("-----Pre Type-------");
			for(String t : tree.getPreWordTypes().keySet()){
		    	System.out.println(t + ": " + tree.getPreWordTypes().get(t));
		    }
			System.out.println("-----Pos Type-------");
			for(String t : tree.getPosWordTypes().keySet()){
		    	System.out.println(t + ": " + tree.getPosWordTypes().get(t));
		    }
			System.out.println("--------");*/
			//System.exit(1);
			Properties.getProperties().setExpectedSept(0.7);
			
			if(useKylm){
				ModelBuilder.getKylmStatistic();
			}else{
				Properties.getProperties().getNgramWordStats().convertToLongStatistics();
		        Properties.getProperties().getNgramHeadWordStats().convertToLongStatistics();
		        Properties.getProperties().getNgramRDsStats().convertToLongStatistics();
			}
	
	       // 
			
	       System.out.println(Properties.getProperties().getNgramHeadWordStats().getWordCount());
	       System.out.println(Properties.getProperties().getNgramRDsStats().getWordCount());
	      
	       System.out.println(Properties.getProperties().getSearchStats().getWordCount());
	       Map<Integer,Integer> indices = new HashMap<Integer, Integer>();
	       List<TreeNode> corpara = new ArrayList<TreeNode>();
	        for(int i =0; i < tree.getSentences().size(); i++){
	        	indices.put((int) tree.getSentences().get(i).getIndexSentence(), i);
	        	if(run ==1){
	        		corpara.add(tree.getSentences().get(i));
	        	}
	        }
	        System.out.println("corpara: " + corpara.size());
	        if(run > 1){
	        	for(int i =0; i < tree.getSentences().size(); i++){
		        	
		        		corpara.add(tree.getSentences().get(indices.get(nextRun.get(i))));
		        	
		        }
	        	System.out.println("next => ");
	        	 for(int i= 0; i < corpara.size(); i++){
		    		 System.out.print(corpara.get(i).getIndexSentence() + ", ");
	        	 }
		    	 System.out.println();

	        }
	     if(run == 1){
	    	Collections.shuffle(corpara); /* Remove the comment to test randomly */
	    	System.out.println("Run Indexing => ");
	    	 for(int i= 0; i < corpara.size(); i++){
	    		 System.out.print(corpara.get(i).getIndexSentence() + ", ");
	    		 nextRun.put(i,(int)corpara.get(i).getIndexSentence());
		     }
	    	 System.out.println();
	     }
	     
	      //  int pivot = (int)(corpara.size() * 0.1);
			int pivot = 499; // Number of training sentences;
			System.out.println("Train Sentence: " + pivot);
			int nTest = 526;
			List<TreeNode> trainSentences= corpara.subList(0,pivot);
			for(int i =0; i < trainSentences.size(); i++){
	        	indices.put((int) trainSentences.get(i).getIndexSentence(), i);
	        	
	        }
			double[] intitialLambda = new double[]{0,0.1,0.2,-0.1};
		      CommonUtils.setParams(intitialLambda, "params.txt");
		      CommonUtils.setSDconfig("SDecoder_cfg.txt","cand_database_.txt", intitialLambda);
		      double[] optimizedLambda = null;
			try {
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
				int incsSize = 10;
				for(int tn = 0;tn < 5; tn++){
					DependencyTree testTree = new DependencyTree();
					 ModelBuilder.makeTestSentences("data/dependency-tree/test/test_tree_" + tn + ".DEP.CONLL", testTree,new int[]{2,3}, 3);
					List<TreeNode> testSentences=testTree.getSentences();
				//	nTest += incsSize;
					Properties.getProperties().setLambda(/*optimizedLambda*/ lamdas);
					try{
						LinearizationDependencyTree.linearizeAll(testSentences,tree, tree.getDependency(), tree.getDependencyRatio(), false);
					}catch(Exception e){
						e.printStackTrace();
					}
					 
					 String candFileName =  "data/dependency-tree/test/cand_database_test_" +tn + ".txt";
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
		    		BufferedReader	br = new BufferedReader(new InputStreamReader(new FileInputStream("data/dependency-tree/test/ref_" + tn + ".txt" ),"UTF-8"));

					 for(TreeNode t: testSentences){
					
						 ref.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
						 src.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
						 cand.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
		
						 bw.write(i+ "|||" + t.getOptimizedSequence());
						 bw.newLine();
						 String[] s = br.readLine().split("\t");
						 ref.append(StringEscapeUtils.escapeXml(s[1].trim()) + "\n\t\t</seg>\n\t</p>");
						 src.append(StringEscapeUtils.escapeXml(s[1].trim()) + "\n\t\t</seg>\n\t</p>");
		
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
					 bw.write("Bag of Words Testing: " + tn + "\n");
					 bw.write("train:" + trainSentences.size() + " sentences\n");
					 bw.write("NGRAM SMOOTH Method: " + Properties.getProperties().getSmoothSTR() + "\n");
					 bw.write("Pre/Post sept: " + Properties.getProperties().getExpectedSept() + "\n");
					 bw.write("Mean Optimal: " + byMean + "\n");

					 bw.write("=> Optimizing Lambda: [" + Properties.getProperties().getLambda()[0] + "," + Properties.getProperties().getLambda()[1] + "," +  Properties.getProperties().getLambda()[2] +"]\n");
					 bw.write("test:" + testSentences.size() + " sentences\n");
					 bw.write("=> Bleu Summary:\n");
					String refFileName = "data/dependency-tree/test/ref_" + tn + ".txt";
					 EvalTool.processArgsAndInitialize(candFileName,refFileName);
					 EvalTool.setCandFileName(candFileName);
					 EvalTool.setRefFileName(refFileName);
					 bw.write("Evaluating set of " + 1 + "'th candidate realization " + "...\n");
					
					 StringBuffer  bs = EvalTool.evaluateCands_nbest(candFileName,1);
					 bw.write(bs.toString());
					 bw.write("\n ");
					 bw.write("\n\t========================================================\n");
		
					 bw.close();
					 System.out.println(" See data/results/bleu_results.txt");
					// return;
				}
			   // while(nTest < 1000){
			    	// test
					
			 //   }
				
				 /* MertCore mert = new MertCore("ZMERT_cfg_test.txt");
			     mert.run_MERT(); // optimize lambda[]!!!
	
			     mert.finish();*/
			      
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}


}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                