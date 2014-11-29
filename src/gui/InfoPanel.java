package gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class InfoPanel extends JPanel {

	private JLabel label1, label2, label3;

	public InfoPanel() {
		label1 = new JLabel("Delay");
		Border blackline = BorderFactory.createLineBorder(Color.black);
		label1.setBorder(blackline);
		label2 = new JLabel("Sync Mode");
		label2.setBorder(blackline);
		label3 = new JLabel("Movie/Idle");
		label3.setBorder(blackline);
		setLayout(new GridLayout(3, 1));
		add(label1);
		add(label2);
		add(label3);
	}

	public void setLabelText(int labelIndex, String text) {
		if (labelIndex < 0 && labelIndex > 3) {
			System.out.println("The label index is too high or too low.");
		}
		switch (labelIndex) {
		case 0:
			label1.setText(text);
			break;
		case 1:
			label2.setText(text);
			break;
		case 2:
			label3.setText(text);
			break;
		}
	}
}
