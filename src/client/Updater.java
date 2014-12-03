package client;

import gui.GUI;
import gui.InfoPanel;

public class Updater extends Thread {
	private ClientMonitor mon;
	private GUI gui;

	public Updater(ClientMonitor mon, GUI gui) {
		this.mon = mon;
		this.gui = gui;
	}

	public void run() {
		System.out.println("Updater started");
		while (!isInterrupted()) {
			int type = -1;
			try {
				type = mon.checkUpdate();
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
			if (type == ClientMonitor.IMAGE) {
				System.out.println("update recognized an image update");
				Image image = mon.getImageFromBuffer();
				try {
					gui.setImage(image);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("failed to set image in GUI" + e);
					e.printStackTrace();
				}
			} else if (type == ClientMonitor.COMMAND) {
				System.out.println("update recognized an command update");
				int command = mon.getCommandFromBuffer();
				gui.sendCommandToInfoPanel(command);

			}
		}
	}
}
