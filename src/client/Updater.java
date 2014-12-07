package client;

import gui.GUI;
import gui.InfoPanel;

public class Updater extends Thread {
	private ClientMonitor mon;
	private GUI gui;
	private boolean sync;
	private long transferDelayS1, transferDelayS2;
	private long relativeDelayS1, relativeDelayS2;
	private Image[] previousImage;
	private final static long THRESHOLD = (long) 0.2;

	public Updater(ClientMonitor mon, GUI gui) {
		this.mon = mon;
		this.gui = gui;
		sync = false;
		transferDelayS1 = transferDelayS2 = 0;
		relativeDelayS1 = relativeDelayS2 = 0;
		previousImage = new Image[2];
	}

	public void run() {
		while (!isInterrupted()) {
			try {
				DataForUpdater data = mon.getUpdateData();

				if (data.getType() == ClientMonitor.COMMAND) {
					if (data.isForceMode()) {
						if (data.isMovieMode()) {
							int com1 = ClientMonitor.MOVIE_MODE;
							gui.sendCommandToInfoPanel(com1);
						} else {
							int com2 = ClientMonitor.IDLE_MODE;
							gui.sendCommandToInfoPanel(com2);

						}

					} else {
						gui.sendCommandToInfoPanel(data.getCommand());
					}

				} else if (data.getType() == ClientMonitor.IMAGE) {
					if (data.isOnlyOneImage()) {
						Image[] image = data.getImage();
						if (image[0].getCameraNbr() == 0) {

							sync = relativeDelayS1 < THRESHOLD;
						} else {
							sync = relativeDelayS2 < THRESHOLD;
						}
						if (sync) {
							// if there is no previous image for cameraIndex
							// image[0].getCameraNbr() then set previous image
							// to the image
							if (previousImage[image[0].getCameraNbr()] == null) {
								previousImage[image[0].getCameraNbr()] = image[0];
							} else {
								long diff = 0;
								if (image[0].getCameraNbr() == 0) {
									relativeDelayS1 = image[0].getTimeStamp()
											- previousImage[image[0]
													.getCameraNbr()]
													.getTimeStamp();
									transferDelayS1 = System
											.currentTimeMillis()
											- image[0].getTimeStamp();
									image[0].setDelay(transferDelayS1);
									diff = relativeDelayS1 - transferDelayS1;
								} else {
									relativeDelayS2 = image[0].getTimeStamp()
											- previousImage[image[0]
													.getCameraNbr()]
													.getTimeStamp();
									transferDelayS2 = System
											.currentTimeMillis()
											- image[0].getTimeStamp();
									image[0].setDelay(transferDelayS2);
									diff = relativeDelayS2 - transferDelayS2;
								}
								previousImage[image[0].getCameraNbr()] = image[0];
								if (diff > 0) {
									sleep(diff);
								}
								gui.setImage(image[0]);
							}
						} else {// async
							if (image[0].getCameraNbr() == 0) {
								transferDelayS1 = System.currentTimeMillis()
										- image[0].getTimeStamp();
								image[0].setDelay(transferDelayS1);
							} else {
								transferDelayS2 = System.currentTimeMillis()
										- image[0].getTimeStamp();
								image[0].setDelay(transferDelayS2);
							}
							previousImage[image[0].getCameraNbr()] = image[0];
							gui.setImage(image[0]);
						}

					} else {
						Image[] imagesToCompare = data.getImage();
						if (previousImage[imagesToCompare[0].getCameraNbr()] == null) {
							previousImage[imagesToCompare[0].getCameraNbr()] = imagesToCompare[0];
						}
						if (previousImage[imagesToCompare[1].getCameraNbr()] == null) {
							previousImage[imagesToCompare[1].getCameraNbr()] = imagesToCompare[1];
						}

						if (data.isForceMode()) {
							switch (data.isSyncMode()) {

							case 1:// sync
								setImagesInGUISync(imagesToCompare);
								break;
							case 2:// async
								try {
									gui.setImage(imagesToCompare[0]);
									gui.setImage(imagesToCompare[1]);
								} catch (Exception e) {
									e.printStackTrace();
								}
								gui.sendCommandToInfoPanel(ClientMonitor.ASYNCHRONIZED);
								break;
							}
						} else {
							// auto
							sync = relativeDelayS1 < THRESHOLD
									&& relativeDelayS2 < THRESHOLD;

							if (sync) {
								setImagesInGUISync(imagesToCompare);
							} else {
								try {
									gui.setImage(imagesToCompare[0]);
									gui.setImage(imagesToCompare[1]);
								} catch (Exception e) {
									e.printStackTrace();
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
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void setImagesInGUISync(Image[] imagesToCompare) {
		long comparisonDelay = imagesToCompare[0].getTimeStamp()
				- imagesToCompare[1].getTimeStamp();
		if (comparisonDelay > 0) {
			try {
				transferDelayS2 = System.currentTimeMillis()
						- imagesToCompare[1].getTimeStamp();
				relativeDelayS2 = imagesToCompare[1].getTimeStamp()
						- previousImage[1].getTimeStamp();
				long oneCameraDiffS2 = relativeDelayS2 - transferDelayS2;
				if (oneCameraDiffS2 > 0) {
					sleep(oneCameraDiffS2);
					comparisonDelay -= oneCameraDiffS2;
				}
				imagesToCompare[1].setDelay(transferDelayS2);
				previousImage[1] = imagesToCompare[1];
				gui.setImage(imagesToCompare[1]);
				if (comparisonDelay > 0) {
					sleep(comparisonDelay);
				}
				transferDelayS1 = System.currentTimeMillis()
						- imagesToCompare[0].getTimeStamp();
				relativeDelayS1 = imagesToCompare[1].getTimeStamp()
						- previousImage[0].getTimeStamp();
				long oneCameraDiffS1 = relativeDelayS1 - transferDelayS1;
				if (oneCameraDiffS1 > 0) {
					sleep(oneCameraDiffS1);
					comparisonDelay -= oneCameraDiffS1;
				}

				imagesToCompare[0].setDelay(transferDelayS1);
				previousImage[0] = imagesToCompare[0];
				gui.setImage(imagesToCompare[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			comparisonDelay = imagesToCompare[1].getTimeStamp()
					- imagesToCompare[0].getTimeStamp();
			try {
				transferDelayS1 = System.currentTimeMillis()
						- imagesToCompare[0].getTimeStamp();
				relativeDelayS1 = imagesToCompare[1].getTimeStamp()
						- previousImage[0].getTimeStamp();
				long oneCameraDiffS1 = relativeDelayS1 - transferDelayS1;
				if (oneCameraDiffS1 > 0) {
					sleep(oneCameraDiffS1);
					comparisonDelay -= oneCameraDiffS1;
				}
				imagesToCompare[0].setDelay(transferDelayS1);
				previousImage[0] = imagesToCompare[0];
				gui.setImage(imagesToCompare[0]);
				if (comparisonDelay > 0) {
					sleep(comparisonDelay);
				}
				transferDelayS2 = System.currentTimeMillis()
						- imagesToCompare[1].getTimeStamp();
				relativeDelayS2 = imagesToCompare[1].getTimeStamp()
						- previousImage[1].getTimeStamp();
				long oneCameraDiffS2 = relativeDelayS2 - transferDelayS2;
				if (oneCameraDiffS2 > 0) {
					sleep(oneCameraDiffS2);
					comparisonDelay -= oneCameraDiffS2;
				}
				imagesToCompare[1].setDelay(transferDelayS2);
				previousImage[1] = imagesToCompare[1];
				gui.setImage(imagesToCompare[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		gui.sendCommandToInfoPanel(ClientMonitor.SYNCHRONIZED);

	}
}
