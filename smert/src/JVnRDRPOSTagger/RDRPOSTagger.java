package JVnRDRPOSTagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * @author DatQuocNguyen
 * 
 */
public class RDRPOSTagger
{
	public static final String MODELPATH = RDRPOSTagger.class.getResource(
			"VNPOSTAGGING.RDR").getPath();

	public Node root;

	public RDRPOSTagger()
	{

	}

	public RDRPOSTagger(Node node)
	{
		root = node;
	}

	// Build an scrdr-based tree for pos tagging from a learned model file
	// containing rules
	public void constructTreeFromRulesFile(String rulesFilePath)
		throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(rulesFilePath)), "UTF-8"));
		String line = buffer.readLine();

		this.root = new Node(new FWObject(false), "NN", null, null, null, 0);

		Node currentNode = this.root;
		int currentDepth = 0;

		for (; (line = buffer.readLine()) != null;) {
			int depth = 0;
			for (int i = 0; i <= 5; i++) { // supposed that the maximum
											// exception level is up to 5.
				if (line.charAt(i) == '\t')
					depth += 1;
				else
					break;
			}

			line = line.trim();
			if (line.length() == 0)
				continue;

			if (line.contains("cc:"))
				continue;

			// System.out.println(line);
			FWObject condition = Utils
					.getCondition(line.split(" : ")[0].trim());
			String conclusion = Utils
					.getConclusion(line.split(" : ")[1].trim());

			Node node = new Node(condition, conclusion, null, null, null, depth);

			if (depth > currentDepth) {
				currentNode.setExceptNode(node);
			}
			else if (depth == currentDepth) {
				currentNode.setIfnotNode(node);
			}
			else {
				while (currentNode.depth != depth)
					currentNode = currentNode.fatherNode;
				currentNode.setIfnotNode(node);
			}
			node.setFatherNode(currentNode);

			currentNode = node;
			currentDepth = depth;
		}
		buffer.close();
	}

	public Node findFiredNode(FWObject object)
	{
		Node currentN = root;
		Node firedN = null;
		while (true) {
			if (currentN.satisfy(object)) {
				firedN = currentN;
				if (currentN.exceptNode == null) {
					break;
				}
				else {
					currentN = currentN.exceptNode;
				}
			}
			else {
				if (currentN.ifnotNode == null) {
					break;
				}
				else {
					currentN = currentN.ifnotNode;
				}
			}

		}

		return firedN;
	}

	// Tag Vietnamese word-segmented sentence
	public static String tagVnWSSentence(String sentence)
		throws IOException
	{
		RDRPOSTagger tree = new RDRPOSTagger();
		tree.constructTreeFromRulesFile(MODELPATH);

		StringBuilder sb = new StringBuilder();

		String newSen = sentence.replace("“", "''").replace("”", "''")
				.replace("\"", "''");

		// Call initial tagger for Vietnamese
		List<WordTag> wordtags = InitialTagger_Vn.VnInitTagger4Sentence(newSen);

		String[] words = sentence.split(" ");
		int size = words.length;

		for (int i = 0; i < size; i++) {
			FWObject object = Utils.getObject(wordtags, size, i);
			Node firedNode = tree.findFiredNode(object);
			if (words[i].equals("RBKT") || words[i].equals("LBKT")) {
				sb.append(words[i] + "/" + words[i] + " ");
			}
			else if (firedNode.conclusion.equals("CH")) {
				if (words[i].equals("?") || words[i].equals("!"))
					sb.append(words[i] + "/. ");
				else
					sb.append(words[i] + "/" + words[i] + " ");
			}
			else if (firedNode.conclusion.equals("Cc")) {
				sb.append(words[i] + "/C ");
			}
			else if (firedNode.conclusion.equals("Z")) {
				sb.append(words[i] + "/S ");
			}
			else if (firedNode.conclusion.equals("Ni")) {
				sb.append(words[i] + "/Ny ");
			}
			else {
				sb.append(words[i] + "/" + firedNode.conclusion + " ");
			}
		}
		return sb.toString();
	}

	/*
	 * Tag Vietnamese word-segmented corpus.
	 */
	public static void tagVnWSCorpus(String inRawFilePath)
		throws IOException
	{
		RDRPOSTagger tree = new RDRPOSTagger();
		tree.constructTreeFromRulesFile(MODELPATH);

		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(inRawFilePath)), "UTF-8"));

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(inRawFilePath + ".TAGGED"), "UTF-8"));

		for (String line; (line = buffer.readLine()) != null;) {
			StringBuilder sb = new StringBuilder();

			line = line.trim();
			if (line.length() == 0) {
				bw.write("\n");
				continue;
			}

			String newLine = line.replace("“", "''").replace("”", "''")
					.replace("\"", "''");

			// Call initial tagger for Vietnamese
			List<WordTag> wordtags = InitialTagger_Vn
					.VnInitTagger4Sentence(newLine);

			String[] words = line.split(" ");
			int size = words.length;

			for (int i = 0; i < size; i++) {
				FWObject object = Utils.getObject(wordtags, size, i);
				Node firedNode = tree.findFiredNode(object);
				if (words[i].equals("RBKT") || words[i].equals("LBKT")) {
					sb.append(words[i] + "/" + words[i] + " ");
				}
				else if (firedNode.conclusion.equals("CH")) {
					if (words[i].equals("?") || words[i].equals("!"))
						sb.append(words[i] + "/. ");
					else
						sb.append(words[i] + "/" + words[i] + " ");
				}
				else if (firedNode.conclusion.equals("Cc")) {
					sb.append(words[i] + "/C ");
				}
				else if (firedNode.conclusion.equals("Z")) {
					sb.append(words[i] + "/S ");
				}
				else if (firedNode.conclusion.equals("Ni")) {
					sb.append(words[i] + "/Ny ");
				}
				else {
					sb.append(words[i] + "/" + firedNode.conclusion + " ");
				}
			}
			bw.write(sb.toString() + "\n");
		}
		buffer.close();
		bw.close();
	}
}
