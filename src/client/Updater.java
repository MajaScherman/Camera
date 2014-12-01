package client;

import gui.GUI;

public class Updater extends Thread {
	private ClientMonitor monitor;
	private GUI gui;

	// check if there's a new image
	public Updater(ClientMonitor mon, GUI gui) {
		monitor = mon;
		this.gui = gui;
	}

	public void run() {
		while (!isInterrupted()) {
			monitor.checkUpdate(); // Check if new image has arrived
			// Here we know that its time to update the GUI with new image.
			// So we should just refresh image in GUI.
			// get image
			// push image to gui
		}
	}
}
