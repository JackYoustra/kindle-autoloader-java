package com.jackyoustra.kautoload;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jackyoustra.kautoload.DownloadProgress.Downloader;
import com.jackyoustra.kautoload.DownloadProgress.Downloader.ProgressNotifierDelegate;

public class Libgen {
	private static final String mirror = "http://gen.lib.rus.ec/";
	
	/**
	 * Search the libgen database for dl links.
	 *
	 * @param term the term to search
	 * @return the dl url or null if none.
	 * @throws IOException 
	 */
	public static Book[] search(String term) throws IOException{
		URL destWebpage = new URL(mirror +
				"foreignfiction/index.php"+ // subdirectory (fiction only now)
				"?s="+ // prefix for query
				URLEncoder.encode(term, "UTF-8")+ // encode search term properly 
				"&f_lang=English"+  // language type
				"&f_columns=0"+  
				"&f_ext=0");
		URLConnection libgenConnection = destWebpage.openConnection();
		// spoof so it looks legit
		libgenConnection.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)" );
		BufferedReader in = new BufferedReader(new InputStreamReader(libgenConnection.getInputStream()));
		String strLine = "";
		String finalHTML = "";
		//Loop through every line in the source
		while ((strLine = in.readLine()) != null){
		   finalHTML += strLine + "\n";
		}
		
		final Book[] urls = booksFromHTML(finalHTML);
		return urls;
	}
	
	private static Book[] booksFromHTML(String finalHTML) throws MalformedURLException {
		ArrayList<Book> books = new ArrayList<Book>();
		Document doc = Jsoup.parse(finalHTML);
		Elements tables = doc.getElementsByTag("table");
		Element targetTable = null;
		// target: the table after the headings table
		for(int i = 0; i < tables.size(); i++){
			Element table = tables.get(i);
			if(table.getElementsByTag("td").get(0).html().contains("Author(s)")){
				targetTable = tables.get(i+1);
				break;
			}
		}
		
		Elements tableData = targetTable.getElementsByTag("td"); // find all table data elements
		for(Element tableDatum : tableData){
			Element dlTag = tableDatum.getElementById("1");
			if(dlTag != null){
				final String currentLine = dlTag.toString();
				final String lowerCaseLine = currentLine.toLowerCase();
				if(lowerCaseLine.contains("english")){
					Pattern dlPattern = Pattern.compile("/foreignfiction/ads\\.php\\?md5=([a-z]|[0-9])*"); // it's an http request with an md5 arg
					Matcher dlMatches = dlPattern.matcher(currentLine);
					dlMatches.find();
					final String dlLink = dlMatches.group();
					final String md5 = dlLink.substring(dlLink.indexOf("=") + 1);
					final URL dlURL = new URL(mirror + dlLink);
					
					final String titlePrefix = "itle:</td><td>";
					final Pattern titlePattern = Pattern.compile(titlePrefix + "[^<]*");  // <td>Title1:</td><td>The Hunt for Red October</td>
					final Matcher titleMatcher = titlePattern.matcher(currentLine);
					titleMatcher.find();
					String title = titleMatcher.group();
					title = title.substring(titlePrefix.length());
					
					// if no title, replace with series
					if(title.equals("")){
						final String seriesPrefix = "eries:</td><td>";
						final Pattern seriesPattern = Pattern.compile(seriesPrefix + "[^<]*");  // Series:</td><td>To Love a Man</td>
						final Matcher seriesMatcher = seriesPattern.matcher(currentLine);
						seriesMatcher.find();
						String series = seriesMatcher.group();
						series = series.substring(seriesPrefix.length());
						title = series;
					}
					
					final String authorPrefix = "uthor1:</td><td>";
					final Pattern authorPattern = Pattern.compile(authorPrefix + "[^<]*"); // <td>Author1:</td><td>Clancy, Tom</td>
					final Matcher authorMatcher = authorPattern.matcher(currentLine);
					authorMatcher.find();
					String author = authorMatcher.group();
					author = author.substring(authorPrefix.length());
					
					final Pattern extensionPattern = Pattern.compile(">[a-z]*\\([0-9]*.*\\)"); //>epub(854kb)</a>
					final Matcher extensionMatcher = extensionPattern.matcher(currentLine);
					extensionMatcher.find();
					final String extensionSize = extensionMatcher.group();
					String extension = extensionSize.substring(1, extensionSize.indexOf('('));
					
					String sizeNotation = extensionSize.substring(extensionSize.indexOf('(')+1, extensionSize.indexOf(')')).toLowerCase();
					int size = 0;
					if(sizeNotation.indexOf('k') != -1){
						size = Integer.parseInt(sizeNotation.substring(0, sizeNotation.indexOf('k')));
					}
					else if(sizeNotation.indexOf('m') != -1){
						size = Integer.parseInt(sizeNotation.substring(0, sizeNotation.indexOf('m'))) * 1024; // iz megabyte
					}
					
					Book currentBook = new Book(title, author, md5, extension, dlURL, size);
					books.add(currentBook);
				}
			}
		}
		Book[] urls = new Book[books.size()];
		books.toArray(urls);
		return urls;
	}
	
	public static void download(String md5, String path, int expectedLength, ProgressNotifierDelegate delegate) throws IOException{
		URL destWebpage = new URL(mirror + 
				"foreignfiction/" +
				"get.php?" +
				"md5=" +
				md5);
		Downloader dl = new Downloader(path, destWebpage.toString(), expectedLength, delegate);
	}
	
	public static void main(String[] args) {
		try {
			final Book[] results = search("The Hunt for Red October");
			for(Book result : results){
				System.out.println(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
