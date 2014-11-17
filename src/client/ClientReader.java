package client;

import java.io.IOException;
import java.io.InputStream;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ClientReader extends Thread {
	private ClientMonitor monitor;
	private InputStream inputstream;
	private byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];

	public ClientReader(ClientMonitor m, InputStream in) {
		monitor = m;
		inputstream = in;
	}

	private static final byte[] CRLF = { 13, 10 };

	/**
	 * Read a line from InputStream 's', terminated by CRLF. The CRLF is not
	 * included in the returned string.
	 */
	private static String getLine(InputStream s) throws IOException {
		boolean done = false;
		String result = "";

		while (!done) {
			int ch = s.read(); // Read
			if (ch <= 0 || ch == 10) {
				// Something < 0 means end of data (closed socket)
				// ASCII 10 (line feed) means end of line
				done = true;
			} else if (ch >= ' ') {
				result += (char) ch;
			}
		}

		return result;
	}

	public void Run() {
		while (!isInterrupted()) {
			try {
				// Read the first line of the response (status line)
				String responseLine;
				responseLine = getLine(inputstream);
				System.out.println("HTTP server says '" + responseLine + "'.");
				// Ignore the following header lines up to the final empty one.
				do {
					responseLine = getLine(inputstream);
				} while (!(responseLine.equals("")));

				// Now load the JPEG image.
				int bufferSize = jpeg.length;
				int bytesRead = 0;
				int bytesLeft = bufferSize;
				int status;

				// We have to keep reading until -1 (meaning "end of file") is
				// returned. The socket (which the stream is connected to)
				// does not wait until all data is available; instead it
				// returns if nothing arrived for some (short) time.
				do {
					status = inputstream.read(jpeg, bytesRead, bytesLeft);
					// The 'status' variable now holds the no. of bytes read,
					// or -1 if no more data is available
					if (status > 0) {
						bytesRead += status;
						bytesLeft -= status;
					}
				} while (status >= 0);
				// sock.close(); we currently dont have sockets in this class.
				// Should we have it?
				System.out.println("Received image data (" + bytesRead
						+ " bytes).");

			} catch (IOException e) {
				System.out.println("Error when receiving image.");
				return;
			}
		}
	}
}
