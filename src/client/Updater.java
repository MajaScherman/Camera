package client;

import gui.GUI;
import gui.InfoPanel;

public class Updater extends Thread {
	private ClientMonitor mon;
	private GUI gui;
	private InfoPanel infoPanel;

	public Updater(ClientMonitor mon, GUI gui, InfoPanel infoPanel) {
		this.mon = mon;
		this.gui = gui;
		this.infoPanel = infoPanel;
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
				switch (command) {
				case ClientMonitor.MOVIE_MODE:
					infoPanel.setLabelText(2, "Movie Mode");
					break;
				case ClientMonitor.ASYNCHRONIZED:
					infoPanel.setLabelText(1, "Asynchronized Mode");
					break;
				case ClientMonitor.SYNCHRONIZED:
					infoPanel.setLabelText(1, "Synchronized Mode");
					break;
				default:
					break;
				}

			}
		}
	}
}
