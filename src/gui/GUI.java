package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class GUI extends JFrame {
	private ImagePanel imagePanel;
	private JFrame frame;

	private byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
	
	public static void main(String[] args){
		GUI gui = new GUI();
	}

	public GUI() {
		super();
		frame = new JFrame();
		frame.setSize(1000, 600);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.CYAN);
		panel.setSize(200, 200);
		frame.add(panel);
//		imagePanel = new ImagePanel();
//		this.getContentPane().setLayout(new BorderLayout());
//		this.getContentPane().add(imagePanel, BorderLayout.NORTH);
//		this.setLocationRelativeTo(null);
//		this.pack();
//		frame.add(imagePanel);
		frame.setVisible(true);
//		refreshImage();
	}

	public void refreshImage() {
		imagePanel.refresh(jpeg);
	}

}

class ImagePanel extends JPanel {
	ImageIcon icon;

	public ImagePanel() {
		super();
		icon = new ImageIcon();
		JLabel label = new JLabel(icon);
		add(label, BorderLayout.CENTER);
		this.setSize(200, 200);
	}

	public void refresh(byte[] data) {
		Image theImage = getToolkit().createImage(data);
		getToolkit().prepareImage(theImage, -1, -1, null);
		icon.setImage(theImage);
		icon.paintIcon(this, this.getGraphics(), 5, 5);
	}
}
