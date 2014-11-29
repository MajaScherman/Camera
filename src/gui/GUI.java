package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class GUI extends JFrame {
	private ImageViewer imageViewer1, imageViewer2;
	private JFrame frame;
	private InfoPanel infoPanel;
	private ButtonPanel buttonPanel;

	private byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
	
	public static void main(String[] args){
		GUI gui = new GUI();
	}

	public GUI() {
		super();
		frame = new JFrame();
		frame.setSize(1000, 600);
		frame.getContentPane().setLayout(new GridLayout(2,2));
		Border blackline = BorderFactory.createLineBorder(Color.black);
		
		//Image panel for the first camera
		imageViewer1 = new ImageViewer();
		imageViewer1.setBackground(Color.GRAY);
		imageViewer1.setBorder(blackline);
		frame.getContentPane().add(imageViewer1);
		
		//Image panel for the second camera
		imageViewer2 = new ImageViewer();
		imageViewer2.setBackground(Color.GRAY);
		imageViewer2.setBorder(blackline);
		frame.getContentPane().add(imageViewer2);
		
		//The panel containing information about states
		infoPanel = new InfoPanel();
		frame.getContentPane().add(infoPanel);
		
		//Panel containing the buttons
		buttonPanel = new ButtonPanel();
		frame.getContentPane().add(buttonPanel);
		frame.setVisible(true);
	}

	

}

