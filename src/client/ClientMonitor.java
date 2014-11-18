package client;

import java.io.InputStream;
import java.io.OutputStream;

public class ClientMonitor {
	private String command;
	private String image;
	private boolean syncMode;
	private boolean movieMode;
	private boolean updateGUI;

	public ClientMonitor() {
		syncMode = false;
		movieMode = false;
		updateGUI = false;
	}

	/**
	 * Request to update the GUI, meaning an image has been sent and the update
	 * should be notified.
	 */
	public synchronized void updateRequest() {
		updateGUI = true;
		notifyAll();
	}
	/**
	 * Tells the updater to update the GUI when time is due.
	 */
	public synchronized void checkUpdate() {
		while (updateGUI == false) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//TODO do something
		updateGUI = false;
	}

	public void setResult() {
		// The reader sets the command, if one has been received, according to
		// the info received from the inputstream
		// from the cilent socket. String getLine(InputStream s)?
		// If image then the reader does not set the command. but sets the image
	}

	public String getToServerCommand() {
		// The writer gets the command set by the reader.
		// Combine with putLine(OutputStream s, String str)?
		return command;

	}

	public String getFromServerCommand() {
		// The writer gets the command set by the reader.
		// Combine with putLine(OutputStream s, String str)?
		return command;

	}

	public String getImage() {
		// image is set by the reader if an image is received. This method is
		// used by the updater which updates the GUI.
		// The update continously calls getImage() to check if there's a new
		// image.
		// How to distinguish from which camera the image comes from?
		return image;
	}

	public boolean checkSyncMode() {
		// Checks if the cameras are in sync
		// Updater uses this to check if they are synchronous and use this
		// knowledge when displaying the images
		return syncMode;

	}

	public boolean changeModeFlag() {
		// Should be called by the reader when one camera has changed to movie
		// mode.
		// This should trigger the writer to tell the other cameras to change to
		// movie mode as well.<z<
		return movieMode;

	}

}
