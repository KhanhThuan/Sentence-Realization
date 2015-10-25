package jp.ac.jaist.srealizer.test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jp.ac.jaist.realizer.data.builder.ModelBuilder;
import jp.ac.jaist.srealizer.data.model.DependencyTree;
import jp.ac.jaist.srealizer.data.model.TreeNode;

public class TestSuitMaker {
	public static void main(String[] args) throws IOException, IOException{
		String foldIn = "C:/Users/vanth_000/OneDrive/JAIST/minor-research-master/VnDPv1/src/Sample/";
		BufferedWriter  bw, bw1;
		StringBuffer b;
		DependencyTree  tree = new DependencyTree();
		ModelBuilder.makeDependencyStatistics(ModelBuilder.getDependencyTreeFile(), tree,new int[]{2,3}, 3);
		Collections.shuffle(tree.getSentences());
		for(int i = 0; i < 5; i++){
			 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(foldIn + "test_" + i + ".txt") ,"UTF-8"));
			 bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/dependency-tree/test/" + "ref_" + i + ".txt") ,"UTF-8"));
			int  k = 0;
			for(int j = 526*i; j< 526 *(i+1) ;j++){
				bw1.write(k++ + "\t" + tree.getSentences().get(j).getRefWordSequence());
				bw1.newLine();
				String[] words = tree.getSentences().get(j).getRefWordSequence().split("[\\s]+");
				List<String> wordList = Arrays.asList(words);
				Collections.shuffle(wordList);
				b = new StringBuffer("");
				for(String s : wordList){
					b.append(s).append(" ");
				}
				bw.write(b.toString());
				bw.newLine();
				
			}
			bw.close();
			bw1.close();
		}
	  }
}
