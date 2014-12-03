package gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import se.lth.cs.eda040.fakecamera.AxisM3006V;
import client.ButtonHandler;
import client.ClientMonitor;
import client.Image;

public class GUI extends JFrame {
	private ImageViewer imageViewer0, imageViewer1;
	private JFrame frame;
	private InfoPanel infoPanel;
	private ButtonPanel buttonPanel;
	private ButtonHandler buttonHandler;
	

	private byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
	public static final int MAXIMUM_NUMBER_OF_CAMERAS = 2;


	public GUI(ClientMonitor mon) {
		super();
		frame = new JFrame();
		frame.setSize(1000, 600);
		frame.getContentPane().setLayout(new GridLayout(2, 2));
		Border blackline = BorderFactory.createLineBorder(Color.black);

		// Image panel for the first camera
		imageViewer0 = new ImageViewer();
		imageViewer0.setBackground(Color.GRAY);
		imageViewer0.setBorder(blackline);
		frame.getContentPane().add(imageViewer0);

		// Image panel for the second camera
		imageViewer1 = new ImageViewer();
		imageViewer1.setBackground(Color.GRAY);
		imageViewer1.setBorder(blackline);
		frame.getContentPane().add(imageViewer1);

		// The panel containing information about states
		infoPanel = new InfoPanel();
		frame.getContentPane().add(infoPanel);
		buttonHandler = new ButtonHandler(mon,infoPanel);
		//Panel containing the buttons
		buttonPanel = new ButtonPanel(mon, infoPanel);
		frame.getContentPane().add(buttonPanel);
		frame.setVisible(true);
	}

	public void setImage(Image image) throws Exception {
		System.out.println("at least I got into setImage method in gUI");
		if (image.getCameraNbr() < 0 || image.getCameraNbr() > MAXIMUM_NUMBER_OF_CAMERAS) {
			System.out.println("Camera index is out of range");
			throw new Exception("Camera index is out of range");
		}
		switch (image.getCameraNbr()) {
		case 0:
			System.out.println("sets image in first panel...");
			imageViewer0.refresh(image.getImage());
			break;
		case 1:
			System.out.println("sets image in second panel...");
			imageViewer1.refresh(image.getImage());
			break;
		}

	}

	public void sendCommandToInfoPanel(int command) {
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
