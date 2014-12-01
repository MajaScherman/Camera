package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import client.ButtonHandler;
import client.ClientMonitor;
import se.lth.cs.eda040.fakecamera.AxisM3006V;

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

	public void setImage(int cameraIndex, byte[] image) {
		if (cameraIndex < 0 || cameraIndex > MAXIMUM_NUMBER_OF_CAMERAS) {
			System.out.println("Camera index is out of range");
			// TODO Throw exception
		}
		switch (cameraIndex) {
		case 0:
			imageViewer0.refresh(image);
			break;
		case 1:
			imageViewer1.refresh(image);
			break;
		}


	}

}
