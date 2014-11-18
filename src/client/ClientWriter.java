package client;

import java.io.IOException;
import java.io.OutputStream;

public class ClientWriter extends Thread {
	private ClientMonitor monitor;
	private OutputStream os;

	public ClientWriter(ClientMonitor mon, OutputStream stream) {
		monitor = mon;
		os = stream;
	}
	
	
	public void run(){
		while(!isInterrupted()){
			//This is far from finished. 
//			What should be done is that we get a command from the monitor and perform different actions depending on the request
			try {
				// Send a simple request, always for "/image.jpg"
				putLine(os, "GET /image.jpg HTTP/1.0");
				putLine(os, "");
			} catch (IOException e) {
				e.printStackTrace();
			}        // The request ends with an empty line
		}
	}
	
	private static final byte[] CRLF      = { 13, 10 };
	
	/**
	 * Send a line on OutputStream 's', terminated by CRLF. The CRLF should not
	 * be included in the string str.
	 */
	private static void putLine(OutputStream s, String str) throws IOException {
		s.write(str.getBytes());
		s.write(CRLF);
	}

}
