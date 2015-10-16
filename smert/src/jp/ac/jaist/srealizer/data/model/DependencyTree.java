package jp.ac.jaist.srealizer.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DependencyTree {
	
	public DependencyTree(Map<String, Long> preDenpents,
			Map<String, Long> postDenpents, Map<String, Long> headWords,Map<String, Long> ngramRDStatistics,
			long numberSentences) {
		super();
		this.preDenpents = preDenpents;
		this.postDenpents = postDenpents;
		this.headWords = headWords;
		this.ngramRDStatistics = ngramRDStatistics;
		this.numberSentences = numberSentences;
		
	}
    
	public DependencyTree() {
		super();
		preDenpents= new TreeMap<String, Long>();
		headWords= new TreeMap<String, Long>();
		postDenpents= new TreeMap<String, Long>();
		ngramRDStatistics = new TreeMap<String, Long>();
		sentences = new ArrayList<TreeNode>();
		IndexSentences = new TreeMap<Integer, Integer>();
		dependency = new HashMap<String, Integer>();
		dependencyRatio = new HashMap<String, Double>();

	}
    
	public Map<String, Integer> getDependency() {
		return dependency;
	}
    
	public void setDependency(Map<String, Integer> dependency) {
		this.dependency = dependency;
	}

	public Map<String, Long> getPreDenpents() {
		return preDenpents;
	}
    public void setPreDenpents(Map<String, Long> preDenpents) {
		this.preDenpents = preDenpents;
	}

	public Map<String, Long> getPostDenpents() {
		return postDenpents;
	}

	public void setPostDenpents(Map<String, Long> postDenpents) {
		this.postDenpents = postDenpents;
	}

	public Map<String, Long> getHeadWords() {
		return headWords;
	}

	public void setHeadWords(Map<String, Long> headWords) {
		this.headWords = headWords;
	}

	public Map<String, Long> getNgramRDStatistics() {
		return ngramRDStatistics;
	}

	public void setNgramRDStatistics(Map<String, Long> ngramRDStatistics) {
		this.ngramRDStatistics = ngramRDStatistics;
	}

	public long getNumberSentences() {
		return numberSentences;
	}

	public void setNumberSentences(long numberSentences) {
		this.numberSentences = numberSentences;
	}

	public List<TreeNode> getSentences() {
		return sentences;
	}

	public void setSentences(List<TreeNode> sentences) {
		this.sentences = sentences;
	}
	public void addSentence(TreeNode root) {
		sentences.add(root);
		IndexSentences.put(root.getIndex(),sentences.size()-1);
	}
	public Map<Integer, Integer> getIndexSentences() {
		return IndexSentences;
	}

	public void setIndexSentences(Map<Integer, Integer> indexSentences) {
		IndexSentences = indexSentences;
	}

	public Map<String, Double> getDependencyRatio() {
		return dependencyRatio;
	}

	public void setDependencyRatio(Map<String, Double> dependencyRatio) {
		this.dependencyRatio = dependencyRatio;
	}

	private Map<String,Long> preDenpents;
	private Map<String,Long> postDenpents;
	private Map<String,Long> headWords;
	private Map<String,Long> ngramRDStatistics;
    private List<TreeNode> sentences;
    private Map<Integer, Integer> IndexSentences;
    private Map<String, Integer> dependency; //1  for pre; 0 for post;
    private Map<String, Double> dependencyRatio; //pre/post;

	private long numberSentences;
	
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          