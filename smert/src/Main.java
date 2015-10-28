import java.util.Date;
import java.util.List;

import jp.ac.jaist.realizer.data.builder.ModelBuilder;
import jp.ac.jaist.srealizer.algorithms.data.PunctuationRemover;
import jp.ac.jaist.srealizer.properties.Properties;
import jp.ac.jaist.srealizer.utils.CommonUtils;
import VnDP.VnDependencyParsing;


public class Main {
	public static void main(String[] args) throws Exception{
		System.out.println("Program start at @" + new Date());
		ModelBuilder.initFolder(); // initialization
		
		
		/******************INPUT*******************************/
			int flagBagOfWord = 1; // change flag to 1 to test in bag-of-word mode.
			String intputFile ="examples/bag-of-word-type-input/test_1.txt" ; // "examples/tree-type-input/test_0.DEP"
			String refFile = "examples/bag-of-word-type-input/ref_1.txt" ; //  // "examples/tree-type-input/ref_0.txt"
			
			
		/******************END_INPUT*******************************/
			
			
		String testFile = intputFile;
		if(flagBagOfWord == 1){ // Test with input is bag of words
			
			String processedFile = "src/sample/test.txt";
			CommonUtils.copy(intputFile, processedFile);
			VnDependencyParsing.parseWordSegmentedCorpus(processedFile);
			//copy("src/Sample/sample.ws.DEP.CONLL", "E:/minor-research-kt/smert/data/dependency-tree/test/test_" + i + ".txt");
			List<String[]> cands =  VnDependencyParsing.getCandiates(intputFile);
			testFile = "data/dependency-tree/test/test_tree.DEP.CONLL";
			PunctuationRemover.removeColl(cands,processedFile + ".DEP.CONLL",testFile  );
			
		}
		Properties.getProperties().realizeSentences(499 /*Number of training sentences*/, testFile, refFile);
		
		System.out.println("Finished at @" + new Date());

		
	}

}
