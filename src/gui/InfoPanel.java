package gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class InfoPanel extends JPanel {

	private JLabel label2, label3, label4, delay1, delay2;

	private TitledBorder delay1Title, delay2Title;
	public InfoPanel() {

		Border blackline = BorderFactory.createLineBorder(Color.black);

		label2 = new JLabel("Idle Mode");
		label2.setBorder(blackline);
		label2.setFont(label2.getFont().deriveFont(50f));
		label2.setHorizontalAlignment(SwingConstants.CENTER);

		label3 = new JLabel("Asyncronized");
		label3.setBorder(blackline);
		label3.setFont(label3.getFont().deriveFont(50f));
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		
		label4 = new JLabel("Auto Mode");
		label4.setBorder(blackline);
		label4.setFont(label4.getFont().deriveFont(50f));
		label4.setHorizontalAlignment(SwingConstants.CENTER);

		delay1 = new JLabel("  ");
		delay1.setFont(label3.getFont().deriveFont(50f));
		delay1.setHorizontalAlignment(SwingConstants.CENTER);
		
		delay1Title = BorderFactory.createTitledBorder(blackline, "CAMERA 1 DELAY");
		delay1Title.setTitleJustification(TitledBorder.CENTER);
		delay1.setBorder(delay1Title);
		
		delay2 = new JLabel(" ");
		delay2.setFont(label3.getFont().deriveFont(50f));
		delay2.setHorizontalAlignment(SwingConstants.CENTER);
		
		delay2Title = BorderFactory.createTitledBorder(blackline, "CAMERA 2 DELAY");
		delay2Title.setTitleJustification(TitledBorder.CENTER);
		delay2.setBorder(delay2Title);

		setLayout(new GridLayout(5, 1));
		
		add(delay1);
		add(delay2);
		add(label2);
		add(label3);
		add(label4);
	}

	public void setLabelText(int labelIndex, String text) {
		if (labelIndex < 0 && labelIndex > 4) {
			System.out.println("The label index is too high or too low.");
		}

		switch (labelIndex) {
		case 0:
			delay1.setText(text);
			break;
		case 1:
			delay2.setText(text);
			break;
		case 2:
			label2.setText(text);
			break;
		case 3:
			label3.setText(text);
			break;
		case 4:
			label4.setText(text);
			break;
		}
	}
}
