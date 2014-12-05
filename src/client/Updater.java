package client;

import gui.GUI;
import gui.InfoPanel;

public class Updater extends Thread {
	private ClientMonitor mon;
	private GUI gui;
	private boolean sync;

	public Updater(ClientMonitor mon, GUI gui) {
		this.mon = mon;
		this.gui = gui;
		sync = false;
	}

	public void run() {
		while (!isInterrupted()) {
			int type = -1;
			try {
				type = mon.checkUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (type == ClientMonitor.COMMAND) {
				int command = mon.getCommandFromUpdaterBuffer();
				gui.sendCommandToInfoPanel(command);

			}else if (type == ClientMonitor.IMAGE) {
				boolean onlyOne = mon.isOnlyOneImage();
				if (onlyOne) {
					Image image = mon.getImageFromBuffer();
					try {
						gui.setImage(image);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Image[] imagesToCompare = mon.getImagesFromBuffers();
					sync = (imagesToCompare[0].isSynchronized()
							&& imagesToCompare[1].isSynchronized());
					if (sync) {
						long diff = imagesToCompare[0].getTimeStamp()
								- imagesToCompare[1].getTimeStamp();
						if (diff > 0) {
							try {
								gui.setImage(imagesToCompare[1]);
								sleep(diff);
								gui.setImage(imagesToCompare[0]);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							diff = imagesToCompare[1].getTimeStamp()
									- imagesToCompare[0].getTimeStamp();
							try {
								gui.setImage(imagesToCompare[0]);
								sleep(diff);
								gui.setImage(imagesToCompare[1]);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						try {
							gui.setImage(imagesToCompare[0]);
							gui.setImage(imagesToCompare[1]);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if (sync) {
					gui.sendCommandToInfoPanel(ClientMonitor.SYNCHRONIZED);
				} else {
					gui.sendCommandToInfoPanel(ClientMonitor.ASYNCHRONIZED);
				}
			}
		}
	}
}
