package com.jackyoustra.kautoload;
import javax.swing.table.AbstractTableModel;


public class LibgenTableModel extends AbstractTableModel {

	private Book[] books;
	private String[] columnNames = {"Title", "Author", "Size (kb)", "Extension"};
	
	public LibgenTableModel(){
		this(new Book[0]);
	}
	
	public LibgenTableModel(Book[] books) {
		this.books = books;
	}
	
	public void update(Book[] books){
		this.books = books;
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return books.length;
	}

	@Override
	public int getColumnCount() {
		return 4;
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public Book getBook(int row){
		return books[row];
	}
	
	public void fireBookProgressUpdated(Book b){
		for(int i = 0; i < books.length; i++){
			if(books[i] == b){
				fireTableCellUpdated(i, 3); // progess bar changed
				break;
			}
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final Book currentBook = books[rowIndex];
		switch (columnIndex) {
		case 0:
			return currentBook.getTitle();
		case 1:
			return currentBook.getAuthor();
		case 2:
			return currentBook.getSize();
		case 3:
			if(currentBook.getProgress() == Book.DOWNLOAD_NOT_STARTED){
				return currentBook.getExtension();
			}
			else{
				return currentBook.getProgress();
			}
		default:
			break;
		}
		return null;
	}

}
