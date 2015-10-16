package jp.ac.jaist.srealizer.algorithms.data;



import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import jp.ac.jaist.srealizer.properties.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;


public class SmartCollectedData {
	public static void collect(String fileout) throws IOException, IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileout),"UTF-8"));

		for(String key : Properties.getProperties().getSearchStats().getStatistics().keySet()){
           try{
        	   for (int k = 0; k< 20; k= k + 4) {
				String address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&hl=en&lr=lang_en&q=";
				key = key.replaceAll("NULL","").trim();
				
				String query = key;
				String charset = "UTF-8";
		         System.out.println("Keyword: " + key);
				URL url = new URL(address + URLEncoder.encode(query, charset));
				Reader reader = new InputStreamReader(url.openStream(), charset);
				GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);
		       // System.err.println(results);
				int total = results.getResponseData().getResults().size();
				//System.out.println("total: "+total);
		 
				// Show title and URL of each results
				for(int i=0; i<=total-1; i++){
					///System.out.println("Title: " + results.getResponseData().getResults().get(i).getTitle());
					//System.out.println("URL: " + results.getResponseData().getResults().get(i).getUrl() + "\n");
					try{
						Document doc = Jsoup.connect( results.getResponseData().getResults().get(i).getUrl()).get();
						Elements es =  doc.getElementsByTag("p");
						if(es != null) {
							String[] sens = es.text().split("([.][\\s\\t\n]+|[?]|!|[.$])");
							for(String s : sens){
								System.out.println(s);
								bw.write(s);
								bw.newLine();
								bw.flush();
							}
						}
						
					}catch(Exception e){
						
					}
				
				}
        	   }
           }catch(Exception e){
        	   
           }
           bw.close();
		}
	}
	class GoogleResults{
		 
	    private ResponseData responseData;
	    public ResponseData getResponseData() { return responseData; }
	    public void setResponseData(ResponseData responseData) { this.responseData = responseData; }
	    public String toString() { return "ResponseData[" + responseData + "]"; }
	 
	    class ResponseData {
	        private List<Result> results;
	        public List<Result> getResults() { return results; }
	        public void setResults(List<Result> results) { this.results = results; }
	        public String toString() { return "Results[" + results + "]"; }
	    }
	 
	    class Result {
	        private String url;
	        private String title;
	        public String getUrl() { return url; }
	        public String getTitle() { return title; }
	        public void setUrl(String url) { this.url = url; }
	        public void setTitle(String title) { this.title = title; }
	        public String toString() { return "Result[url:" + url +",title:" + title + "]"; }
	    }
	}
}
