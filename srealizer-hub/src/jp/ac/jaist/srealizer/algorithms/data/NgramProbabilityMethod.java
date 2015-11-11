package jp.ac.jaist.srealizer.algorithms.data;

import java.util.Map;

import jp.ac.jaist.srealizer.data.model.NGramStatistics;

public class NgramProbabilityMethod {
	
	
	// incompleteness
	public static double kneserNeySmoothing(String[] words, double d,int n,  Map<String,Long>  stats, Map<String,Long>  preStats, Map<String,Long>  followStats, Map<Integer,Long> gramCountStats ){
		double p = 1;
		if(words.length < n) n= words.length; 
		for(int i =n-1; i < words.length; i++){
			p *= contProb(words,d, i, n,true, stats, preStats, followStats, gramCountStats) ;
		}
		return p;
	}
	public static double contProb(String words[],double d, int i, int n,boolean highest, Map<String,Long>  stats, Map<String,Long>  preStats, Map<String,Long>  followStats, Map<Integer,Long> gramCountStats){
		if( n == 1){
			return (countKN(words, i, i,preStats ) *1.0 + 1 )/(gramCountStats.get(2) + stats.size());
		}
		return 1.0 * max(countKN(words,  i-n+1,i,highest? stats : preStats) - d, 0D) / 
				(countKN(words,  i-n+1,i-1, highest ? stats :preStats) + stats.size())
				+ ((d * countKN(words,  i-n+1,i-1, followStats) +1) /(countKN(words,  i-n+1,i-1, stats) + stats.size()) )
				  * contProb(words, d, i, n-1, false, stats, preStats, followStats,gramCountStats); 
	
		
	}
	
	public static long countKN(String words[], int i, int j, Map<String,Long>  stats){
		  String w = concat(words, i, j);
		//  System.out.println(w);
		  return stats.containsKey(w) ? stats.get(w): 0;
	}
	
	private static String concat(String[] words, int i, int j){
		String b= "";
		int start = i < 0 ? 0:i;
		for(int k = start;k <= j; k++){
			b += words[k] + " ";
		}
		return b.trim();
		
	}
	private static double max(double a, double b){
		return a > b ? a: b;
	}
}
