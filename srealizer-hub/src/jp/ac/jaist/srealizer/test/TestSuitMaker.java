package jp.ac.jaist.srealizer.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import jp.ac.jaist.realizer.data.builder.ModelBuilder;
import jp.ac.jaist.srealizer.data.model.DependencyTree;
import jp.ac.jaist.srealizer.data.model.TreeNode;
import jp.ac.jaist.srealizer.properties.Properties;
import jp.ac.jaist.srealizer.utils.CommonUtils;

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

		for(int i = 0; i < 10; i++){
			 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(foldIn + "test_" + i + ".txt") ,"UTF-8"));
			 bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(foldIn + "ref_" + i + ".txt") ,"UTF-8"));
			
			 bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(foldIn1 + "test_" + i + ".DEP") ,"UTF-8"));
			 bw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(foldIn1 + "ref_" + i + ".txt") ,"UTF-8"));
				
			 	StringBuilder  ref= new StringBuilder("");
	         	StringBuilder  cand= new StringBuilder("");

	         	StringBuilder  src= new StringBuilder("");
	         	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE mteval SYSTEM \"ftp://jaguar.ncsl.nist.gov/mt/resources/mteval-xml-v1.3.dtd\">";
	         	ref.append(xml);
	         	src.append(xml);
	         	cand.append(xml);
	         	
	    		ref.append("<mteval>\n<refset setid=\"example_set\" srclang=\"Eng\" trglang=\"Eng\" refid=\"ref1\">");
	    		ref.append("<doc docid=\"doc1\" genre=\"nw\">");
	    		//
	    		cand.append("<mteval>\n<tstset setid=\"example_set\" srclang=\"Eng\" trglang=\"Eng\" sysid=\"sample_system\">");
	    		cand.append("<doc docid=\"doc1\" genre=\"nw\">");
	    		//
	    		src.append("<mteval>\n<srcset setid=\"example_set\" srclang=\"Eng\"  >");
	    		src.append("<doc docid=\"doc1\" genre=\"nw\">");
	    		
			for(int j = 526*i; j< 526 *(i+1) ;j++){
				
				bw1.write( tree.getSentences().get(j).getRefWordSequence());
				bw1.newLine();
				bw3.write( tree.getSentences().get(j).getRefWordSequence());
				bw3.newLine();
				
				 ref.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
				 src.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
				 cand.append("\n\t<p>\n\t\t<seg id=\"" + (i+1) + "\">");
				 
				
				 ref.append(StringEscapeUtils.escapeXml( tree.getSentences().get(j).getRefWordSequence()) + "\n\t\t</seg>\n\t</p>");
				 src.append(StringEscapeUtils.escapeXml( tree.getSentences().get(j).getRefWordSequence()) + "\n\t\t</seg>\n\t</p>");

				
			
				
				
				String[] words = tree.getSentences().get(j).getRefWordSequence().split("[\\s]+");
				List<String> wordList = Arrays.asList(words);
				//Collections.shuffle(wordList); // next time
				
				b = new StringBuffer("");
				for(String s : wordList){
					b.append(s).append(" ");
				}
				String out = CommonUtils.filterOne(b.toString());
				bw.write(out);
				bw.newLine();
				cand.append(StringEscapeUtils.escapeXml(out) + "\n\t\t</seg>\n\t</p>");

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
			 ref.append("</doc>");
			 ref.append("</refset>");
			 //
			 cand.append("</doc>");
			 cand.append("</tstset>");
			 //
			 src.append("</doc>");
			 src.append("</srcset>");
			 //
			 ref.append("</mteval>");
	         src.append("</mteval>");
	         cand.append("</mteval>");
	         try{
		         File f  = new File("examples/bag-of-word-type-input/mteval/" + i);
		         if(!f.exists()) f.mkdir();

	         }catch(Exception e){
	        	 
	         }
			CommonUtils.copy("results/mteval/mteval-v13a.pl","examples/bag-of-word-type-input/mteval/" + i + "/mteval-v13a.pl");
			 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("examples/bag-of-word-type-input/mteval/" +i + "/src.xml"),"UTF-8"));
			 bw.write(src.toString());
			 bw.close();
			 //
			 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( "examples/bag-of-word-type-input/mteval/" + i + "/tst.xml"),"UTF-8"));
			 bw.write(cand.toString());
			 bw.close();
			 //
			 bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( "examples/bag-of-word-type-input/mteval/" +  i + "/ref.xml"),"UTF-8"));
			 bw.write(ref.toString());
			 bw.close();
						
		}
	  }
}
