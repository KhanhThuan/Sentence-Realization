/**
 * 
 */
package VnDP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.jaist.srealizer.algorithms.data.PunctuationRemover;
import Util.CoNLLFormatCreator;
import mstparser.DependencyParser;
import mstparser.DependencyPipe;
import mstparser.DependencyPipe2O;
import mstparser.ParserOptions;

/**
 * @author DatQuocNguyen
 * 
 */
public class VnDependencyParsing
	extends DependencyParser
{
	public final static String TRAINEDMODELPATH = "src/Model/VnDP.model";

	/**
	 * @param pipe
	 * @param options
	 */
	public VnDependencyParsing(DependencyPipe pipe, ParserOptions options)
	{
		super(pipe, options);
		// TODO Auto-generated constructor stub
	}

	public static void parseWordSegmentedCorpus(String pathToWSCorpus)
		throws Exception
	{
		CoNLLFormatCreator.toCoNLL4Corpus(pathToWSCorpus);

		String testFilePath = pathToWSCorpus + ".TAGGED.CONLL";
		String outputFilePath = pathToWSCorpus + ".DEP.CONLL";

		String[] strOptions = { "test", "decode-type:non-proj", "format:CONLL",
				"model-name:" + TRAINEDMODELPATH, "test-file:" + testFilePath,
				"output-file:" + outputFilePath };

		ParserOptions options = new ParserOptions(strOptions);
		DependencyPipe pipe = options.secondOrder ? new DependencyPipe2O(
				options) : new DependencyPipe(options);
		VnDependencyParsing dp = new VnDependencyParsing(pipe, options);

		// System.out.println(options.toString());

		System.out.println("Loading pre-trained model:" + TRAINEDMODELPATH);
		dp.loadModel(options.modelName);
		pipe.closeAlphabets();
		dp.outputParses();

		System.out
				.println("Output dependency-parsed corpus: " + outputFilePath);
	}

	public static void main(String args[])
		throws IOException, Exception
	{
		for(int i = 0; i < 5; i++){
			parseWordSegmentedCorpus("src/Sample/test_" + i + ".txt");
			//copy("src/Sample/sample.ws.DEP.CONLL", "E:/minor-research-kt/smert/data/dependency-tree/test/test_" + i + ".txt");
			List<String[]> cands = getCandiates("src/Sample/test_" + i + ".txt");
			PunctuationRemover.removeColl(cands, "src/Sample/test_" + i + ".txt.DEP.CONLL",  "data/dependency-tree/test/test_tree_" + i + ".DEP.CONLL");
		}
		//parseWordSegmentedCorpus("src/Sample/sample.ws");
	}
	public static List<String[]> getCandiates(String source) throws IOException, IOException{
		BufferedReader	br = new BufferedReader(new InputStreamReader(new FileInputStream(source),"UTF-8"));
		String s  = null;
		List<String[]> cands = new ArrayList<String[]>();
		while((s = br.readLine()) != null){
			cands.add(s.split("[\\s]+"));
		}
		br.close();
		return cands;
	}
}
