package gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
	private DelayPanel delay1, delay2;
	

	private byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
	public static final int MAXIMUM_NUMBER_OF_CAMERAS = 2;


	public GUI(ClientMonitor mon) {
		super();
		frame = new JFrame();
		frame.setSize(1500, 600);
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Delay panel
//		delay1 = new DelayPanel();
//		frame.getContentPane().add(delay1);
//		
//		delay2 = new DelayPanel();
//		frame.getContentPane().add(delay2);
//		
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
		if (image.getCameraNbr() < 0 || image.getCameraNbr() > MAXIMUM_NUMBER_OF_CAMERAS) {
			System.out.println("Camera index is out of range");
			throw new Exception("Camera index is out of range");
		}
		switch (image.getCameraNbr()) {
		case 0:
			imageViewer0.refresh(image.getImage());
			infoPanel.setLabelText(0, Long.toString(image.getDelay()));
			break;
		case 1:
			imageViewer1.refresh(image.getImage());
			infoPanel.setLabelText(1, Long.toString(image.getDelay()));
			break;
		}

	}

	public void sendCommandToInfoPanel(int command) {
		switch (command) {
		case ClientMonitor.MOVIE_MODE:		
			infoPanel.setLabelText(3, "Movie Mode");
			buttonPanel.setButtonText(1, "IDLE MODE");
			break;
		case ClientMonitor.ASYNCHRONIZED:
			infoPanel.setLabelText(2, "Asynchronized Mode");
			buttonPanel.setButtonText(2, "SYNCHRONIZED MODE");

			break;
		case ClientMonitor.SYNCHRONIZED:
			infoPanel.setLabelText(3, "Synchronized Mode");
			buttonPanel.setButtonText(2, "ASYNCHRONIZED");

			break;
		default:
			break;
		}
		
	}

}
