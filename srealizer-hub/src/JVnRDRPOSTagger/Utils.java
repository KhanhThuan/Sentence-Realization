package JVnRDRPOSTagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author DatQuocNguyen
 * 
 */
public class Utils
{
	// Return the list of pair (word, tag) from a sentence initialized by
	// (an external) initial tagger
	public static List<WordTag> getWordTagList(String initializedSentence)
	{
		List<WordTag> wordTagList = new ArrayList<WordTag>();
		for (String wordTag : initializedSentence.split(" ")) {
			wordTag = wordTag.trim();
			if (wordTag.length() == 0)
				continue;
			if (wordTag.equals("///"))
				wordTagList.add(new WordTag("/", "/"));
			else {
				int index = wordTag.lastIndexOf("/");
				wordTagList.add(new WordTag(wordTag.substring(0, index),
						wordTag.substring(index + 1)));
			}
		}
		return wordTagList;
	}

	public static HashMap<String, String> getDictionary(String inputFileName)
	{
		HashMap<String, String> dict = new HashMap<String, String>();
		BufferedReader buffer;
		try {
			URL url = Utils.class.getResource(inputFileName);
			buffer = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(url.getPath())), "UTF-8"));
			for (String line; (line = buffer.readLine()) != null;) {
				// System.out.println(line);
				String[] wordTag = line.split(" ");
				dict.put(wordTag[0], wordTag[1]);
			}
			buffer.close();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dict;
	}

	public static boolean isVnProperNoun(String word)
	{
		// System.out.println(word);
		if (Character.isUpperCase(word.charAt(0))) {
			if (word.split("_").length >= 5)// more than 5 tokens => likely to
											// be a proper noun
				return true;
			int index = word.indexOf("_");
			while (index >= 0) {
				if (Character.isLowerCase(word.charAt(index + 1))) {
					return false;
				}
				index = word.indexOf("_", index + 1);
			}
			return true;
		}
		else
			return false;

	}

	public static boolean isAbbre(String word)
	{
		for (int i = 0; i < word.length(); i++) {
			if (Character.isLowerCase(word.charAt(i)) || word.charAt(i) == '_')
				return false;
		}
		return true;
	}

	public static FWObject getCondition(String strCondition)
	{
		FWObject condition = new FWObject(false);

		for (String rule : strCondition.split(" and ")) {
			rule = rule.trim();
			String key = rule.substring(rule.indexOf(".") + 1,
					rule.indexOf(" "));
			String value = getConclusion(rule);

			if (key.equals("prevWord2")) {
				condition.context.set(0, value);
			}
			if (key.equals("prevTag2")) {
				condition.context.set(1, value);
			}
			if (key.equals("prevWord1")) {
				condition.context.set(2, value);
			}
			if (key.equals("prevTag1")) {
				condition.context.set(3, value);
			}
			if (key.equals("word")) {
				condition.context.set(4, value);
			}
			if (key.equals("tag")) {
				condition.context.set(5, value);
			}
			if (key.equals("nextWord1")) {
				condition.context.set(6, value);
			}
			if (key.equals("nextTag1")) {
				condition.context.set(7, value);
			}
			if (key.equals("nextWord2")) {
				condition.context.set(8, value);
			}
			if (key.equals("nextTag2")) {
				condition.context.set(9, value);
			}
		}

		return condition;
	}

	public static FWObject getObject(List<WordTag> wordtags, int size, int index)
	{
		FWObject object = new FWObject(true);

		if (index > 1) {
			object.context.set(0, wordtags.get(index - 2).word);
			object.context.set(1, wordtags.get(index - 2).tag);
		}

		if (index > 0) {
			object.context.set(2, wordtags.get(index - 1).word);
			object.context.set(3, wordtags.get(index - 1).tag);
		}

		object.context.set(4, wordtags.get(index).word);
		object.context.set(5, wordtags.get(index).tag);

		if (index < size - 1) {
			object.context.set(6, wordtags.get(index + 1).word);
			object.context.set(7, wordtags.get(index + 1).tag);
		}

		if (index < size - 2) {
			object.context.set(8, wordtags.get(index + 2).word);
			object.context.set(9, wordtags.get(index + 2).tag);
		}

		return object;
	}

	public static String getConclusion(String strConclusion)
	{
		// Additional process to extract wor/tag pairs
		// object.preTag1 == "" or object.nextWord2= "" ...
		if (strConclusion.contains("\"\"")) {
			if (strConclusion.contains("Word"))
				return "<W>";
			else
				return "<T>"; // strConclusion.contains("Tag")
		}
		// conclusion in rule: object.conclusion = "Nc"
		String conclusion = strConclusion.substring(
				strConclusion.indexOf("\"") + 1, strConclusion.length() - 1);
		return conclusion;
	}

	public static void main(String args[])
	{
	}
}
