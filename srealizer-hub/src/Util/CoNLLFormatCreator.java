/**
 * 
 */
package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import JVnRDRPOSTagger.RDRPOSTagger;

/**
 * @author DatQuocNguyen
 * 
 */

public class CoNLLFormatCreator
{
	// From word segmented sentence to CoNLL format.

	public static String fromTaggedSen2CoNLL(String taggedSen)
	{
		StringBuilder build = new StringBuilder();

		int Id = 0;
		for (String wordTag : taggedSen.split(" ")) {
			wordTag = wordTag.trim();
			String word = "", tag = "";
			if (wordTag.length() == 0)
				continue;
			Id = Id + 1;
			if (wordTag.equals("///")) {
				word = "/";
				tag = "/";
			}
			else {
				int index = wordTag.lastIndexOf("/");
				word = wordTag.substring(0, index);
				tag = wordTag.substring(index + 1);
			}

			build.append(Id);
			build.append("\t");
			build.append(word);
			build.append("\t");
			build.append("_");
			build.append("\t");
			if (tag.equals("LBKT") || tag.equals("RBKT"))
				build.append(tag);
			else
				build.append(tag.charAt(0));
			build.append("\t");
			build.append(tag);
			build.append("\t");
			build.append("_");
			build.append("\t");
			build.append("0");
			build.append("\t");
			build.append("root");
			build.append("\t");
			build.append("_");
			build.append("\t");
			build.append("_");
			build.append("\n");
		}
		return build.toString();
	}

	public static String toCoNLL4Sentence(String wsSen)
		throws IOException
	{
		StringBuilder build = new StringBuilder();

		String taggedSen = RDRPOSTagger.tagVnWSSentence(wsSen);

		return fromTaggedSen2CoNLL(taggedSen);
	}

	// From word segmented corpus to CoNLL format.
	public static void toCoNLL4Corpus(String inputWSCorpusPath)
		throws IOException
	{
		System.out.println("Tagging word-segmented corpus: "
				+ inputWSCorpusPath);
		RDRPOSTagger.tagVnWSCorpus(inputWSCorpusPath);
		System.out.println("\tOutput POS-tagged corpus: " + inputWSCorpusPath
				+ ".TAGGED");

		System.out
				.println("Converting POS-tagged corpus to be in CoNLL format...");
		// To speed up the process of converting to CoNLL format instead of
		// using the previous toCoNLL4Sentence function.
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(inputWSCorpusPath + ".TAGGED")),
				"UTF-8"));

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(inputWSCorpusPath + ".TAGGED.CONLL"),
				"UTF-8"));

		for (String taggedSen; (taggedSen = buffer.readLine()) != null;) {
			bw.write(fromTaggedSen2CoNLL(taggedSen) + "\n");
		}

		buffer.close();
		bw.close();
		System.out.println("\tOutput POS-tagged corpus in CoNLL format: "
				+ inputWSCorpusPath + ".TAGGED.CONLL");

	}

	public static void main(String args[])
		throws IOException
	{
		// System.out.println(CoNLLFormatCreator
		// .toCoNLL4Sentence("Sài_Gòn hàng_hiệu thể_thao ."));
		// CoNLLFormatCreator.toCoNLL4Corpus("./src/Sample/VietTB.FOLD09.WSRAW");
	}
}
