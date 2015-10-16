package jp.ac.jaist.srealizer.algorithms.data;

import java.util.Map;

import jp.ac.jaist.srealizer.data.model.NGramStatistics;

public class NgramProbabilityMethod {
	
	
	// incompleteness
	public static double kneserNeySmoothing(String[] words, double d,int n,  Map<String,Long>  stats, Map<String,Long>  preStats, Map<String,Long>  followStats, Map<Integer,Long> gramCountStats ){
		double p = 1;
		
		for(int i =0; i <= words.length; i++){
			p *= contProb(words,d, i, n,true, stats, preStats, followStats, gramCountStats) ;
		}
		return p;
	}
	public static double contProb(String words[],double d, int i, int n,boolean highest, Map<String,Long>  stats, Map<String,Long>  preStats, Map<String,Long>  followStats, Map<Integer,Long> gramCountStats){
		if( n == 1){
			return (countKN(words, i, n,preStats ) *1.0 + 1 )/(gramCountStats.get(2) + 1000);
		}
		return 1.0 * max(countKN(words, i, i-n+1,highest? stats : preStats) - d, 0D) / 
				(countKN(words, i-1, i-n+1, highest ? stats :preStats) + 1000)
				+ ((d * countKN(words, i-1, i-n+1, followStats) +1) /(countKN(words, i-1, i-n+1, stats) + 1000) )
				  * contProb(words, d, i, n-1, false, stats, preStats, followStats,gramCountStats); 
	
		
	}
	
	public static long countKN(String words[], int i, int j, Map<String,Long>  stats){
		  String w = concat(words, i, j);
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
