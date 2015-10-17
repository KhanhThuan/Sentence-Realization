package jp.ac.jaist.srealizer.properties;

import java.util.ArrayList;
import java.util.List;

import jp.ac.jaist.srealizer.data.model.NGramStatistics;


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
	
	
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     