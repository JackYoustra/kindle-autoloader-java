package com.jackyoustra.kautoload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;

public class Book {
	private String title;
	private String author;
	private String filename;
	private URL downloadLocation;
	private int size; // in kb
	
	public Book(String title, String author, String filename) {
		this.title = title;
		this.author = author;
		this.filename = filename;
	}
	
	public Book(String title, String author, String filename, URL downloadLocation, int size) {
		this.title = title;
		this.author = author;
		this.filename = filename;
		this.downloadLocation = downloadLocation;
		this.size = size;
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

	public URL getDownloadLocation() {
		return downloadLocation;
	}
	
	public int getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.title + "\n" + this.author + "\n" + this.filename + " (" + size + "kb)";
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
