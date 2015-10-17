package jp.ac.jaist.srealizer.data.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TreeNode {

	
	public TreeNode(int index, int parent, String name, String RD, String type) {
		this.index = index;
		this.name = name;
		this.parent = parent;
		this.RD = RD;
		this.type = type;
		this.optimizedSequence = this.refWordSequence = name;
		children = new ArrayList<TreeNode>();
		candidates = new ArrayList<Candidate>();
		hasOptimized = false;
		pres = new ArrayList<TreeNode>();
		poss = new ArrayList<TreeNode>();
		presIndices = new TreeMap<Integer, Integer>();
		possIndices = new TreeMap<Integer, Integer>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRD() {
		return RD;
	}
	public void setRD(String rD) {
		RD = rD;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public List<TreeNode> getPres() {
		return pres;
	}
	public void setPres(List<TreeNode> pres) {
		this.pres = pres;
	}
	public Map<Integer, Integer> getPresIndices() {
		return presIndices;
	}
	public void setPresIndices(Map<Integer, Integer> presIndices) {
		this.presIndices = presIndices;
	}
	public Map<Integer, Integer> getPossIndices() {
		return possIndices;
	}
	public void setPossIndices(Map<Integer, Integer> possIndices) {
		this.possIndices = possIndices;
	}
	public List<TreeNode> getPoss() {
		return poss;
	}
	public void setPoss(List<TreeNode> poss) {
		this.poss = poss;
	}

	public void addPres(TreeNode node) {
	    pres.add(node);
	    presIndices.put(node.index, pres.size() -1);
		
	}
	public void addPoss(TreeNode node) {
		   poss.add(node);
		   possIndices.put(node.index, poss.size() -1);
		
	}
	public Map<Integer,TreeNode> getNodes() {
		return nodes;
	}
	public void setNodes(Map<Integer,TreeNode> nodes) {
		this.nodes = nodes;
	}
	public String getrefHeadSequence() {
		return refHeadSequence;
	}
	public void setrefHeadSequence(String refHeadSequence) {
		this.refHeadSequence = refHeadSequence;
	}
	public List<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public String getRefWordSequence() {
		return refWordSequence;
	}
	public void setRefWordSequence(String refWordSequence) {
		this.refWordSequence = refWordSequence;
	}
	public List<Candidate> getCandidates() {
		return candidates;
	}
	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}
	public void addCandidate(Candidate candidate) {
		this.candidates.add(candidate);
	}
	public boolean isHasOptimized() {
		return hasOptimized;
	}
	public void setHasOptimized(boolean hasOptimized) {
		this.hasOptimized = hasOptimized;
	}
	public String getOptimizedSequence() {
		return optimizedSequence;
	}
	public void setOptimizedSequence(String optimizedSequence) {
		this.optimizedSequence = optimizedSequence;
	}
	public long getIndexSentence() {
		return indexSentence;
	}
	public void setIndexSentence(long indexSentence) {
		this.indexSentence = indexSentence;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private long indexSentence;
	private String sentence;
	private String refHeadSequence;
	private String refWordSequence;
	private String optimizedSequence;
	private String name;
	private String RD;
	private int parent;
	private String type;
	private int index;
	private boolean hasOptimized;
	private List<TreeNode> pres;
	private Map<Integer, Integer> presIndices;
	private Map<Integer, Integer> possIndices;
	private List<TreeNode> poss;
	private Map<Integer,TreeNode> nodes;
	private List<TreeNode> children;
    private List<Candidate> candidates;
	
}
                                    