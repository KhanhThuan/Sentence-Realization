package JVnRDRPOSTagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author DatQuocNguyen
 * 
 */
public class InitialTagger_Vn
{
	private static final Pattern NUMP = Pattern.compile("[0-9]");

	public static HashMap<String, String> FREQDICT = Utils
			.getDictionary("Dicts/VNFREQ.DICT");
	public static HashMap<String, String> UNKNWORDSDICT = Utils
			.getDictionary("Dicts/VNOTHERS.DICT");
	public static HashMap<String, String> VNNAMES = Utils
			.getDictionary("Dicts/VNNAMES.DICT");

	public static void VnInitTagger4Corpus(String inputRawFilePath,
			String outFilePath)
		throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(inputRawFilePath)), "UTF-8"));

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFilePath), "UTF-8"));

		for (String line; (line = buffer.readLine()) != null;) {
			line = line.trim();
			if (line.length() == 0) {
				bw.write("\n");
				continue;
			}
			for (WordTag st : VnInitTagger4Sentence(line))
				bw.write(st.word + "/" + st.tag + " ");
			bw.write("\n");
		}

		buffer.close();
		bw.close();
	}

	public static List<WordTag> VnInitTagger4Sentence(String sentence)
	{
		List<WordTag> wordtags = new ArrayList<WordTag>();

		for (String word : sentence.split(" ")) {
			String tag = "";
			if (FREQDICT.containsKey(word)) {
				tag = FREQDICT.get(word);
			}
			else if (UNKNWORDSDICT.containsKey(word)) {
				tag = UNKNWORDSDICT.get(word);
			}
			else if (VNNAMES.containsKey(word)) {
				tag = "Np";
			}
			else {
				if (NUMP.matcher(word).find()) {
					tag = "M";
				}
				else if (word.length() == 1
						&& Character.isUpperCase(word.charAt(0))) {
					tag = "Y";
				}
				else if (Utils.isAbbre(word)) {
					tag = "Ny";
				}
				else if (Utils.isVnProperNoun(word)) {
					tag = "Np";
				}
				else {
					tag = "N";
				}
			}
			wordtags.add(new WordTag(word, tag));
		}
		return wordtags;
	}

	public static void main(String args[])
		throws IOException
	{
		for (WordTag st : VnInitTagger4Sentence("Chiến_tranh đi qua để lại quê_hương Thái_Mỹ , huyện Củ_Chi LBKT TP._HCM RBKT hộ gia_đình chính_sách và hơn 2.000 ha \" đất thép \" ."))
			System.out.print(st.word + "/" + st.tag + " ");
	}
}
