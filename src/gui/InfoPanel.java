package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class InfoPanel extends JPanel {

	private JLabel  label2, label3;

	public InfoPanel() {


		Border blackline = BorderFactory.createLineBorder(Color.black);
		

		label2 = new JLabel("Sync Mode");
		label2.setBorder(blackline);
		label2.setFont(label2.getFont().deriveFont(50f));
		label2.setHorizontalAlignment(SwingConstants.CENTER);

		label3 = new JLabel("Idle Mode");
		label3.setBorder(blackline);
		label3.setFont(label3.getFont().deriveFont(50f));
		label3.setHorizontalAlignment(SwingConstants.CENTER);

		setLayout(new GridLayout(2, 1));
	
		add(label2);
		add(label3);
	}

	public void setLabelText(int labelIndex, String text) {
		if (labelIndex < 1 && labelIndex > 3) {
			System.out.println("The label index is too high or too low.");
		}

		switch (labelIndex) {
		
		case 1:
			label2.setText(text);
			break;
		case 2:
			label3.setText(text);
			break;
		}
	}
}
