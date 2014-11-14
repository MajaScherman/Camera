package server;

import java.io.IOException;
import java.io.OutputStream;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ServerWriter {
	private OutputStream os;
	private int serverPort; // TCP port for HTTP server
	private AxisM3006V myCamera; // Makes up the JPEG images
	private byte[] jpeg;
	private ServerMonitor monitor;
	
	// By convention, these bytes are always sent between lines
	// (CR = 13 = carriage return, LF = 10 = line feed)

	private static final byte[] CRLF = { 13, 10 };
	
	public ServerWriter(OutputStream os, int port, ServerMonitor monitor){
		this.os = os;
		this.monitor = monitor;
		 jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		serverPort = port;
		myCamera = new AxisM3006V();
		myCamera.init();
		myCamera.setProxy("argus-1.student.lth.se", port);
	}
	public void run() throws IOException{
		String oldReq = "";
		/**
		 * Unsure of how to handle the request. Quite messy code right now.
		 * 
		 */
		while(monitor.getRequest().equals(oldReq)){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		// Interpret the request. Complain about everything but GET.
		// Ignore the file name.
		String request = "";
		try{
		request = monitor.getRequest();
		}catch(Exception e){
			//...this is not good? 
		}
		if (request.substring(0, 4).equals("GET ")) {
			// Got a GET request. Respond with a JPEG image from the
			// camera. Tell the client not to cache the image
			putLine(os, "HTTP/1.0 200 OK");
			putLine(os, "Content-Type: image/jpeg");
			putLine(os, "Pragma: no-cache");
			putLine(os, "Cache-Control: no-cache");
			putLine(os, ""); // Means 'end of header'

			if (!myCamera.connect()) {
				System.out.println("Failed to connect to camera!");
				System.exit(1);
			}
			int len = myCamera.getJPEG(jpeg, 0);

			os.write(jpeg, 0, len);
			myCamera.close();
		} else {
			// Got some other request. Respond with an error message.
			putLine(os, "HTTP/1.0 501 Method not implemented");
			putLine(os, "Content-Type: text/plain");
			putLine(os, "");
			putLine(os, "No can do. Request '" + request
					+ "' not understood.");

			System.out.println("Unsupported HTTP request!");
		}
		oldReq = request;
		os.flush(); // Flush any remaining content
		//clientSocket.close(); // Disconnect from the client
	}
	/**
	 * Send a line on OutputStream 's', terminated by CRLF. The CRLF should not
	 * be included in the string str.
	 */
	private static void putLine(OutputStream s, String str) throws IOException {
		s.write(str.getBytes());
		s.write(CRLF);
	}

}
