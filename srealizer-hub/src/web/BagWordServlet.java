package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ac.jaist.realizer.data.builder.ModelBuilder;
import jp.ac.jaist.srealizer.algorithms.data.PunctuationRemover;
import jp.ac.jaist.srealizer.properties.Properties;
import jp.ac.jaist.srealizer.utils.CommonUtils;
import jp.ac.jaist.srealizer.utils.FontUtil;
import VnDP.VnDependencyParsing;

/**
 * Servlet implementation class BagWordServlet
 */
@WebServlet("/BagWordServlet")
public class BagWordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String workfolder;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public void init() throws ServletException {
		workfolder = getServletContext().getInitParameter("work_folder");
		System.out.println(workfolder);
	};

	public BagWordServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String words = request.getParameter("words");
		System.out.println(words);
		if (words.trim().length() != 0) {
			String[] ws = words.trim().split(",");
			System.out.println("Program start at @" + new Date());
			ModelBuilder.initFolder(); // initialization
			StringBuffer bf = new StringBuffer();
			for (int i = 0; i < ws.length; i++)
				bf.append(
						(ws[i].trim().replaceAll("[\\s]+", " ")
								.replaceAll("[\\s]", "_").toLowerCase()))
						.append(" ");
			try {
				Socket sock = new Socket("localhost", 444);
				SendThread sendThread = new SendThread(sock, bf.toString());
				Thread thread = new Thread(sendThread);
				thread.start();
				BufferedReader  recieve = new BufferedReader(new InputStreamReader(
						sock.getInputStream()));// get inputstream
				String msgRecieved = null;
				while ((msgRecieved = recieve.readLine()) == null) {
					
					
					
				}
				System.out.println("here");
				System.out.println("From server: " +FontUtil.NCR2UTF(msgRecieved));
				sock.close();
				request.setAttribute("message", msgRecieved);
				request.setAttribute("words", words);

				request.getRequestDispatcher("index.jsp").forward(request,response);
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}
	}

	
	class SendThread implements Runnable {
		Socket sock = null;
		PrintWriter print = null;
		BufferedReader brinput = null;
		String message;
		public SendThread(Socket sock, String mes) {
			this.sock = sock;
			this.message = mes;
		}// end constructor

		public void run() {
			try {
				if (sock.isConnected()) {
					System.out.println("Client connected to "
							+ sock.getInetAddress() + " on port "
							+ sock.getPort());
					this.print = new PrintWriter(sock.getOutputStream(), true);
					while (true) {
						
						this.print.println( FontUtil.UTF2NCR(message));
						this.print.flush();

							break;
					}// end while
					
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}// end run method
	}// end class
}
