package com.jackyoustra.kautoload;

public class Book {
	private String title;
	private String author;
	private String filename;
	
	public Book(String title, String author, String filename) {
		this.title = title;
		this.author = author;
		this.filename = filename;
	}
	
	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getFilename() {
		return filename;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.title + "\n" + this.author + "\n" + this.filename;
	}
}
