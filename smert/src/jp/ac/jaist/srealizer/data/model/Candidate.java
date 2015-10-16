package jp.ac.jaist.srealizer.data.model;

import java.util.List;

public class Candidate {
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public double[] getFeats() {
		return feats;
	}
	public void setFeats(double[] feats) {
		this.feats = feats;
	}
	public double getAvgScore() {
		return avgScore;
	}
	public void setAvgScore(double avgScore) {
		this.avgScore = avgScore;
	}
	public List<String> getRefs() {
		return refs;
	}
	public void setRefs(List<String> refs) {
		this.refs = refs;
	}
	
	public int getIndexSentence() {
		return indexSentence;
	}
	public void setIndexSentence(int indexSentence) {
		this.indexSentence = indexSentence;
	}
	public int getIndexNode() {
		return indexNode;
	}
	public void setIndexNode(int indexNode) {
		this.indexNode = indexNode;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private String content;
	private double[] feats;
    private double avgScore;
    private List<String> refs;
    private int indexSentence;
    private int indexNode;
    private String id;
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              