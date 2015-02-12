package com.jackyoustra.kautoload;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Library {
	private List<Book> books = new ArrayList<Book>();

	public Library(String path) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		Document dom = db.parse(path);

		Element libraries = dom.getDocumentElement();
		NodeList nodeList = libraries.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nodeLoc = nodeList.item(i);
			if (nodeLoc.getNodeName().equals("Book")) {
				NodeList properties = nodeLoc.getChildNodes();
				String filename = null, author = null, title = null;
				for (int j = 0; j < properties.getLength(); j++) {
					Node currentInfo = properties.item(j);
					if (currentInfo.getNodeName().equals("Title")) {
						title = currentInfo.getTextContent();
					} else if (currentInfo.getNodeName().equals("Author")) {
						author = currentInfo.getTextContent();
					} else if (currentInfo.getNodeName().equals("Filename")) {
						filename = currentInfo.getTextContent();
					} else {
						// nothing needed now, but it will happen
					}
				}
				
				Book newBook = new Book(title, author, filename);
				books.add(newBook);
			}
		}
	}
	

	/**
	 * Gets the books.
	 *
	 * @return A shallow (plz don't edit, things'll break!!!) copy of all of the books in the library
	 */
	public List<Book> getBooks() {
		return new ArrayList<Book>(books);
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Books:\n");
		for (Book b : books) {
			sb.append(b.toString() + "\n");
		}
		return sb.toString();
	}
}
