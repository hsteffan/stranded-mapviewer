/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import de.hstsoft.sdeep.NoteManager;
import de.hstsoft.sdeep.model.Atmosphere;
import de.hstsoft.sdeep.model.Note;

/** @author Holger Steffan created: 22.03.2015 */
public class NoteDlg extends JFrame {
	private JTextField txtTitle;
	private JLabel lblDate;
	private Note note;
	private JButton btnDelete;
	private JTextArea txtText;
	private NoteManager mgr;

	/** Create the frame. */
	public NoteDlg() {
		setType(Type.UTILITY);
		setAlwaysOnTop(true);
		setTitle("Note");
		setBounds(100, 100, 300, 225);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel pBottom = new JPanel();
		pBottom.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(pBottom, BorderLayout.SOUTH);
		pBottom.setLayout(new BoxLayout(pBottom, BoxLayout.X_AXIS));

		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		btnDelete.setVisible(false);
		pBottom.add(btnDelete);

		Component horizontalGlue = Box.createHorizontalGlue();
		pBottom.add(horizontalGlue);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		pBottom.add(btnSave);

		JPanel pHead = new JPanel();
		FlowLayout fl_pHead = (FlowLayout) pHead.getLayout();
		fl_pHead.setAlignment(FlowLayout.LEFT);
		getContentPane().add(pHead, BorderLayout.NORTH);

		lblDate = new JLabel("Date: 2014-03-27 Day: 5");
		pHead.add(lblDate);

		JPanel pCenter = new JPanel();
		getContentPane().add(pCenter, BorderLayout.CENTER);

		JLabel lblTitle = new JLabel("Title:");

		txtTitle = new JTextField();
		txtTitle.setColumns(30);

		JLabel lblMessage = new JLabel("Message:");

		txtText = new JTextArea(5, 100);
		txtText.setWrapStyleWord(true);
		txtText.setLineWrap(true);

		GroupLayout gl_pCenter = new GroupLayout(pCenter);
		gl_pCenter.setHorizontalGroup(gl_pCenter.createParallelGroup(Alignment.TRAILING).addGroup(
				gl_pCenter
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_pCenter
										.createParallelGroup(Alignment.TRAILING)
										.addComponent(txtText, Alignment.LEADING, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGroup(
												Alignment.LEADING,
												gl_pCenter.createSequentialGroup().addComponent(lblTitle)
														.addPreferredGap(ComponentPlacement.UNRELATED)
														.addComponent(txtTitle, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addComponent(lblMessage, Alignment.LEADING)).addContainerGap()));
		gl_pCenter.setVerticalGroup(gl_pCenter.createParallelGroup(Alignment.LEADING).addGroup(
				gl_pCenter
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_pCenter
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblTitle)
										.addComponent(txtTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lblMessage).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(txtText, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)));
		pCenter.setLayout(gl_pCenter);

	}

	protected void delete() {
		mgr.remove(note);
		close();
	}

	private boolean update = false;

	private void close() {
		this.setVisible(false);
		this.dispose();
	}

	protected void save() {
		note.setTitle(txtTitle.getText());
		note.setText(txtText.getText());
		if (update)
			mgr.updateNote(note);
		else
			mgr.addNote(note);
		close();
	}

	public NoteDlg(NoteManager mgr, Note note) {
		this();
		update = true;
		this.mgr = mgr;
		this.note = note;
		btnDelete.setVisible(true);

		this.txtTitle.setText(note.getTitle());
		this.txtText.setText(note.getText());

		setHeader(note.getDaysSurvived(), note.getYear(), note.getMonth(), note.getDay());
	}

	public NoteDlg(NoteManager mgr, Atmosphere atmos, Point2D position) {
		this();
		update = false;
		this.mgr = mgr;
		this.note = new Note();
		this.note.setDaysSurvived(atmos.getDaysElapsed());
		this.note.setDay(atmos.getDay());
		this.note.setMonth(atmos.getMonth());
		this.note.setYear(atmos.getYear());
		this.note.setPosition(position);
		setHeader(note.getDaysSurvived(), note.getYear(), note.getMonth(), note.getDay());
	}

	private void setHeader(int dayselapsed, int year, int month, int day) {
		String header = String.format("Day: %d Date: %d-%d-%d", dayselapsed, year, month, day);
		lblDate.setText(header);
	}

}
