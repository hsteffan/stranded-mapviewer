/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.view;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/** @author Holger Steffan created: 03.03.2015 */
public class About extends JFrame {

	private URI uri = URI.create("http://mapviewer.hst-soft.de");

	private JPanel contentPane;

	/** Create the frame. */
	public About() {
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("About");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 331, 171);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JButton btnNewButton = new JButton("Close");
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				About.this.dispose();
			}
		});

		JLabel lblStrandedDeepMap = new JLabel("Stranded Deep Map Viewer");
		lblStrandedDeepMap.setFont(new Font("Tahoma", Font.BOLD, 18));

		JLabel lblBy = new JLabel("(c) 2015 by Holger Steffan");
		lblBy.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel lblVersion = new JLabel(MainWindow.VERSION);
		lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
		SpringLayout sl_contentPane = new SpringLayout();
		sl_contentPane.putConstraint(SpringLayout.WEST, lblBy, 0, SpringLayout.WEST, lblVersion);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblBy, 305, SpringLayout.WEST, lblVersion);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblVersion, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblVersion, -5, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblVersion, 6, SpringLayout.SOUTH, lblStrandedDeepMap);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblStrandedDeepMap, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblStrandedDeepMap, 35, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblStrandedDeepMap, 32, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblStrandedDeepMap, -35, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnNewButton, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnNewButton, 0, SpringLayout.EAST, contentPane);
		contentPane.setLayout(sl_contentPane);
		contentPane.add(lblStrandedDeepMap);
		contentPane.add(lblVersion);
		contentPane.add(btnNewButton);
		contentPane.add(lblBy);

		final JLabel lblNewLabel = new JLabel("<HTML>" + uri.toString() + "</HTML>");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblBy, 6, SpringLayout.SOUTH, lblNewLabel);
		lblNewLabel.setForeground(Color.BLUE);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNewLabel, 12, SpringLayout.SOUTH, lblVersion);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel, 0, SpringLayout.WEST, lblVersion);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblNewLabel, 0, SpringLayout.EAST, btnNewButton);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(uri);
					} catch (IOException e1) {
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				lblNewLabel.setText("<HTML><U>" + uri.toString() + "</U></HTML>");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblNewLabel.setText("<HTML>" + uri.toString() + "</HTML>");
			}
		});
		contentPane.add(lblNewLabel);
	}
}
