import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.List;
import java.lang.*;

import JVnRDRPOSTagger.RDRPOSTagger;
import VnDP.VnDependencyParsing;
import jp.ac.jaist.srealizer.algorithms.data.PunctuationRemover;
import jp.ac.jaist.srealizer.properties.Properties;
import jp.ac.jaist.srealizer.utils.CommonUtils;
import jp.ac.jaist.srealizer.utils.FontUtil;
public class Server {
	private static VnDependencyParsing dp;
	public static void main(String[] args) throws IOException {
		Properties.getProperties().trainRealizeSentences(499);
		try {
			dp = VnDependencyParsing.parseWordSegmentedCorpusPreBuild("src/Sample/sample.ws");
			Properties.getProperties().setDp(dp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final int port = 444;
		System.out.println("Server waiting for connection on port "+port);
		ServerSocket ss = new ServerSocket(port);
		while(true){
		Socket clientSocket = ss.accept();
		System.out.println("Recieved connection from "+clientSocket.getInetAddress()+" on port "+clientSocket.getPort());
		//create two threads to send and recieve from client
		RecieveFromClientThread recieve = new RecieveFromClientThread(clientSocket);
		Thread thread = new Thread(recieve);
		thread.start();
		}
		
	}}
class RecieveFromClientThread implements Runnable
{
	Socket clientSocket=null;
	BufferedReader brBufferedReader = null;
	PrintWriter pwPrintWriter;

	public RecieveFromClientThread(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}//end constructor
	public void run() {
		try{
		brBufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));		
		
		String messageString;
	
		while((messageString = brBufferedReader.readLine())!= null){//assign message from client to messageString
			/*if(messageString.equals("EXIT"))
			{
				break;//break to close socket if EXIT
			}*/
			System.out.println("From Client: " + FontUtil.NCR2UTF(messageString));//print the message from client
			//System.out.println("Please enter something to send back to client..");
				messageString = CommonUtils.filterOne(FontUtil.NCR2UTF(messageString));
				pwPrintWriter =new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));//get outputstream
				long t= new Date().getTime();	
				String intputFile ="src/Sample/"+ t  + "test.txt";
			        CommonUtils.writeToFile(FontUtil.NCR2UTF(messageString), intputFile);
			      synchronized (Properties.getProperties().getDp()) {
			    	  VnDependencyParsing.parseWordSegmentedCorpusTest(intputFile, Properties.getProperties().getDp());
				} 
			        List<String[]> cands =  VnDependencyParsing.getCandiates(intputFile);
					String testFile = "data/dependency-tree/test/"+ t +"test_tree.DEP.CONLL";
					PunctuationRemover.removeColl(cands,intputFile + ".DEP.CONLL",testFile  );
					List<String> results = Properties.getProperties().testRealizeSentences(testFile);
					System.out.println("Result: " + results.get(0));
					pwPrintWriter.println( FontUtil.UTF2NCR(results.get(0)));//send message to client with PrintWriter
					pwPrintWriter.flush();//flush the PrintWriter
					//System.out.println("Please enter something to send back to client..");
				String[] deletedFiles ={"test.txt","test.txt.DEP.CONLL","test.txt.TAGGED.CONLL","test.txt.TAGGED"};
				for(String fstr: deletedFiles){
					File f = new File("src/Sample/"+ t  + fstr);
					try{
						f.delete();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
					
		
		
		//this.clientSocket.close();
		//System.exit(0);
	}
		
	}
	catch(Exception ex){System.out.println(ex.getMessage());}
	}
}//end class RecieveFromClientThread
class SendToClientThread implements Runnable
{
	Socket clientSock = null;
	
	public SendToClientThread(Socket clientSock)
	{
		this.clientSock = clientSock;
	}
	public void run() {
	
	}//end run
}//end class SendToClientThread
