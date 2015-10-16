package jp.ac.jaist.realizer.data.builder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.jaist.srealizer.data.model.DependencyTree;
import jp.ac.jaist.srealizer.data.model.TreeNode;
import jp.ac.jaist.srealizer.properties.Properties;


public class ModelBuilder {
	private static String dataFile = "data/words-data.in";
	private static String sentenceFile = "data/builder/sentences.txt";
	private static String dependencyTreeFile ="data/dependency-tree/dependency-tree-no-punct.txt";
	private static String dependencyTreeFileWithPunct ="data/dependency-tree/dependency-tree.txt";
	private static 	String configDataFile = "data/configData.txt";
	private static String ngramStatsFile = "data/builder/ngram-stats.txt";
	
	public static String getDataFile() {
		return Properties.getProperties().getMode() + dataFile;
	}
	public static String getSentenceFile() {
		return  Properties.getProperties().getMode() + sentenceFile;
	}
	public static String getDependencyTreeFile() {
		return  Properties.getProperties().getMode() + dependencyTreeFile;
	}
	public static String getDependencyTreeFileWithPunct() {
		return dependencyTreeFileWithPunct;
	}
	public static void setDependencyTreeFileWithPunct(
			String dependencyTreeFileWithPunct) {
		ModelBuilder.dependencyTreeFileWithPunct = Properties.getProperties().getMode() +  dependencyTreeFileWithPunct;
	}

	public static String getNgramStatsFile() {
		return Properties.getProperties().getMode() + ngramStatsFile;
	}
	private static  String configBuilderFile ="data/builder/config.txt";
	public static void restoreWordModel(){
		try {
			BufferedWriter bw = null;
			String[] clearFiles = {Properties.getProperties().getMode() + sentenceFile, Properties.getProperties().getMode() + configDataFile,Properties.getProperties().getMode() + ngramStatsFile, Properties.getProperties().getMode() + configBuilderFile};
			for(String file :  clearFiles){
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file) ,"UTF-8"));
				bw.write("");
				bw.flush();
				bw.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void makeSentence(String dataFile,String sentenceFile){
		try {
			BufferedReader 	br = null;
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sentenceFile, true) ,"UTF-8"));

			String line = null, fig = null;
			long counter = 0;
			long proceededLines = 0;
			try {
				br = new BufferedReader(new FileReader(Properties.getProperties().getMode() + configDataFile));
				// line 1: number of proceeded sentences
				// line 2: support types of n-grams;
				fig = br.readLine().trim();
				proceededLines =Long.parseLong(fig);
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile),"UTF-8"));
			while((line = br.readLine()) != null){
				if(++counter > proceededLines){
					String[] sens = line.split("([.][\\s\\t\n]+|[?]|!|[.$])");
					for(String s : sens){
						if(s.trim().length() > 0){
							String[] words = s.split("[\\s\\t]");
							String w1 = "";
							List<String> wl = new ArrayList<String>();
							for(String w : words){
							//	System.out.println(w);
								w1 = "";
								for(int i = 0; i < w.length(); i++){
									if(w.charAt(i) == ',' || w.charAt(i) == ';' || w.charAt(i) == ':'
														  || w.charAt(i) == '(' || w.charAt(i) == ')' || w.charAt(i) == '\''
														  || w.charAt(i) == '"' || w.charAt(i) == '.'){
									   if((w.charAt(i) == ',' || w.charAt(i) == '.') && i +1 < w.length() && w.charAt(i+1) >='0' && w.charAt(i) <= '9')
										   w1 += w.charAt(i);
									   else{
										   if(w1.trim().length() > 0)
												wl.add(w1);
										   w1 ="";
									   }
										
									}else
										w1 += w.charAt(i);
								}
								if(w1.trim().length() > 0)	wl.add(w1);
							}
						
							String sentence = "";
							for(String w : wl){
									if(w.trim().length() > 0)sentence += w.toLowerCase() + " ";
							}
							if(sentence.trim().length() > 2 ) {
								bw.write(sentence.trim());
							//	System.out.println("sen: "   + sentence);
								bw.newLine();
							}
							bw.flush();
						}
						
					}
					
				}
				
					
			}
			br.close();
			bw.close();
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode() + configDataFile) ,"UTF-8"));
			System.out.println(counter);
			bw.write(counter + "");
			bw.newLine();
			bw.flush();
			bw.close();
			} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public static void ngramAllStatistics(String inputFileSentences, int[] grams){
		long lineSentences = 0;
		int maxgram = 0;
		long wordsCount = 0;
		boolean newN = false;
		BufferedReader br;
		BufferedWriter bw;
		String s = null;
		String[] ngrams = null;
		Map<String,Integer> ngramProcesses = new TreeMap<String, Integer>();
		
		try {
			br = new BufferedReader(new FileReader(Properties.getProperties().getMode() + configBuilderFile));
			// line 1: number of proceeded sentences
			// line 2: support types of n-grams;
			s = br.readLine().trim();
			lineSentences =Long.parseLong(s);
			s = br.readLine().trim();
			ngrams = s.split("\t");
			s = br.readLine().trim();
			wordsCount = Long.parseLong(s);
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if(ngrams != null){
			for(String n : ngrams){
				ngramProcesses.put(n, 0); // 0  for old
				if(Integer.parseInt(n) -1 > 0) ngramProcesses.put((Integer.parseInt(n) -1) + "" , 0); // 0  for old
	
				if(Integer.parseInt(n) > maxgram) maxgram = Integer.parseInt(n);  
			}
		}
		for(int d : grams){
			if(!ngramProcesses.containsKey("" + d)){
				ngramProcesses.put(d+"", 1); // 1  for new
				if(d -1 > 0 && !ngramProcesses.containsKey("" + (d-1))) ngramProcesses.put((d-1)+"", 1); // 1  for new
				newN = true;
				if(d > maxgram) maxgram = d;
			}
		}
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(Properties.getProperties().getMode() + ngramStatsFile),"UTF-8"));
		    s =  null;
			while((s = br.readLine()) != null ){
			      String[] stats = s.split("[\\t]");
			      for(String stat : stats){
			    	  String[] vals = stat.split(":");
			    	 
			    	  Properties.getProperties().getNgramWordStats().getStatistics().put(vals[0].trim(),Long.parseLong(vals[1].trim()));
	                	
			      }
			}
			br.close();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileSentences),"UTF-8"));
		    s =  null;
			long counter = 0;
			while((s = br.readLine()) != null ){
				if(s.trim().length() > 0){
					  counter++;
				      if(counter > lineSentences || newN ){
					      String[] words = s.split("[\\s\\t]");
					      if(counter > lineSentences) wordsCount += words.length;
					      ngramASentence(words, maxgram, ngramProcesses, counter > lineSentences);
				      }
				}
			    
			}
			lineSentences = counter;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode() + ngramStatsFile) ,"UTF-8"));
			int  i  = 0;
			String line ="";
			for(String t : Properties.getProperties().getNgramWordStats().getStatistics().keySet()){
				line += t +":" + Properties.getProperties().getNgramWordStats().getStatistics().get(t) + "\t";
				if(++i % 10 == 0) {
					bw.write(line.trim());
					line = "";
					bw.newLine();
				}
				bw.flush();

			}
			if(line.trim().length() > 0) bw.write(line.trim());
			bw.flush();
			bw.close();
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Properties.getProperties().getMode() + configBuilderFile) ,"UTF-8"));
			bw.write(lineSentences + "");
			bw.newLine();
			String ngramsStr ="";
			for(String n: ngramProcesses.keySet()){
				ngramsStr += n + "\t";
			}
			bw.write(ngramsStr.trim());
			bw.newLine();
			bw.write(wordsCount + "");
            bw.newLine();
            bw.flush();
            bw.close();
            outputGramCountStatistics(Properties.getProperties().getMode() + "data/builder/n-gram-count.txt",Properties.getProperties().getNgramWordStats().getGramCountStatistics());
            
            outputCountStatistics(Properties.getProperties().getMode() + "data/builder/n-gram-preceding-count.txt",Properties.getProperties().getNgramWordStats().getPrecedingStatistics());
            outputCountStatistics(Properties.getProperties().getMode() + "data/builder/n-gram-follow-count.txt",Properties.getProperties().getNgramWordStats().getFollowStatistics());
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	private static void ngramASentence(String[] words,int maxgram, Map<String, Integer> ngramProcesses, boolean runAll) {
		
		String w = "";
		for(int i =0; i <= words.length; i++){
			if(i < words.length) w = words[i];
			else w = "NULL"
					+ "";
					
			for(int j = 0; j < maxgram;  j++){
				
				w = w.trim();
				if(runAll){
					// counting gram types
					if(i - j >= 0){
						if(!Properties.getProperties().getNgramWordStats().getGramCountStatistics().containsKey(j+1)){
							Properties.getProperties().getNgramWordStats().getGramCountStatistics().put(j+1, new TreeSet<String>());
						}
						Properties.getProperties().getNgramWordStats().getGramCountStatistics().get(j+1).add(w);

					}
				// N1+(*,w)
                    if(!Properties.getProperties().getNgramWordStats().getPrecedingStatistics().containsKey(w) ){
                   
                    	Properties.getProperties().getNgramWordStats().getPrecedingStatistics().put(w, new TreeSet<String>());
                    }
            		Properties.getProperties().getNgramWordStats().getPrecedingStatistics().get(w).add(i - 1 - j >= 0 ?  words[i- 1-j].toLowerCase() :  "NULL");

               // N1+(w,*)
	                if(!Properties.getProperties().getNgramWordStats().getFollowStatistics().containsKey(w) ){
	                    	Properties.getProperties().getNgramWordStats().getFollowStatistics().put(w, new TreeSet<String>());
	                }
	                if(i != words.length) 
                		Properties.getProperties().getNgramWordStats().getFollowStatistics().get(w).add( i +1 < words.length ? words[i+1].toLowerCase() : "NULL");
	                else{
                		Properties.getProperties().getNgramWordStats().getFollowStatistics().get(w).add( words[0].toLowerCase());

	                }
				}
		      if((runAll && ngramProcesses.containsKey(""+(j + 1))) || (ngramProcesses.containsKey(""+(j + 1)) && ngramProcesses.get(""+(j + 1)) == 1)){
                	//System.out.println(w);
                	if(Properties.getProperties().getNgramWordStats().getStatistics().containsKey(w)){
                		Properties.getProperties().getNgramWordStats().getStatistics().put(w,Properties.getProperties().getNgramWordStats().getStatistics().get(w) + 1 );
                	}else{
                		Properties.getProperties().getNgramWordStats().getStatistics().put(w,1L);
                	}
                }
                w = (i - 1 - j >= 0 ? words[i- 1-j] : "NULL") + " " + w;
				
			}
		}
	}
	public static void makeDependencyStatistics(String dependencyTreeFile, DependencyTree tree, int[] gramsA, int headMaxGram){
		System.out.println("Making Dependency Statistics ....");
        BufferedReader br= null;
        BufferedWriter  bw = null;
        String headWordStatisticsFile = Properties.getProperties().getMode() + "data/dependency-tree/train/headwords-statistics.txt";
        String refSubtreeFile = Properties.getProperties().getMode() + "data/dependency-tree/train/ref-subtree.txt";

        String rdStatisticsFile = Properties.getProperties().getMode() + "data/dependency-tree/train/rd-statistics.txt";
        String dependencyStatisticsFile= Properties.getProperties().getMode() + "data/dependency-tree/train/dependency-statistics.txt";
        try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(dependencyTreeFile),"UTF-8"));
			List<String[]> propsStack = new ArrayList<String[]>();
			String s = null, rdName = null;
			int maxGram = 0;
			Map<Integer, Integer> grams = new HashMap<Integer, Integer>();
			for(int n : gramsA){
				if(maxGram < n) maxGram = n;
				grams.put(n,1);
				if(n-1 > 0)grams.put(n-1,1);
			}
			long numberSentences = 0, numberOfWords = 0;
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(refSubtreeFile) ,"UTF-8"));
			while((s = br.readLine()) != null){
				
				//System.out.println(s.equals("\n"));
				if(s.trim().length() == 0){
					//System.out.println(propsStack.size());
					if( propsStack.size() > 40 ){
						propsStack = new ArrayList<String[]>();

					}else	if(propsStack.size() > 0){
						numberOfWords += propsStack.size();
						String[] words = new String[propsStack.size()];
						numberSentences++;
						String rd = "", w ="";
						StringBuffer wholeSentence = new StringBuffer("");
						Map<Integer,TreeNode> nodeMaps = new HashMap<Integer, TreeNode>();
						TreeNode  root = null;
						int rootIndex = 0;
                        for(int i = 0; i <= propsStack.size(); i++){
                			// also word statistics
							/***************************************************/
                        	String[] props = null;
							if(i < propsStack.size()){
								props = propsStack.get(i);
								w = props[0].toLowerCase();
							}
							else w = "NULL";
							for(int j = 0; j < Properties.getProperties().getGramWord();  j++){
								
								w = w.trim();
								// counting gram types
									if(i - j >= 0){
										if(!Properties.getProperties().getSearchStats().getGramCountStatistics().containsKey(j+1)){
											Properties.getProperties().getSearchStats().getGramCountStatistics().put(j+1, new TreeSet<String>());
										}
										Properties.getProperties().getSearchStats().getGramCountStatistics().get(j+1).add(w);

									}
								// N1+(*,w)
				                    if(!Properties.getProperties().getSearchStats().getPrecedingStatistics().containsKey(w) ){
				                    	//if(i - 1 - j >= 0)
				                   
				                    	Properties.getProperties().getSearchStats().getPrecedingStatistics().put(w, new TreeSet<String>());
				                    }
		                    		Properties.getProperties().getSearchStats().getPrecedingStatistics().get(w).add(i - 1 - j >= 0 ? propsStack.get(i- 1-j)[0].toLowerCase() :  "NULL");

				               // N1+(w,*)
					                if(!Properties.getProperties().getSearchStats().getFollowStatistics().containsKey(w) ){
					                    	Properties.getProperties().getSearchStats().getFollowStatistics().put(w, new TreeSet<String>());
					                }
					            	if(i != propsStack.size()) 
			                    		Properties.getProperties().getSearchStats().getFollowStatistics().get(w).add( i +1 < propsStack.size() ? propsStack.get(i+1)[0].toLowerCase() : "NULL");
					            	else{
			                    		Properties.getProperties().getSearchStats().getFollowStatistics().get(w).add( propsStack.get(0)[0].toLowerCase());

					            	}
					            	
					           // N-gram count
				                	if(Properties.getProperties().getNgramWordStats().getStatistics().containsKey(w)){
				                		Properties.getProperties().getNgramWordStats().getStatistics().put(w,Properties.getProperties().getNgramWordStats().getStatistics().get(w) + 1 );
				                	}else{
				                		Properties.getProperties().getNgramWordStats().getStatistics().put(w,1L);
				                	}
				                /// 
				                	if(Properties.getProperties().getSearchStats().getStatistics().containsKey(w)){
				                		Properties.getProperties().getSearchStats().getStatistics().put(w,Properties.getProperties().getSearchStats().getStatistics().get(w) + 1 );
				                	}else{
				                		Properties.getProperties().getSearchStats().getStatistics().put(w,1L);
				                	}
				                w = (i - 1 - j >= 0 ? propsStack.get(i- 1-j)[0].toLowerCase() : "NULL") + " " + w;
								
							}
							/***************************************************/

							// NGram Statistics For Relation Dependency.
							if(i < propsStack.size()) rd = props[3].toUpperCase();
							else rd = "null";
									
							for(int j = 0; j < maxGram;  j++){
								
								rd = rd.trim();
								
								// counting gram types
								if(i - j >= 0){
									
									if(!Properties.getProperties().getNgramRDsStats().getGramCountStatistics().containsKey(j+1)){
										Properties.getProperties().getNgramRDsStats().getGramCountStatistics().put(j+1, new TreeSet<String>());
									}
									Properties.getProperties().getNgramRDsStats().getGramCountStatistics().get(j+1).add(rd);

								}
							// N1+(*,w)
			                    if(!Properties.getProperties().getNgramRDsStats().getPrecedingStatistics().containsKey(rd) ){
			                    	//if(i - 1 - j >= 0)
			                   
			                    	Properties.getProperties().getNgramRDsStats().getPrecedingStatistics().put(rd, new TreeSet<String>());
			                    }
	                    		Properties.getProperties().getNgramRDsStats().getPrecedingStatistics().get(rd).add(i - 1 - j >= 0 ? propsStack.get(i- 1-j)[3].toUpperCase():  "null");

			               // N1+(w,*)
				                if(!Properties.getProperties().getNgramRDsStats().getFollowStatistics().containsKey(rd) ){
				                    	Properties.getProperties().getNgramRDsStats().getFollowStatistics().put(rd, new TreeSet<String>());
				                }
				            	if(i != propsStack.size()) 
		                    		Properties.getProperties().getNgramRDsStats().getFollowStatistics().get(rd).add( i +1 < propsStack.size() ? propsStack.get(i+1)[3].toUpperCase() : "null");
				            	else{
		                    		Properties.getProperties().getNgramRDsStats().getFollowStatistics().get(rd).add( propsStack.get(0)[3].toUpperCase());

				            	}
								// n-gram count;
				                if(grams.containsKey(j + 1) && grams.get(j + 1) == 1){
				                	//System.out.println("rd " + rd);
				                	if(tree.getNgramRDStatistics().containsKey(rd)){
				                		tree.getNgramRDStatistics().put(rd,tree.getNgramRDStatistics().get(rd) + 1 );
				                	}else{
				                		tree.getNgramRDStatistics().put(rd,1L);
				                	}
				                }
				                rd = (i - 1 - j >= 0 ? propsStack.get(i- 1-j)[3].toUpperCase() : "null") + " " + rd;
								
							}
							
                        }
						for(int i =0; i < propsStack.size(); i++){
							
							String[] props = propsStack.get(i);
							words[i] = props[0].toLowerCase();
							wholeSentence.append(props[0].toLowerCase()).append(" ");
							// HeadWords
							int parentIndex = Integer.parseInt(props[2]); // +1//
							TreeNode  node = null, parentNode = null;
							if(nodeMaps.containsKey(i+1)) node = nodeMaps.get(i+1);
							else {
								node =	new TreeNode(i+1,parentIndex, props[0].toLowerCase(), props[3].toUpperCase());
								node.setIndexSentence(numberSentences -1);

								nodeMaps.put(i+1, node);
							}
							if(parentIndex == 0) rootIndex = i+1;
							if(nodeMaps.containsKey(parentIndex)) parentNode = nodeMaps.get(parentIndex);
							else if(parentIndex != 0) {
								String[] propsParent = propsStack.get(parentIndex-1);
								parentNode =	new TreeNode(parentIndex,Integer.parseInt(propsParent[2]), propsParent[0].toLowerCase(), propsParent[3].toUpperCase());
								parentNode.setIndexSentence(numberSentences -1);

								nodeMaps.put(parentIndex, parentNode);
							}
							if(parentIndex != 0){
								parentNode.getChildren().add(node);
								if(i + 1 < parentIndex){
									parentNode.addPres(node);
								}else{
									parentNode.addPoss(node);

								}
							}
				
							// Pre-dependent and Post-dependent 
							rdName = props[3].toUpperCase();
							if(i + 1< Integer.parseInt(props[2].trim())){
								if(tree.getPreDenpents().containsKey(rdName)){
									tree.getPreDenpents().put(rdName, tree.getPreDenpents().get(rdName) + 1);
								}else{
									tree.getPreDenpents().put(rdName, 1L);

								}
							}else{
								if(tree.getPostDenpents().containsKey(rdName)){
									tree.getPostDenpents().put(rdName, tree.getPostDenpents().get(rdName) + 1);
								}else{
									tree.getPostDenpents().put(rdName, 1L);

								}
							}
							
							
							
						}
						root = nodeMaps.get(rootIndex);
						root.setNodes(nodeMaps);
						root.setSentence(wholeSentence.toString());
						tree.addSentence(root);
                        
					/*	for(int k : nodeMaps.keySet()){
							System.out.println(nodeMaps.get(k).getName() + ": " + nodeMaps.get(k).getPres().size() +  nodeMaps.get(k).getPoss().size() );
						}*/
						trainHeadWords(tree.getHeadWords(), root, headMaxGram,bw);
						bw.newLine();
						bw.flush();
						propsStack = new ArrayList<String[]>();
						
					}
				}else{
					String[] props = s.split("\t");
					// props[0] : word, props[1] : tag, props[2] : parent, props[3] : Relation Dependency (RD)
					//if()
				    propsStack.add(props);
					
				}
				
			}
			tree.setNumberSentences(numberSentences);
			bw.close();
			Properties.getProperties().getSearchStats().setWordCount(numberOfWords);
			Properties.getProperties().getNgramRDsStats().setWordCount(numberOfWords);

			Properties.getProperties().getNgramHeadWordStats().setStatistics(tree.getHeadWords());
			Properties.getProperties().getNgramRDsStats().setStatistics( tree.getNgramRDStatistics());
			outputStatistics(getNgramStatsFile(), Properties.getProperties().getNgramWordStats().getStatistics());
			outputStatistics(headWordStatisticsFile, tree.getHeadWords());
			outputStatistics(rdStatisticsFile, tree.getNgramRDStatistics());
			outputDependencyStatistics(dependencyStatisticsFile, tree.getPreDenpents(), tree.getPostDenpents());
			processDependency(tree);
			for(String rd: tree.getDependencyRatio().keySet()){
				System.out.println(rd + ": " + tree.getDependencyRatio().get(rd));
			}
        }
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private static void processDependency(DependencyTree tree) {
		
		for(String t : tree.getPreDenpents().keySet()){
			
			tree.getDependencyRatio().put(t,!tree.getPostDenpents().containsKey(t) ? 1: tree.getPreDenpents().get(t) *1.0/ (tree.getPreDenpents().get(t) + tree.getPostDenpents().get(t)));
			if(!tree.getPostDenpents().containsKey(t) || tree.getPostDenpents().get(t) < tree.getPreDenpents().get(t)){
				tree.getDependency().put(t,1);
			}else{
				tree.getDependency().put(t,0);
			}

		}
		for(String t : tree.getPostDenpents().keySet()){
			
			if(!tree.getPreDenpents().containsKey(t)){
				tree.getDependencyRatio().put(t,0D);

				tree.getDependency().put(t,0);
				
			}
		}
	}
	private static void outputDependencyStatistics(
			String dependencyStatisticsFile, Map<String, Long> presDenpents,
			Map<String, Long> postDenpents) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dependencyStatisticsFile) ,"UTF-8"));
			
			String line ="";
			for(String t : presDenpents.keySet()){
				line= t +":" + presDenpents.get(t) + " " + (postDenpents.containsKey(t) ? postDenpents.get(t): 0) +  "\t";
				
				bw.write(line.trim());
				
				bw.newLine();
			
				bw.flush();

			}
			for(String t : postDenpents.keySet()){
				
				if(!presDenpents.containsKey(t)){
					line= t +":" + 0 + " " + postDenpents.get(t) +  "\t";
					bw.write(line.trim());
					
					bw.newLine();
				
					bw.flush();
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void outputStatistics(String statisticsFile, Map<String, Long> stats){
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(statisticsFile) ,"UTF-8"));
			int  i  = 0;
			String line ="";
			for(String t : stats.keySet()){
				line += t +":" + stats.get(t) + "\t";
				if(++i % 10 == 0) {
					bw.write(line.trim());
					line = "";
					bw.newLine();
				}
				bw.flush();

			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void outputGramCountStatistics(String statisticsFile, Map<Integer, Set<String>> stats){
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(statisticsFile) ,"UTF-8"));
			
			String line ="";
			for(int t : stats.keySet()){
				
				line = t +"\t" +stats.get(t).size() + "\t";
				for(String s : stats.get(t)){
					line += s + "\t";
				}
				
				
				
				bw.write(line.trim());
				bw.newLine();
				bw.flush();

			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void outputCountStatistics(String statisticsFile, Map<String, Set<String>> stats){
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(statisticsFile) ,"UTF-8"));
			
			String line ="";
			for(Object t : stats.keySet()){
				
				line = t +"\t" +stats.get(t).size() + "\t";
				for(String s : stats.get(t)){
					line += s + "\t";
				}
				
				
				bw.write(line.trim());
				bw.newLine();
				bw.flush();

			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void readStatistics(String statisticsFile, Map<String, Long> statistics){
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(statisticsFile),"UTF-8"));
		    String s =  null;
			while((s = br.readLine()) != null ){
			      String[] stats = s.split("[\\t]");
			      for(String stat : stats){
			    	  String[] vals = stat.split(":");
			    	 
	                statistics.put(vals[0].trim(),Long.parseLong(vals[1].trim()));
	                	
			      }
			}
			br.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void trainHeadWords(Map<String, Long> headWords,
			TreeNode root, int headMaxGram, BufferedWriter bw) {
		// Ignore tree with only one node
		if(root  != null){
			for(int i = 0; i < root.getPres().size(); i++ ){
				trainHeadWords(headWords, root.getPres().get(i), headMaxGram, bw);
			}
			for(int i = 0; i < root.getPoss().size(); i++ ){
				trainHeadWords(headWords, root.getPoss().get(i), headMaxGram, bw);
			}
		}
		if(root != null && (root.getPoss().size() + root.getPres().size()) > 0){
			Properties.getProperties().getNgramHeadWordStats().setWordCount(
					Properties.getProperties().getNgramHeadWordStats().getWordCount() + root.getPoss().size() + root.getPres().size() + 1);
					
			//System.out.println(root.getName());
			String w = null;
			
			StringBuffer seqHeadWords = new StringBuffer(""),  seqWords = new StringBuffer("");
			List<TreeNode> linearizedNodes = new ArrayList<TreeNode>();
			for(int i = 0; i < root.getPres().size(); i++ ){
				linearizedNodes.add(root.getPres().get(i));
			}
			linearizedNodes.add(root);
			for(int i = 0; i < root.getPoss().size(); i++ ){
				linearizedNodes.add(root.getPoss().get(i));
			}
			for(int i = 0; i <= linearizedNodes.size(); i++ ){
				
				if(i <  linearizedNodes.size()) {
					w = linearizedNodes.get(i).getName().toLowerCase();
					seqHeadWords.append( w).append( " ");
					seqWords.append( linearizedNodes.get(i).getRefWordSequence()).append(" ");
				}
				else w = "NULL"; // sensitive with "null"
				
				for(int j = 0; j < headMaxGram;  j++){
					
					w = w.trim();
					// counting gram types
					if(i - j >= 0){
						if(!Properties.getProperties().getNgramHeadWordStats().getGramCountStatistics().containsKey(j+1)){
							Properties.getProperties().getNgramHeadWordStats().getGramCountStatistics().put(j+1, new TreeSet<String>());
						}
						Properties.getProperties().getNgramHeadWordStats().getGramCountStatistics().get(j+1).add(w);

					}
				// N1+(*,w)
                    if(!Properties.getProperties().getNgramHeadWordStats().getPrecedingStatistics().containsKey(w) ){
                    	//if(i - 1 - j >= 0)
                   
                    	Properties.getProperties().getNgramHeadWordStats().getPrecedingStatistics().put(w, new TreeSet<String>());
                    }
            		Properties.getProperties().getNgramHeadWordStats().getPrecedingStatistics().get(w).add(i - 1 - j >= 0 ? linearizedNodes.get(i- 1-j).getName().toLowerCase() :  "NULL");

               // N1+(w,*)
	                if(!Properties.getProperties().getNgramHeadWordStats().getFollowStatistics().containsKey(w) ){
	                    	Properties.getProperties().getNgramHeadWordStats().getFollowStatistics().put(w, new TreeSet<String>());
	                }
	            	if(i != linearizedNodes.size()) 
                		Properties.getProperties().getNgramHeadWordStats().getFollowStatistics().get(w).add( i +1 < linearizedNodes.size() ? linearizedNodes.get(i+1).getName().toLowerCase() : "NULL");
	            	else{
                		Properties.getProperties().getNgramHeadWordStats().getFollowStatistics().get(w).add(linearizedNodes.get(0).getName().toLowerCase());

	            	}
	            	
	                	//System.out.println(w);
	                	if(headWords.containsKey(w)){
	                		headWords.put(w,headWords.get(w) + 1 );
	                	}else{
	                		headWords.put(w,1L);
	                	}
	                
	                w = (i - 1 - j >= 0 ? linearizedNodes.get(i- 1-j).getName() : "NULL") + " " + w;
					
				}
			}
		//	System.out.println("Seq: " + seqHeadWords);
			root.setrefHeadSequence(seqHeadWords.toString().trim());
			root.setRefWordSequence(seqWords.toString().trim());
			try {
				bw.write(root.getIndex() + "\t" + seqWords.toString().trim() + "\n");
				bw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
		
	}
	
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           