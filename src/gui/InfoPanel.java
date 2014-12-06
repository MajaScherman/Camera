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

	private JLabel label1, label2, label3, delay1, delay2;

	private TitledBorder delay1Title, delay2Title;
	public InfoPanel() {

		Border blackline = BorderFactory.createLineBorder(Color.black);

		label1 = new JLabel("Idle Mode");
		label1.setBorder(blackline);
		label1.setFont(label1.getFont().deriveFont(50f));
		label1.setHorizontalAlignment(SwingConstants.CENTER);

		label2 = new JLabel("Asyncronized");
		label2.setBorder(blackline);
		label2.setFont(label2.getFont().deriveFont(50f));
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		
		label3 = new JLabel("Auto Mode");
		label3.setBorder(blackline);
		label3.setFont(label3.getFont().deriveFont(50f));
		label3.setHorizontalAlignment(SwingConstants.CENTER);

		delay1 = new JLabel("  ");
		delay1.setFont(label2.getFont().deriveFont(50f));
		delay1.setHorizontalAlignment(SwingConstants.CENTER);
		
		delay1Title = BorderFactory.createTitledBorder(blackline, "CAMERA 1 DELAY");
		delay1Title.setTitleJustification(TitledBorder.CENTER);
		delay1.setBorder(delay1Title);
		
		delay2 = new JLabel(" ");
		delay2.setFont(label2.getFont().deriveFont(50f));
		delay2.setHorizontalAlignment(SwingConstants.CENTER);
		
		delay2Title = BorderFactory.createTitledBorder(blackline, "CAMERA 2 DELAY");
		delay2Title.setTitleJustification(TitledBorder.CENTER);
		delay2.setBorder(delay2Title);

		setLayout(new GridLayout(5, 1));
		
		add(delay1);
		add(delay2);
		add(label1);
		add(label2);
		add(label3);
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
		case 2: //the label on the top movie/idle
			label1.setText(text);
			break;
		case 3://sync/async
			label2.setText(text);
			break;
		case 4://Forced/auto
			label3.setText(text);
			break;
		}
	}
}
