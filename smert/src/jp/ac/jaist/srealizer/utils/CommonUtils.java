package jp.ac.jaist.srealizer.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class CommonUtils {
		
	
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
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        