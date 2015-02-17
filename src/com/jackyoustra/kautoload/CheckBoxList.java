package com.jackyoustra.kautoload;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private int prevDragIndex = -1;
	private Library underlyingLibrary;
	
	/** Annoying hack to get checkbox working */
	public Map<JCheckBox, Boolean> checkboxHashMap = new HashMap<JCheckBox, Boolean>();
	
	public CheckBoxList(Library lib) {
		setCellRenderer(new CellRenderer());

		underlyingLibrary = lib;
		
		MouseAdapter ma = new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				if(index != prevDragIndex){
					prevDragIndex = index;
					this.mousePressed(e);
				}
			}
			public void mousePressed(MouseEvent e) {
				prevDragIndex = locationToIndex(e.getPoint());
				if(Arrays.equals(getSelectedIndices(), prevSelectIndicies)){
					clearSelection();
					for(int i = 0; i < prevSelectIndicies.length; i++){
						int selectedIndex = prevSelectIndicies[i];
						JCheckBox checkbox = (JCheckBox) getModel().getElementAt(selectedIndex);
						checkbox.setSelected(false);
					}
				}
				prevSelectIndicies = getSelectedIndices();
				// reset to false
				final int size = getModel().getSize();
				for(int i = 0; i < size; i++){
					JCheckBox checkbox = (JCheckBox) getModel().getElementAt(i);
					if(checkbox.isEnabled()){
						checkbox.setSelected(checkbox.isSelected());
					}
				}
				
				int[] selectedIndicies = getSelectedIndices();
				for(int i = 0; i < selectedIndicies.length; i++){
					int selectedIndex = selectedIndicies[i];
					JCheckBox checkbox = (JCheckBox) getModel().getElementAt(selectedIndex);
					if(checkbox.isEnabled()){
						checkbox.setSelected(!checkbox.isSelected());
					}
					else{
						removeSelectionInterval(i, i);
					}
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
			
		};
		
		addMouseListener(ma);
		addMouseMotionListener(ma);

		//setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public Library getUnderlyingLibrary() {
		return underlyingLibrary;
	}

	protected class CellRenderer implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = (JCheckBox) value;
			checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
			if(checkboxHashMap.containsKey(checkbox)){
				checkbox.setEnabled(checkboxHashMap.get(checkbox));
			}
			else{
				checkbox.setEnabled(isEnabled());
			}
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return checkbox;
		}
	}
}