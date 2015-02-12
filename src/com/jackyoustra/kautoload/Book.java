package com.jackyoustra.kautoload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

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

	public String getDisplayTitle() {
		return this.getTitle() + " - " + this.getAuthor();
	}

	public void saveToPath(String kindlePath, String ebookRelativeDirectory) throws IOException {
		File bookFile = new File(kindlePath + filename);
		OutputStream out = null;
		try{
			out = new FileOutputStream(bookFile);
			if(!bookFile.exists()){
				bookFile.createNewFile();
			}
			RandomAccessFile f = new RandomAccessFile(bookFile, "rw");
			byte[] b = new byte[(int)f.length()];
			f.read(b);
			f.close();
			out.write(b); // write file
		}
		finally{
			out.close();
		}
	}
}
