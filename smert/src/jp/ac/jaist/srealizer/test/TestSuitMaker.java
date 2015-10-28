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
		String foldIn = "examples/bag-of-word-type-input/";
		String foldIn1 = "examples/tree-type-input/";
		BufferedWriter  bw, bw1, bw2,bw3;
		StringBuffer b;
		DependencyTree  tree = new DependencyTree();
		ModelBuilder.makeDependencyStatistics(ModelBuilder.getDependencyTreeFile(), tree,new int[]{2,3}, 3);
		Collections.shuffle(tree.getSentences());
		Collections.shuffle(tree.getSentences());

		for(int i = 0; i < 3; i++){
			 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(foldIn + "test_" + i + ".txt") ,"UTF-8"));
			 bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(foldIn + "ref_" + i + ".txt") ,"UTF-8"));
			
			 bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(foldIn1 + "test_" + i + ".DEP") ,"UTF-8"));
			 bw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(foldIn1 + "ref_" + i + ".txt") ,"UTF-8"));
			
			for(int j = 526*i; j< 526 *(i+1) ;j++){
				
				bw1.write( tree.getSentences().get(j).getRefWordSequence());
				bw1.newLine();
				bw3.write( tree.getSentences().get(j).getRefWordSequence());
				bw3.newLine();
				
				
				String[] words = tree.getSentences().get(j).getRefWordSequence().split("[\\s]+");
				List<String> wordList = Arrays.asList(words);
				Collections.shuffle(wordList);
				b = new StringBuffer("");
				for(String s : wordList){
					b.append(s).append(" ");
				}
				bw.write(b.toString());
				bw.newLine();
				List<String[]> stacksSentence = tree.getSentences().get(j).getStacksSentence();
			
				StringBuffer bf = new StringBuffer("");
				for(String[] props: stacksSentence){
					bf.append(props[0].toLowerCase() + "\t" + props[1].toUpperCase() + "\t" + props[2] +"\t" + props[3].toUpperCase()).append("\n");
				}
				
				bf.append("\n");
				bw2.write(bf.toString());
				
			}
			bw.close();
			bw1.close();
			bw2.close();
			bw3.close();
		}
	  }
}
