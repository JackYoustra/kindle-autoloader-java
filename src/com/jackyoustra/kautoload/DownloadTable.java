package com.jackyoustra.kautoload;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.imgscalr.Scalr;

public class DownloadTable extends JTable {

	private static class ProgressRenderer extends DefaultTableCellRenderer {
		private static BufferedImage image;
	    private final JProgressBar progressBar = new JProgressBar(0, 100);
	    private final JLabel imageLabel;
	    static{
	    	try {
				image = ImageIO.read(MainApp.class.getResourceAsStream("/src/assets/uiactivityindicator_intro.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    public ProgressRenderer(Dimension dimensions) {
	        super();
	        setOpaque(true);
	        int smallest = dimensions.width;
	        if(dimensions.width > dimensions.height){
	        	smallest = dimensions.height;
	        }
	        BufferedImage scaled = Scalr.resize(image, smallest);
	        imageLabel = new JLabel(new ImageIcon(scaled));
	        progressBar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
	        imageLabel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
	        /*
	        Timer t = new Timer();
	        t.schedule(new TimerTask() {
				
				@Override
				public void run() {
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							BufferedImage rotatedPhoto = Scalr.rotate((BufferedImage)((ImageIcon)imageLabel.getIcon()).getImage(), Scalr.Rotation.CW_90, (BufferedImageOp[]) null);
							imageLabel.setIcon(new ImageIcon(rotatedPhoto));
							imageLabel.repaint();
							System.out.println("rotate");
						}
					});
				}
			}, 0, 300);
	        */
	    }
	
	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        if(value instanceof String){
	        	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        }
	        else if(value instanceof Integer){
	        	Integer i = (Integer) value;
	        	if(i == Book.DOWNLOAD_HANG){
	        		return imageLabel;
	        	}
	        	else{
	        		progressBar.setValue(i);
	        		if(i == 100){
	        			progressBar.setStringPainted(true);
	        			progressBar.setString("Complete!");
	        			progressBar.setForeground(Color.blue);
	        		}
	            	return progressBar;
	        	}
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
			return new ProgressRenderer(new Dimension(this.getColumnModel().getColumn(column).getWidth(), getRowHeight()));
		}
		return super.getCellRenderer(row, column);
	}
	
}
