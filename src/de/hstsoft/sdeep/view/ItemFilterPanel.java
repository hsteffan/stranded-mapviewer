/** Copyright © 2015 Holger Steffan H.St. Soft
 * 
 * This file is subject to the terms and conditions defined in file 'license', which is part of this source code
 * package. */
package de.hstsoft.sdeep.view;

import java.awt.BorderLayout;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JPanel;

/** @author Holger Steffan created: 01.03.2015 */
public class ItemFilterPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public ItemFilterPanel() {
		setLayout(new BorderLayout(0, 0));

		JList<String> list = new JList<String>();
		list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String[] values = new String[] { "Blah", "Blub", "Dingens" };

			public int getSize() {
				return values.length;
			}

			public String getElementAt(int index) {
				return values[index];
			}
		});
		add(list, BorderLayout.CENTER);
	}
}
