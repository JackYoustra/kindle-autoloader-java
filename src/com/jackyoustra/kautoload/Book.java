package com.jackyoustra.kautoload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;

public class Book {
	public static final int DOWNLOAD_NOT_STARTED = -1;
	public static final int DOWNLOAD_HANG = -2;
	private String title;
	private String author;
	private String filename;
	private String md5;
	private URL downloadLocation;
	private int size; // in kb
	private int progress = DOWNLOAD_NOT_STARTED;
	
	public Book(String title, String author, String filename) {
		this.title = title;
		this.author = author;
		this.filename = filename;
	}
	
	public Book(String title, String author, String md5, String extension, URL downloadLocation, int size) {
		this.title = title;
		this.author = author;
		this.md5 = md5;
		this.filename = title + "." + extension;
		this.downloadLocation = downloadLocation;
		this.size = size;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getMD5(){
		return md5;
	}
	
	public int getProgress() {
		return progress;
	}
	
	// range: a number between 0-100
	public void setProgress(int progress) {
		// validation
		this.progress = progress;
	}
	
	public String getExtension(){
		return filename.substring(filename.indexOf('.') + 1);
	}
	
	/**
	 * Gets the filename. Format is <title>.<extension>
	 *
	 * @return the filename
	 */
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
		return this.title + "\n" + this.author + "\n" + this.filename + " (" + size + "kb)" + "\nmd5: " + this.md5;
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
