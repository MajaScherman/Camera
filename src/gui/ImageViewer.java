package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class ImageViewer extends JPanel {
	private ImageIcon icon;
	private JLabel imageIcon, label1, label2;

	public ImageViewer() {
		super();
		icon = new ImageIcon();
		imageIcon = new JLabel(icon);
		//label2 = new JLabel("Java Code Geeks", icon, JLabel.CENTER);
		this.setSize(200, 200);
		
		Border blackline = BorderFactory.createLineBorder(Color.black);

		label1 = new JLabel("Delay");
		label1.setBorder(blackline);
		label1.setFont(label1.getFont().deriveFont(50f));
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		setLayout(new BorderLayout());
		// TODO set visible to label1!!!!!!

		add(imageIcon);
		//add(label2);

	}

	public void refresh(byte[] data) {
		Image theImage = getToolkit().createImage(data);
		getToolkit().prepareImage(theImage, -1, -1, null);
		icon.setImage(theImage);
		imageIcon.setText("GE MIG TEXT");
		icon.paintIcon(this, this.getGraphics(), 5, 5);
	}

}
