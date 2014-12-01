package client;

import gui.GUI;

public class Updater extends Thread {
	private ClientMonitor mon;
	private GUI gui;

	// check if there's a new image
	public Updater(ClientMonitor mon, GUI gui) {
		this.mon = mon;
		this.gui = gui;
	}

	public void run() {
		while (!isInterrupted()) {
//			int type = mon.checkUpdate(); // Check if new image has arrived
			// Here we know that its time to update the GUI with new image.
			// So we should just refresh image in GUI.
			// get image
			// push image to gui
//			if(type==image){
			//TODO Fixa image struktur
//				byte[] image = mon.getImageFromBuffer();
//				gui.setImage(mon.getCameraIndex(),image);
////				skicka timestamp eller delay till gui
//			}else{
//				push mode sträng grejs;
//			}
		}
	}
}
