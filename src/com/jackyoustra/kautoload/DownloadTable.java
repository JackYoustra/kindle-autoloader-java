package com.jackyoustra.kautoload;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class DownloadTable extends JTable {


	private static class ProgressRenderer extends DefaultTableCellRenderer {
	
	    private final JProgressBar progressBar = new JProgressBar(0, 100);
	
	    public ProgressRenderer() {
	        super();
	        setOpaque(true);
	        progressBar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
	    }
	
	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        if(value instanceof String){
	        	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        }
	        else if(value instanceof Integer){
	        	Integer i = (Integer) value;
	            progressBar.setValue(i);
	            return progressBar;
	        }
	        else{
	        	System.err.println("unexpected output");
	        }
	        return this;
	    }
	}
	
	public DownloadTable() {
		super();
	}
	
	public DownloadTable(TableModel dm) {
		super(dm);
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if(column == getModel().getColumnCount()-1){
			return new ProgressRenderer();
		}
		return super.getCellRenderer(row, column);
	}
	
}
