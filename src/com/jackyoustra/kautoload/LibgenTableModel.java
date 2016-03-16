package com.jackyoustra.kautoload;
import javax.swing.table.AbstractTableModel;


public class LibgenTableModel extends AbstractTableModel {

	private Book[] books;
	
	public LibgenTableModel(){
		this.books = new Book[0];
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
		return 3;
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
		default:
			break;
		}
		return null;
	}

}
