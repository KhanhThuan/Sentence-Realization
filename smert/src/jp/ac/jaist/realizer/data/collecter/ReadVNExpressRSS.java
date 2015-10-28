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

public class ReadVNExpressRSS {
	public static void main(String[] args) throws InterruptedException, IOException {
		String[] rss = {"phap-luat.rss","thoi-su.rss","kinh-doanh.rss","the-gioi.rss","the-thao.rss","giao-duc.rss","suc-khoe.rss","gia-dinh.rss","du-lich.rss","khoa-hoc.rss","so-hoa.rss","oto-xe-may.rss","cong-dong.rss","tam-su.rss","cuoi.rss"};
        String host ="http://vnexpress.net/rss/";
    

		Map<String, Integer> links = new HashMap<String,Integer>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/accessed-links.txt"),"UTF-8"));
		String l = null;
		while( (l = br.readLine()) != null){
			links.put(l.trim(), 1);
		}
		br.close();

		while(true){
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/words-data.in",true),"UTF-8"));
			BufferedWriter bwl = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/accessed-links.txt",true),"UTF-8"));

			for(String r : rss){
				RSSFeedParser parser = new RSSFeedParser(
						 host + r);
				Feed feed = parser.readFeed();
				System.out.println(feed);
				for (FeedMessage message : feed.getMessages()) {
					if(!links.containsKey(message.getLink().trim())){
						links.put(message.getLink().trim(), 1);
						bwl.write(message.getLink().trim());
						bwl.newLine();
                        bwl.flush();
						System.out.println(message.getTitle() + "\n" + message.getLink());
						try {
							bw.write(message.getTitle());
							bw.newLine();
							String s = message.getDescription().replaceAll("<a.*</br>", "");
							bw.write(s);
							bw.flush();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							Document doc = Jsoup.connect(message.getLink()).get();
							if(doc != null ){
								Element e1 = doc.getElementById("left_calculator");
								
								if(e1 != null){
									Elements e2 = e1.getElementsByClass("fck_detail");
									
									
									bw.newLine();
									if(e2 != null)
										bw.write(e2.text());
									else bw.write(e1.text());
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
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  