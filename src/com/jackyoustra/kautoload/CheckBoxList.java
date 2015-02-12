package com.jackyoustra.kautoload;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class CheckBoxList extends JList {
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private int[] prevSelectIndicies = {-1};
	
	public CheckBoxList() {
		setCellRenderer(new CellRenderer());

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(Arrays.equals(getSelectedIndices(), prevSelectIndicies)){
					clearSelection();
				}
				prevSelectIndicies = getSelectedIndices();
				// reset to false
				final int size = getModel().getSize();
				for(int i = 0; i < size; i++){
					JCheckBox checkbox = (JCheckBox) getModel().getElementAt(i);
					checkbox.setSelected(false);
				}
				
				int[] selectedIndicies = getSelectedIndices();
				for(int selectedIndex : selectedIndicies){
					JCheckBox checkbox = (JCheckBox) getModel().getElementAt(selectedIndex);
					checkbox.setSelected(!checkbox.isSelected());
				}
				
				repaint();
				/*
				int index = locationToIndex(e.getPoint());

				if (index != -1) {
					JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
					checkbox.setSelected(!checkbox.isSelected());
					repaint();
				}
				*/
			}
		});

		//setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	protected class CellRenderer implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = (JCheckBox) value;
			checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return checkbox;
		}
	}
}