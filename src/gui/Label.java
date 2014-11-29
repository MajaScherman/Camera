package gui;

import java.awt.Color;

import javax.swing.JLabel;

public class Label extends JLabel {

	private JLabel label1, label2, label3;

	public Label(String msg) {
		label1 = new JLabel(msg);
//		label1.setSize(200, 100);
//		label1.setBackground(Color.CYAN);
//	
		label2 = new JLabel(msg);
		label3 = new JLabel(msg);
		
	}
}
