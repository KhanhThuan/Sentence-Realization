package jp.ac.jaist.srealizer.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jp.ac.jaist.srealizer.data.model.TreeNode;
import jp.ac.jaist.srealizer.properties.Properties;

import org.apache.commons.lang.StringEscapeUtils;

import JVnRDRPOSTagger.RDRPOSTagger;

public class CommonUtils {
		
	private static final Logger log = Logger.getLogger( CommonUtils.class.getName() );

		public static void printList(List<Integer> list){
			for(int i = 0; i < list.size(); i++){
				System.out.println("i = " + i + ": " + list.get(i) );
			}
		}
		public static void setSDconfig(String configFileName, String candFile,double[] lamda ){
			StringBuffer s = new StringBuffer("");
			s.append("cands_file=").append(candFile).append("\n")
			.append("cands_per_sen=50\n").append("top_n=10\n")
			.append("RD ").append(lamda[1]).append("\n")
			.append("Word Model ").append(lamda[2]).append("\n")
			.append("Headword Model ").append(lamda[3]).append("\n");
					

		}
		public static void setParams(double[] lamda, String filename){
			StringBuffer s = new StringBuffer("");
			s.append("RD		|||	").append(lamda[1]).append("	Opt	-Inf	+Inf	-1	+1\n")
				.append("Word Model	|||	").append(lamda[2]).append("	Opt	-Inf	+Inf	-1	+1\n")
				.append("Headword Model	||| ").append(lamda[3]).append("	Opt	-Inf	+Inf	-1	+1\n")
				.append("normalization = none");
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF-8"));
				bw.write(s.toString());
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		public static double[][] sort(List<double[]> allLamdas) {
			double[][] x = new double[allLamdas.get(0).length][allLamdas.size()];
			int  j = 0;
			for(double[] d: allLamdas){
				
				for(int i =0; i < d.length; i++){
					x[i][j] = d[i];
				}
				j++;
			}
			for(int i =0; i < x.length; i++){
				for(int k = 0; k < x[i].length -1; k++){
					for(int t = 0; t < x[i].length - k -1; t++){
						if(x[i][t] > x[i][t+1]){
							double temp= x[i][t];
							x[i][t] = x[i][t+1];
							x[i][t+1] = temp;
						}
					}
				}
			}
			
		/*	for(int i =0; i < x.length; i++){
				for(int k = 0; k < x[i].length; k++){
					
					System.out.print(x[i][k] + " ");
				}
				System.out.println();
			}*/
			return x;
		}
		public static void copy(String source, String dest) throws IOException, IOException{
			BufferedReader	br = new BufferedReader(new InputStreamReader(new FileInputStream(source),"UTF-8"));
			BufferedWriter	bw= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest),"UTF-8"));

			String s  = null;
			while((s = br.readLine()) != null){
				bw.write(s);
				bw.newLine();
			}
			br.close();
			bw.close();
		}
		
		public static String filterOne(String messageString) throws IOException{
			String[] ws = messageString.trim().split("[\\s]+");
			StringBuffer newStr = new StringBuffer("");
			int[] tags = new int[ws.length];
			boolean[] collected = new boolean[ws.length];
			for(int i = 0; i < ws.length; i++){ // roughly ordering the sentence.
				String s = RDRPOSTagger.tagVnWSSentence(ws[i]);
				String tag = s.split("/")[1].trim().toUpperCase();
				System.out.print(tag + " ");
				tags[i] = tag.equals("M") ? 0:  tag.equals("N")|| tag.equals("E") || tag.equals("NC") ||tag.equals("NP")? 1:tag.equals("NY") ? 2: tag.equals("R") ? 3 :  tag.equals("A")  ?  4: tag.equals("V")? 5: tag.equals("C") ? 6: 7;
			}
			System.out.println();
			sort(ws, tags);
			for(int  i = 0; i < ws.length; i++){
				System.out.println(ws[i] + ":" + tags[i]);
			}
			int prev_tag = 0;
			int count = 0;
			int max = 7;
			for(int  i = 0;;i++){
				
					if(i < ws.length && !collected[i]) {
						newStr.append(ws[i]).append(" ");
						collected[i] = true;
						System.out.println("=> "  +ws[i]);
						prev_tag = tags[i];

						count++;
						while(i < ws.length && tags[i] == prev_tag ) i++;
						
						if(i == ws.length) i = -1;
						else i--;
					}
					if(i ==  ws.length) i =0;
					
				if(count == ws.length)break;
			}
			log.info("==> " + newStr.toString());
			return newStr.toString();
		}
		public String filterTwo(String message){
			return "";
		}
		public static void writeToFile(String sourceStr, String dest) throws IOException, IOException{
			BufferedWriter	bw= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest),"UTF-8"));

		
			bw.write(sourceStr);
			
			bw.close();
		}
		public static void sort( String words[], final int[] tags){
			for(int i =0; i < words.length -1; i++){
				for(int j = 0; j < words.length -i -1; j++){
					if(tags[j] > tags[j+1]) {
						int tmp = tags[j];
						tags[j] = tags[j+1];
						tags[j+1] = tmp;
						String tmpStr = words[j];
						words[j] = words[j+1];
						words[j+1] = tmpStr;
					}else if(tags[j] == tags[j+1]){
						if(words[j].length() > words[j+1].length()){
							String tmpStr = words[j];
							words[j] = words[j+1];
							words[j+1] = tmpStr;
						}
					}
				}
			}
		
		}

		public static void main(String[] args) throws IOException{
			List<double[] > al = new ArrayList<double[]>();
			al.add(new double[]{2,3,4});
			al.add(new double[]{1,2,5});
			al.add(new double[]{3,6,4});
			al.add(new double[]{2,3,7});
			al.add(new double[]{9,3,1});

			sort(al);
			
			System.out.println(filterOne("công_nghiệp quốc_gia đang phát_triển việt_nam là "));
			
		}
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        