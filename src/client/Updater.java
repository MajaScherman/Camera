package client;

import gui.GUI;

public class Updater extends Thread {
	private ClientMonitor mon;
	private GUI gui;

	public Updater(ClientMonitor mon, GUI gui) {
		this.mon = mon;
		this.gui = gui;
	}

	public void run() {
		while (!isInterrupted()) {
			int type = -1;
			try {
				type = mon.checkUpdate();
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
			if (type == ClientMonitor.IMAGE) {
				Image image = mon.getImageFromBuffer();
				gui.setImage(image);
			} else if (type == ClientMonitor.COMMAND) {
				// TODO update modes an stuff
			}
		}
	}
}
