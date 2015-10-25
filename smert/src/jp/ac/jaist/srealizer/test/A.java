package jp.ac.jaist.srealizer.test;

import java.util.Map;
import java.util.TreeMap;

import jp.ac.jaist.srealizer.algorithms.data.NgramProbabilityMethod;
import jp.ac.jaist.srealizer.algorithms.linearization.LinearizationDependencyTree;
import jp.ac.jaist.srealizer.properties.Properties;
import jp.ac.jaist.srealizer.utils.CommonUtils;
import kylm.main.CountNgrams;

public class A {

	public static void main(String[] args) {
	     // CommonUtils.setParams(new double[]{0, 0.1, 0.2, -0.1}, "params.txt");
/*		System.out.println("huyen: " + Properties.getProperties().getNgramWordStats().getStatistics().get("huyện"));

		System.out.println("huyen cua chi: " + Properties.getProperties().getNgramWordStats().getStatistics().get("huyện củ_chi"));
		System.out.println("");
		System.out.println(LinearizationDependencyTree.ngramProbabilities("huyện củ_chi tp._hcm", Properties.getProperties().getNgramWordStats().getStatistics(), Properties.getProperties().getGramWord()));
		System.out.println("------------");
		System.out.println(LinearizationDependencyTree.ngramProbabilities("huyện tp._hcm củ_chi", Properties.getProperties().getNgramWordStats().getStatistics(), Properties.getProperties().getGramWord()));
		System.out.println("++++++");
		System.out.println(NgramProbabilityMethod.kneserNeySmoothing("huyện tp._hcm củ_chi".split("[\\s]+"), 0.3, 3, Properties.getProperties().getNgramWordStats().getStatistics(),
				Properties.getProperties().getNgramWordStats().getPrecedingLongStatistics(), Properties.getProperties().getNgramWordStats().getFollowLongStatistics(), 
				Properties.getProperties().getNgramWordStats().getGramCountLongStatistics()));
		System.out.println("-++-----------");
		System.out.println(NgramProbabilityMethod.kneserNeySmoothing("huyện củ_chi tp._hcm".split("[\\s]+"), 0.3, 3, Properties.getProperties().getNgramWordStats().getStatistics(),
				Properties.getProperties().getNgramWordStats().getPrecedingLongStatistics(), Properties.getProperties().getNgramWordStats().getFollowLongStatistics(), 
				Properties.getProperties().getNgramWordStats().getGramCountLongStatistics()));
		System.exit(1);	 */     
		
		Map<String,Map<String,Integer>> preSeq = new TreeMap<String, Map<String,Integer>>();
		Map<String,Map<String,Integer>> posSeq = new TreeMap<String, Map<String,Integer>>();
		preSeq.put("A", new TreeMap<String, Integer>());
		preSeq.get("A").put("B",2);
		preSeq.put("B", new TreeMap<String, Integer>());


	}

}
