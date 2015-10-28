package JVnRDRPOSTagger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DatQuocNguyen
 * 
 */

/*
 * Define a 5word/tag window object to capture the context of a word
 */
public class FWObject
{
	public List<String> context;

	public FWObject(boolean check)
	{
		// 2ndPreWord, 2ndPreTag, PreWord, PreTag, Word, Tag, NextWord, NextTag,
		// 2ndNextWord, 2ndNextTag
		context = new ArrayList<String>();
		for (int i = 0; i < 10; i++)
			context.add(null);

		if (check == true) {
			for (int i = 0; i < 10; i = i + 2) {
				context.set(i, "<W>");
				context.set(i + 1, "<T>");
			}
		}
	}

	public String toStr()
	{
		String str = "";
		for (int i = 0; i < 10; i = i + 2) {
			str = str + context.get(i) + "/" + context.get(i + 1) + " ";
		}
		return str;
	}
}
