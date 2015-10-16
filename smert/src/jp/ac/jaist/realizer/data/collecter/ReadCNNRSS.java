package jp.ac.jaist.realizer.data.collecter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReadCNNRSS {
	public static void main(String[] args) throws InterruptedException, IOException {
		String[] rss = {"edition_world.rss","edition_africa.rss","edition_americas.rss",
				"edition_asia.rss","edition_europe.rss","edition_meast.rss","edition_us.rss",
				"money_news_international.rss","edition_technology.rss","edition_space.rss",
				"edition_entertainment.rss","edition_sport.rss","edition_football.rss","edition_golf.rss","edition_motorsport.rss",
				"edition_tennis.rss","edition_travel.rss","cnn_freevideo.rss"};
        String host ="http://rss.cnn.com/rss/";
    

		Map<String, Integer> links = new HashMap<String,Integer>();
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("en/data/accessed-links.txt"),"UTF-8"));
			String l = null;
			while( (l = br.readLine()) != null){
				links.put(l.trim(), 1);
			}
			br.close();
		}catch(IOException e){
			
		}
		

		while(true){
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("en/data/words-data.in",true),"UTF-8"));
			BufferedWriter bwl = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("en/data/accessed-links.txt",true),"UTF-8"));

			for(String r : rss){
				RSSFeedParser parser = new RSSFeedParser(
						 host + r);
				Feed feed = parser.readFeed();
				for (FeedMessage message : feed.getMessages()) {
					if(!links.containsKey(message.getLink().trim())){
						links.put(message.getLink().trim(), 1);
						bwl.write(message.getLink().trim());
						bwl.newLine();
                        bwl.flush();
						System.out.println("Title: " + message.getTitle() + "\n" + message.getLink());
						
						try {
							Document doc = Jsoup.connect(message.getLink()).get();
							//System.out.println(doc);
							if(doc != null ){
								    
									Elements e1 = doc.getElementsByClass("pg-headline");
									
									//System.out.println("text: " + e2.text());
									if(e1 != null){
										bw.write(e1.text());
										bw.newLine();
										bw.flush();

									}
									Element e2 = doc.getElementById("body-text");
									
									if(e2 != null){
										Elements remvElements = e2.getElementsByClass("el__storyhighlights_wrapper");
										String removedText = "";
										if(remvElements != null) removedText = remvElements.text();
										System.out.println("removed:" + removedText);
										String gainedText = e2.text();
										gainedText = gainedText.substring(removedText.length());
										bw.write(gainedText);
										
										bw.newLine();
										bw.flush();

									}
								
									
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			
					}
				}
		
				}
			bw.close();
			bwl.close();
			Thread.sleep(2*3600*1000);

			}
		}
	
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        