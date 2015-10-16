package jp.ac.jaist.srealizer.data.model;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class NGramStatistics {
	private int[] grams;
	private long wordCount;
	private Map<String, Long> statistics;
	private Map<String, Long> precedingLongStatistics;
	private Map<String, Long> followLongStatistics;
	private Map<Integer, Long> gramCountLongStatistics;

	private Map<String, Set<String>> precedingStatistics;
	private Map<String, Set<String>> followStatistics;
	private Map<Integer, Set<String>> gramCountStatistics;

	public NGramStatistics() {
		super();
		wordCount = 0;
		statistics = new TreeMap<String, Long>();
		precedingStatistics = new TreeMap<String, Set<String>>();
		followStatistics = new TreeMap<String, Set<String>>();
		gramCountStatistics= new TreeMap<Integer, Set<String>>();
		precedingLongStatistics = new TreeMap<String, Long>();
		followLongStatistics = new TreeMap<String, Long>();
		gramCountLongStatistics = new TreeMap<Integer, Long>();

		
	}
	public NGramStatistics(int[] grams, Map<String, Long> statistics) {
		super();
		this.grams = grams;
		this.statistics = statistics;
	}
	public void convertToLongStatistics(){
		for(String k : precedingStatistics.keySet()){
			precedingLongStatistics.put(k,(long) precedingStatistics.get(k).size());
		}
		precedingStatistics.clear();
		for(String k : followStatistics.keySet()){
			followLongStatistics.put(k,(long) followStatistics.get(k).size());
		}
		followStatistics.clear();
		for(Integer k : gramCountStatistics.keySet()){
			gramCountLongStatistics.put(k,(long) gramCountStatistics.get(k).size());
		}
		gramCountStatistics.clear();
	}
	public int[] getGrams() {
		return grams;
	}
	public void setGrams(int[] grams) {
		this.grams = grams;
	}
	public Map<String, Long> getStatistics() {
		return statistics;
	}
	public void setStatistics(Map<String, Long> statistics) {
		this.statistics = statistics;
	}
	public long getWordCount() {
		return wordCount;
	}
	public void setWordCount(long wordCount) {
		this.wordCount = wordCount;
	}
	public Map<Integer, Set<String>> getGramCountStatistics() {
		return gramCountStatistics;
	}
	public void setGramCountStatistics(Map<Integer, Set<String>> gramCountStatistics) {
		this.gramCountStatistics = gramCountStatistics;
	}
	public Map<String, Set<String>> getFollowStatistics() {
		return followStatistics;
	}
	public void setFollowStatistics(Map<String, Set<String>> followStatistics) {
		this.followStatistics = followStatistics;
	}
	public Map<String, Set<String>> getPrecedingStatistics() {
		return precedingStatistics;
	}
	public void setPrecedingStatistics(Map<String, Set<String>> precedingStatistics) {
		this.precedingStatistics = precedingStatistics;
	}
	public Map<String, Long> getPrecedingLongStatistics() {
		return precedingLongStatistics;
	}
	public void setPrecedingLongStatistics(Map<String, Long> precedingLongStatistics) {
		this.precedingLongStatistics = precedingLongStatistics;
	}
	public Map<String, Long> getFollowLongStatistics() {
		return followLongStatistics;
	}
	public void setFollowLongStatistics(Map<String, Long> followLongStatistics) {
		this.followLongStatistics = followLongStatistics;
	}
	public Map<Integer, Long> getGramCountLongStatistics() {
		return gramCountLongStatistics;
	}
	public void setGramCountLongStatistics(Map<Integer, Long> gramCountLongStatistics) {
		this.gramCountLongStatistics = gramCountLongStatistics;
	}
	
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               