package jp.ac.jaist.srealizer.algorithms.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class PunctuationRemover {
	 public static void removeColl(List<String[]> cands, String orgDependencyTreeFile, String outDependencyTreeFile){
		   try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(orgDependencyTreeFile),"UTF-8"));
				List<String[]> propsStack = new ArrayList<String[]>();
				String s = null;
				
			
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outDependencyTreeFile) ,"UTF-8"));
				int k = 0;
				while((s = br.readLine()) != null){
					
					//System.out.println(s.trim().length()  + ": "+ s);
					if(s.trim().length() == 0){
						//System.out.println(propsStack.size());
						Map<Integer, Integer> punctPoints = new HashMap<Integer, Integer>();
						if(propsStack.size() > 0){
							int punct = 0;
							for(int i =0; i < propsStack.size(); i++){
								String[] props = propsStack.get(i);
								int parentIndex = Integer.parseInt(props[6]);
								if(!propsStack.get(i)[7].equals("punct")){
								 //  System.out.println(punct);
									while(parentIndex!= 0  && propsStack.get(parentIndex-1)[7].equals("punct")){
							        	 propsStack.get(i)[6] = Integer.parseInt(propsStack.get(parentIndex-1)[6]) + "";
							        	 parentIndex = Integer.parseInt(propsStack.get(parentIndex-1)[6]);
							         }
									punctPoints.put(i, punct);
								}else{
									punct++;
								}
						         
							}
							for(int i =0; i < propsStack.size(); i++){
								if(!propsStack.get(i)[7].equals("punct")){
									String word =  propsStack.get(i)[1].trim().equals("<num>")? cands.get(k)[i].trim() :  propsStack.get(i)[1].trim();
									String line = word  + "\t" + propsStack.get(i)[3].trim() + "\t" + 
								(Integer.parseInt(propsStack.get(i)[6])  == 0 ?  0 : (Integer.parseInt(propsStack.get(i)[6]) -punctPoints.get(Integer.parseInt(propsStack.get(i)[6]) -1)))  + "\t" 
												+ propsStack.get(i)[7];
									bw.write(line);
									bw.newLine();
								
									bw.flush();
								}
							}
	                       
					
							k++;
							propsStack = new ArrayList<String[]>();
							bw.write(" \n");;
							bw.flush();

						}
					}else{
						String[] props = s.split("[\\s]+");
						// props[0] : id, props[1]: word,  props[6] : parent, props[7] : Relation Dependency (RD)
						//if()
					    propsStack.add(props);
						
					}
					
				}
			bw.flush();
			br.close();
			bw.close();
	       }
			catch(Exception e){
				e.printStackTrace();
			}
	   }
   public static void remove(String orgDependencyTreeFile, String outDependencyTreeFile){
	   try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(orgDependencyTreeFile),"UTF-8"));
			List<String[]> propsStack = new ArrayList<String[]>();
			String s = null;
			
		
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outDependencyTreeFile) ,"UTF-8"));
			while((s = br.readLine()) != null){
				
				//System.out.println(s.trim().length()  + ": "+ s);
				if(s.trim().length() == 0){
					//System.out.println(propsStack.size());
					Map<Integer, Integer> punctPoints = new HashMap<Integer, Integer>();
					if(propsStack.size() > 0){
						int punct = 0;
						for(int i =0; i < propsStack.size(); i++){
							String[] props = propsStack.get(i);
							int parentIndex = Integer.parseInt(props[2]);
							if(!propsStack.get(i)[3].equals("P")){
							 //  System.out.println(punct);
								while(parentIndex!= 0  && propsStack.get(parentIndex-1)[3].equals("P")){
						        	 propsStack.get(i)[2] = Integer.parseInt(propsStack.get(parentIndex-1)[2]) + "";
						        	 parentIndex = Integer.parseInt(propsStack.get(parentIndex-1)[2]);
						         }
								punctPoints.put(i, punct);
							}else{
								punct++;
							}
					         
						}
						for(int i =0; i < propsStack.size(); i++){
							if(!propsStack.get(i)[3].equals("P")){
								String line = propsStack.get(i)[0]  + "\t" + propsStack.get(i)[1] + "\t" + 
							(Integer.parseInt(propsStack.get(i)[2])  == 0 ?  0 : (Integer.parseInt(propsStack.get(i)[2]) -punctPoints.get(Integer.parseInt(propsStack.get(i)[2]) -1)))  + "\t" 
											+ propsStack.get(i)[3];
								bw.write(line);
								bw.newLine();
							
								bw.flush();
							}
						}
                       
				
						
						propsStack = new ArrayList<String[]>();
						bw.write(" \n");;
						bw.flush();

					}
				}else{
					String[] props = s.split("\t");
					// props[0] : word, props[1] : tag, props[2] : parent, props[3] : Relation Dependency (RD)
					//if()
				    propsStack.add(props);
					
				}
				
			}
		bw.flush();
		br.close();
		bw.close();
       }
		catch(Exception e){
			e.printStackTrace();
		}
   }
   public static void removeColl(String orgDependencyTreeFile, String outDependencyTreeFile){
	   try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(orgDependencyTreeFile),"UTF-8"));
			List<String[]> propsStack = new ArrayList<String[]>();
			String s = null;
			
		
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outDependencyTreeFile) ,"UTF-8"));
			while((s = br.readLine()) != null){
				
				//System.out.println(s.trim().length()  + ": "+ s);
				if(s.trim().length() == 0){
					//System.out.println(propsStack.size());
					Map<Integer, Integer> punctPoints = new HashMap<Integer, Integer>();
					if(propsStack.size() > 0){
						int punct = 0;
						for(int i =0; i < propsStack.size(); i++){
							String[] props = propsStack.get(i);
							int parentIndex = Integer.parseInt(props[6]);
							if(!propsStack.get(i)[7].equals("punct")){
							 //  System.out.println(punct);
								while(parentIndex!= 0  && propsStack.get(parentIndex-1)[7].equals("punct")){
						        	 propsStack.get(i)[6] = Integer.parseInt(propsStack.get(parentIndex-1)[6]) + "";
						        	 parentIndex = Integer.parseInt(propsStack.get(parentIndex-1)[6]);
						         }
								punctPoints.put(i, punct);
							}else{
								punct++;
							}
					         
						}
						for(int i =0; i < propsStack.size(); i++){
							if(!propsStack.get(i)[7].equals("punct")){
								String line = propsStack.get(i)[1].trim()  + "\t" + propsStack.get(i)[3].trim() + "\t" + 
							(Integer.parseInt(propsStack.get(i)[6])  == 0 ?  0 : (Integer.parseInt(propsStack.get(i)[6]) -punctPoints.get(Integer.parseInt(propsStack.get(i)[6]) -1)))  + "\t" 
											+ propsStack.get(i)[7];
								bw.write(line);
								bw.newLine();
							
								bw.flush();
							}
						}
                       
				
						
						propsStack = new ArrayList<String[]>();
						bw.write(" \n");;
						bw.flush();

					}
				}else{
					String[] props = s.split("[\\s]+");
					// props[0] : id, props[1]: word,  props[6] : parent, props[7] : Relation Dependency (RD)
					//if()
				    propsStack.add(props);
					
				}
				
			}
		bw.flush();
		br.close();
		bw.close();
       }
		catch(Exception e){
			e.printStackTrace();
		}
   }
   public static void main(String[] args){
	   remove("en/data/dependency-tree/WSJ21_22.3.pc.gs.tab", "en/data/dependency-tree/dependency-tree-no-punct.txt");
	   removeColl("data/dependency-tree/VietTB.conll", "data/dependency-tree/dependency-tree-no-punct.txt");
	   String s = "11	tâm_trí	_	N	N	_	10	dob	_	_";
	   String[] ss = s.split("[\\s]+");
	   for(String t : ss){
		   System.out.println(t);
	   }
   }
}
