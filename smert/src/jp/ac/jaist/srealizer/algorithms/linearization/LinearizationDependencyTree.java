package jp.ac.jaist.srealizer.algorithms.linearization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jp.ac.jaist.srealizer.algorithms.data.NgramProbabilityMethod;
import jp.ac.jaist.srealizer.data.model.Candidate;
import jp.ac.jaist.srealizer.data.model.DependencyTree;
import jp.ac.jaist.srealizer.data.model.NGramStatistics;
import jp.ac.jaist.srealizer.data.model.TreeNode;
import jp.ac.jaist.srealizer.properties.Properties;

public class LinearizationDependencyTree {
	private static final Logger log = Logger.getLogger( LinearizationDependencyTree.class.getName() );

	public static String getCandDBFile() {
		return 	"cand_database_.txt";
	}

	public static String getRefFile() {
		return 	"ref_database_.txt";
	}
	public static   List<List<Candidate>> linearizeAll(List<TreeNode> trainSentences, DependencyTree tree, Map<String,Integer> dependencies, Map<String, Double> dependencyRatio, boolean isOptimized) throws IOException{
		log.info("Linearization:..." + isOptimized);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getCandDBFile()),"UTF-8"));
		BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getRefFile()),"UTF-8"));

		for(int i =0; i < trainSentences.size(); i++){
			log.info(i + " isOptimized <=" + isOptimized + " Sentence: " + trainSentences.get(i).getSentence());
			 linearizeOne(trainSentences.get(i),tree, dependencies,dependencyRatio,bw, bw1,isOptimized);
		}
		bw.flush();
		bw.close();
		bw1.flush();
		bw1.close();
		int count = Properties.getProperties().getCounter();
		copy(getCandDBFile(), Properties.getProperties().getMode() + "data/views/" + count + ".cand");
		copy(getRefFile(), Properties.getProperties().getMode() + "data/views/" + count+ ".ref");

		return setNBest(getCandDBFile());
		
	}
	private static void copy(String source, String dest){
		try {
			BufferedReader  br = new BufferedReader(new InputStreamReader(new FileInputStream(source),"UTF-8"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest),"UTF-8"));
			String s = null;
			StringBuffer lines = new StringBuffer("");
			while((s = br.readLine()) != null){
				lines.append(s).append("\n");
			}
			br.close();
			bw.write(lines.toString().trim());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	private static   List<List<Candidate>> setNBest(String candDBFile) {
		  List<List<Candidate>> candidates = new ArrayList<List<Candidate>>();
	      String line =  null;
	      
	      String prevIndex = null;
	      try {
	    	int numOfCand = 0;
	    	Properties.getProperties().setNbestsSize(new ArrayList<Integer>());
	    	InputStream inStream_cands = new FileInputStream(new File(candDBFile));
		    BufferedReader inFile_cands = new BufferedReader(new InputStreamReader(inStream_cands, "utf8"));
		      
			while((line = inFile_cands.readLine()) != null){
				  String[] s = line.split("[|]+");
				  String curIndex = s[0].trim();
				  
				  if(prevIndex == null || !prevIndex.equals(curIndex)){
					  candidates.add(new ArrayList<Candidate>());
					  if(prevIndex != null){
						  Properties.getProperties().getNbestsSize().add(numOfCand);
					  }
					  numOfCand=0;
			        
				  }
				  Candidate candidate = new Candidate();
				  double[] feats = new double[3];
			  	  String[] nums = s[2].trim().split("[\\s]+");
			  	  double score = 0.0;
			  	 
			  	  String[] indicies = s[0].trim().split("-");
				  
			  	  candidate.setIndexSentence(Integer.parseInt(indicies[0]));
			  	  
			  	  if(indicies.length > 1) 
			  		  candidate.setIndexNode(Integer.parseInt(indicies[1]));
			  	  candidate.setId(s[0].trim());
			  	  candidate.setAvgScore(score);
			  	  candidate.setContent(s[1].trim());
			  	  candidate.setFeats(feats);
			  	  candidates.get(candidates.size()-1).add(candidate);
				 numOfCand++;
				  
				 prevIndex = curIndex;
			         
			     
			  }
		    if(numOfCand != 0)Properties.getProperties().getNbestsSize().add(numOfCand);
		    
			inFile_cands.close();
			
			
		 /*   for(int i = 0;i < candidates.size(); i++){
		    	double mean = 1, max = 0;
		    	for(Candidate c : candidates.get(i)){
		    		for(int j = 0; j < c.getFeats().length; j++){
		    			if(mean > c.getFeats()[j]) mean = c.getFeats()[j];
		    			if(max < c.getFeats()[j]) max = c.getFeats()[j];

		    		}
		    	}
		    	for(Candidate c : candidates.get(i)){
		    		for(int j = 0; j < c.getFeats().length; j++){
		    			 c.getFeats()[j]=  Math.log(c.getFeats()[j]);
		    			 

		    		}
		    	}
		    }*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return candidates;
	}
    
	private static void linearizeOne(TreeNode root,DependencyTree tree,  Map<String,Integer> dependencies, Map<String, Double> dependencyRatio, BufferedWriter bw, BufferedWriter bw1, boolean isOptimized) throws IOException {

	    if(root != null)
			for(TreeNode c : root.getChildren()){
				linearizeOne(c,tree, dependencies,dependencyRatio, bw, bw1,isOptimized);
			}
	  
	   // log.info(root.getName()  + ":" + root.isHasOptimized());
		if(root != null && !root.isHasOptimized() && root.getChildren().size() > 0){
			List<TreeNode> pres = new ArrayList<TreeNode>();
			List<TreeNode> pos = new ArrayList<TreeNode>();
			boolean isCollectedCandidates = true;
			for(TreeNode c : root.getChildren()){
				//System.out.print(c.getRD() + " ");
				if(c.getChildren().size() > 0 && !c.isHasOptimized()) isCollectedCandidates = false;
				if(dependencyRatio.get(c.getRD()) >= Properties.getProperties().getExpectedSept()) pres.add(c);
				else if(dependencyRatio.get(c.getRD()) <= 1- Properties.getProperties().getExpectedSept()){
					 pos.add(c);
				}else{
					long fowl = tree.getPosWordTypes().containsKey(  root.getType()+ "-" + c.getType()) ? tree.getPosWordTypes().get(  root.getType()+ "-" + c.getType()): 0;
					long precede = tree.getPreWordTypes().containsKey(c.getType() + "-" + root.getType()) ? tree.getPreWordTypes().get(c.getType() + "-" + root.getType()): 0;
					//double percents = fowl != 0? precede/(precede + fowl) : 1;
					if(fowl > precede ){
						pos.add(c);
					}else{
						pres.add(c);
					}
					
				}
				/*if(dependencies.containsKey(c.getRD()))
					if(	dependencies.get(c.getRD()) == 1){
						pres.add(c);
					}else pos.add(c);*/
			}
			//System.out.println("Root: " + root.getName() + ", pres: " + pres.size() + ", pos: " + pos.size());
			if(pres.size() +  pos.size() >= 1){
				if(isCollectedCandidates){
					//log.info("(REF) => " + root.getRefWordSequence());
					if(isOptimized){
					    bw1.write(root.getIndexSentence() + "-" + root.getIndex() +  "\t" + root.getRefWordSequence());
				        bw1.newLine();
				        bw1.flush();
					}
				}
				List<int[]> permsPres = new ArrayList<int[]>(), permsPoss = new ArrayList<int[]>();
				if(pres.size() > 0){
					permsPres = Permutation.QuickPerm(pres.size());
				}
				if(pos.size() > 0){
					permsPoss = Permutation.QuickPerm(pos.size());
				}
				List<Candidate> candidates = new ArrayList<Candidate>();
				for(int i =0; i < (permsPres.size() == 0 ? 1: permsPres.size()); i++){
					
					StringBuffer seqRDs = new StringBuffer(""), seqHeadWords = new StringBuffer(), seqWords = new StringBuffer();
					for(int j =0; j <pres.size(); j++){
						TreeNode node =  pres.get(permsPres.get(i)[j] -1);
						seqRDs.append(node.getRD()).append(" ");
						seqHeadWords.append(node.getName()).append(" ");
						if(isCollectedCandidates)seqWords.append(node.getOptimizedSequence()).append(" ");

						   
					}
					seqRDs.append(root.getRD()).append(" ");
					seqHeadWords.append(root.getName()).append(" ");
					if(isCollectedCandidates)seqWords.append(root.getOptimizedSequence()).append(" ");

					
						for(int k =0; k < (permsPoss.size() == 0 ? 1: permsPoss.size()); k++){
							StringBuffer appendTextRDs = new StringBuffer(""),appendTextHeadWords= new StringBuffer(""),appendWords = new StringBuffer("");
							for(int j =0; j <pos.size(); j++){
								TreeNode node =  pos.get(permsPoss.get(k)[j] -1);
								appendTextRDs.append(node.getRD()).append(" ");
								appendTextHeadWords.append(node.getName()).append(" ");
								if(isCollectedCandidates)appendWords.append(node.getOptimizedSequence()).append(" ");
								

							
							}
							String newCandTextRDs =  seqRDs.toString().trim() + " " +  appendTextRDs.toString().trim();
							String newCandHeadWords =  seqHeadWords.toString().trim() + " " +  appendTextHeadWords.toString().trim();
							//if(isCollectedCandidates) log.info("(R) : " +  root.getOptimizedSequence() + " (W) => " + seqWords.toString().trim() + " " +  appendWords.toString().trim());
							//System.out.println("RD => " + newCandTextRDs);
							//System.out.println("HEAD => " + newCandHeadWords);
							if(isCollectedCandidates){
								Candidate c =  ngramModel(root.getIndexSentence(),root.getIndex(),  seqWords.toString().trim() + " " +  appendWords.toString().trim(), newCandTextRDs, newCandHeadWords,bw,isOptimized);
								if(!isOptimized){
									candidates.add(c);
								}
							}
						}
					
				}
				if(!isOptimized){
					normalize(candidates);
					int[] idx = sort(candidates);
					root.setHasOptimized(true);
					root.setOptimizedSequence(candidates.get(idx[0]).getContent());
				}
			}
		}
		
	
	}
	private static void normalize(List<Candidate> candidates){
		   // min-max normalization;
	  
	    	double mean = 1, max = 0;
	    	for(Candidate c : candidates){
	    		for(int j = 0; j < c.getFeats().length; j++){
	    			if(mean > c.getFeats()[j]) mean = c.getFeats()[j];
	    			if(max < c.getFeats()[j]) max = c.getFeats()[j];

	    		}
	    	}
	    	for(Candidate c : candidates){
	    		double score = 0.0;
	    		for(int j = 0; j < c.getFeats().length; j++){
	    			if(max == mean)  c.getFeats()[j] = 0.9;
	    			else{
	    				c.getFeats()[j]=  0.1 + 0.8 * (c.getFeats()[j] -  mean)  /(max-mean);
	    			}
	    			 score += Properties.getProperties().getLambda()[j] * c.getFeats()[j];

	    		}
	    		c.setAvgScore(score);
	    	
	    }
	}
	private static Candidate ngramModel(long sentence, int indexNode, String newCandTextWord,
			String newCandTextRDs, String newCandHeadWords, BufferedWriter bw, boolean isOptimized) throws IOException {
		Candidate candidate = new Candidate();
		double feat1 = 0.0, feat2 = 0.0, feat3 = 0.0;
       if(Properties.getProperties().getSmooth() == Properties.getProperties().ADD_ONE){
    	   feat1 = ngramProbabilities(newCandTextRDs,Properties.getProperties().getNgramRDsStats().getStatistics(),Properties.getProperties().getGramRD());
   		   feat2 = ngramProbabilities(newCandTextWord,Properties.getProperties().getNgramWordStats().getStatistics(),Properties.getProperties().getGramWord());

   		   feat3 = ngramProbabilities(newCandHeadWords,Properties.getProperties().getNgramHeadWordStats().getStatistics(),Properties.getProperties().getGramHeadWord());
           
   		
       }else if(Properties.getProperties().getSmooth() == Properties.getProperties().KNEY){
    	   feat1 =  NgramProbabilityMethod.kneserNeySmoothing(
    		       newCandTextRDs.split("[\\s]+"),
    		    		   0.3, Properties.getProperties().getGramRD(), 
    		    		   Properties.getProperties().getNgramRDsStats().getStatistics(),
    		    		   Properties.getProperties().getNgramRDsStats().getPrecedingLongStatistics(),
    		    		   Properties.getProperties().getNgramRDsStats().getFollowLongStatistics(),
    		    		   Properties.getProperties().getNgramRDsStats().getGramCountLongStatistics());
    		        feat2 =  NgramProbabilityMethod.kneserNeySmoothing(
    		        	       newCandTextWord.split("[\\s]+"),
    		        	    		   0.3, Properties.getProperties().getGramWord(), 
    		        	    		   Properties.getProperties().getNgramWordStats().getStatistics(),
    		        	    		   Properties.getProperties().getNgramWordStats().getPrecedingLongStatistics(),
    		        	    		   Properties.getProperties().getNgramWordStats().getFollowLongStatistics(),
    		        	    		   Properties.getProperties().getNgramWordStats().getGramCountLongStatistics());
    		        feat3 =  NgramProbabilityMethod.kneserNeySmoothing(
    		        	       newCandHeadWords.split("[\\s]+"),
    		        	    		   0.3, Properties.getProperties().getGramHeadWord(), 
    		        	    		   Properties.getProperties().getNgramHeadWordStats().getStatistics(),
    		        	    		   Properties.getProperties().getNgramHeadWordStats().getPrecedingLongStatistics(),
    		        	    		   Properties.getProperties().getNgramHeadWordStats().getFollowLongStatistics(),
    		        	    		   Properties.getProperties().getNgramHeadWordStats().getGramCountLongStatistics());
       }else{
    	// Kylm
   		feat1 = probs(newCandTextRDs,Properties.getProperties().getGramRD(), Properties.getProperties().getNgramRDsStats());
   		feat2 = probs(newCandTextWord,Properties.getProperties().getGramWord(), Properties.getProperties().getNgramWordStats());
   		feat3 = probs(newCandHeadWords,Properties.getProperties().getGramHeadWord(), Properties.getProperties().getNgramHeadWordStats());

   		
       }
		
		//  log.info(feat1 + ": " +  feat2 + ":" + feat3);
      /*  
        */
		if(isOptimized){
	        bw.write(sentence + "-" + indexNode +  " ||| " + newCandTextWord + " ||| "  + feat1 + " " + feat2 + " " + feat3);
	        bw.newLine();
	        bw.flush();
		}else{
			 candidate.setFeats(new double[]{feat1,feat2,feat3});
		     candidate.setContent(newCandTextWord);
		}
       
		return candidate;
	}
	private static double probs(String text,int gram, NGramStatistics nGramStatistics){
		double d = 1.0;
		String[] words = text.split("[\\s]+");
		if(words.length < gram){
			gram = words.length;
		}
		for(int i = gram-1; i < words.length; i++){
			d *= probOne(words, i, gram, nGramStatistics);
		}
		
		return d;
	}
    private static double probOne(String[] words,int  i,int gram, NGramStatistics nGramStatistics){
    	if(gram == 1){
    		/*if(!nGramStatistics.getProps().containsKey(words[i]))
    			System.out.println(words[i].length());*/
    		if(nGramStatistics.getProps().containsKey(words[i])) 
    			return /*Math.pow(10,*/(nGramStatistics.getProps().get(words[i]))/*)*/;
    		else return 1;
    	}
    	String wbk = concat(words, i-gram+1,i-1);
    	return probOne(words, i, gram-1, nGramStatistics) *((nGramStatistics.getBackoffWeight().containsKey(wbk)) ? /*Math.pow(10,*/nGramStatistics.getBackoffWeight().get(wbk)/*)*/: 1.0);
    	
    			
			
		
    	
    }
	private static String concat(String[] words, int i, int j){
		String b= "";
		int start = i < 0 ? 0:i;
		for(int k = start;k <= j; k++){
			b += words[k] + " ";
		}
		return b.trim();
		
	}
    private static double power10(double a){
    	return Math.pow(10,a);
    }
	/*private*/ public  static double ngramProbabilities(String text,
			Map<String, Long> ngramStats, int gram) {
		String[] words = text.split("[\\s]");
		StringBuffer w = new StringBuffer();
		double prop = 1.0;
		boolean hasNull = false;
		if(words.length < gram){
			
			gram = words.length;
		}
		
		for(int i =gram -1; i < words.length; i++){ // Ignore start and end symbol.
			 hasNull = false;
			/*if(i < words.length) w = new StringBuffer(words[i]);
			else{
				w = new StringBuffer("NULL");
				hasNull = true;
			}*/
			 w = new StringBuffer("");
			double num = 0, den = 0;	
			System.out.print("");
			for(int j = 0; j < gram;  j++){
				
                w.append(" " + (i - gram + 1 +j >= 0 && i - gram + 1 +j < words.length? words[i - gram + 1 +j] : "NULL"));
               
                if(i - gram + 1 +j < 0 ||  i - gram + 1 +j >= words.length){
                	hasNull = true;
                }
				String w1 = w.toString().trim();
                if( j + 1 ==  gram -1  && !hasNull){
    				//System.out.print(w1 + " => ");

                	den = (ngramStats.containsKey(w1)? ngramStats.get(w1): 0) + ngramStats.size();
                }
                if( j + 1 ==  gram  && !hasNull){
    				//System.out.println(w1);

                	num = (ngramStats.containsKey(w1)? ngramStats.get(w1): 0) + 1;
                }
				
			}
			if(!hasNull){
				//if(num == 0 || den == 0) prop *= 0.00001;
				//else
				if(gram == 1) den= ngramStats.size();
				prop *= num/den;
			}
			
		}
		if(prop == 1.0){
			prop = ((ngramStats.containsKey(text)? ngramStats.get(text): 0) + 1)/ngramStats.size();
		}
		return prop;
	}

	public static int[] sort(List<Candidate> candidates) {

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
	
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      