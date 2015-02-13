package com.jackyoustra.kautoload;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;

// TODO: Auto-generated Javadoc
/**
 * The Class MainApp.
 */
public class MainApp {

	/** The kindle path. */
	private static String kindlePath;
	
	/** The frame. */
	private JFrame frame;
	
	/** The lbl kindle status. */
	private JLabel lblKindleStatus;
	
	/** The kindle connected. */
	private boolean kindleConnected = false;
	
	/** The Book list. */
	private CheckBoxList BookList;
	
	/** The lbl books. */
	private JLabel lblBooks;
	
	/** The btn download. */
	private JButton btnDownload;

	/**
	 * Launch the application.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 454, 297);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 434, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 0, 160, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		lblKindleStatus = new JLabel("Kindle Not Connected");
		lblKindleStatus.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblKindleStatus = new GridBagConstraints();
		gbc_lblKindleStatus.anchor = GridBagConstraints.NORTH;
		gbc_lblKindleStatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblKindleStatus.insets = new Insets(5, 0, 5, 0);
		gbc_lblKindleStatus.gridx = 0;
		gbc_lblKindleStatus.gridy = 0;
		frame.getContentPane().add(lblKindleStatus, gbc_lblKindleStatus);
		
		lblBooks = new JLabel("Books:");
		lblBooks.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblBooks = new GridBagConstraints();
		gbc_lblBooks.insets = new Insets(0, 0, 5, 0);
		gbc_lblBooks.gridx = 0;
		gbc_lblBooks.gridy = 1;
		frame.getContentPane().add(lblBooks, gbc_lblBooks);

		
		Library localBooks = null;
		try {
			localBooks = new Library(getManifestFileLocation());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame,
					"Error getting book list!\nPlease restart");
			e.printStackTrace();
		}
		
		List<Book> books = localBooks.getBooks();
		List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>(books.size());
		for(Book currentBook : books){
			checkBoxList.add(new JCheckBox(currentBook.getDisplayTitle()));
		}
		BookList = new CheckBoxList(localBooks);
		JCheckBox[] b = new JCheckBox[checkBoxList.size()];
		b = checkBoxList.toArray(b);
		BookList.setListData(b);   // set the list data for the object
		
		btnDownload = new JButton("Put on kindle");
		GridBagConstraints gbc_btnDownload = new GridBagConstraints();
		gbc_btnDownload.gridx = 0;
		gbc_btnDownload.gridy = 3;
		frame.getContentPane().add(btnDownload, gbc_btnDownload);
		
		final Library lambdaLocalBooks = localBooks;
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// download button clicked
				for(Book currentBook : lambdaLocalBooks.getBooks()){
					for(int currentIndex : BookList.getSelectedIndices()){
						JCheckBox currentCheckBox = (JCheckBox) BookList.getModel().getElementAt(currentIndex);
						if(currentCheckBox.getText().equals(currentBook.getDisplayTitle())){
							// same thing, can download book
							try {
								currentBook.saveToPath(getKindleDocumentsDirectory(), getEbooksFileFolder());
								refreshKindleList();
								currentCheckBox.setSelected(false);
								BookList.clearSelection();
							} catch (IOException e) {
								JOptionPane.showMessageDialog(frame,
										"Error saving book list!");
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
		
		
		GridBagConstraints gbc_BookList_1 = new GridBagConstraints();
		gbc_BookList_1.insets = new Insets(0, 0, 5, 0);
		gbc_BookList_1.fill = GridBagConstraints.BOTH;
		gbc_BookList_1.gridx = 0;
		gbc_BookList_1.gridy = 2;
		frame.getContentPane().add(BookList, gbc_BookList_1);
		
		// constantly check for kindle
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		ses.schedule(new Runnable() {
			@Override
			public void run() {
				// startup check to be accurate
				kindleConnected = false;
				listenForKindle();
				kindleConnected = true;
				listenForKindle();
				while (true) {
					listenForKindle();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, 0, TimeUnit.MILLISECONDS);

	}

	/**
	 * Gets the documents directory.
	 *
	 * @return the documents directory
	 */
	private static String getDocumentsDirectory() {
		// possibly one level too high for mac
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();
		return fw.getDefaultDirectory().toString() + File.separator;
	}
	
	/**
	 * Gets the ebooks file folder.
	 *
	 * @return the ebooks file folder
	 */
	private static String getEbooksFileFolder(){
		return getDocumentsDirectory() + "ebooks" + File.separator;
	}

	/**
	 * Gets the manifest file location.
	 *
	 * @return the manifest file location
	 */
	private static String getManifestFileLocation() {
		return getEbooksFileFolder() + "thingToRead.xml";
	}
	
	/**
	 * Gets the kindle documents directory.
	 *
	 * @return the kindle documents directory
	 */
	private static String getKindleDocumentsDirectory(){
		return kindlePath + File.separator + "documents" + File.separator;
	}

	/**
	 * On kindle disconnected.
	 */
	private void onKindleDisconnected() {
		lblKindleStatus.setText("Kindle not connected!");
		BookList.setSelectedIndex(-1);
		BookList.setEnabled(false);
		btnDownload.setEnabled(false);
	}

	/**
	 * On kindle connected.
	 */
	private void onKindleConnected() {
		lblKindleStatus.setText("Kindle connected");
		BookList.setEnabled(true);
		btnDownload.setEnabled(true);
		
		refreshKindleList();
	}

	/**
	 * Refresh the eligibility of books based on the loaded state of the kindle.
	 */
	private void refreshKindleList() {
		List<Book> bookList = BookList.getUnderlyingLibrary().getBooks();
		
		for(int i = 0; i < bookList.size(); i++){
			Book currentBook = bookList.get(i);
			JCheckBox currentElement = (JCheckBox) BookList.getModel().getElementAt(i);
			
			System.out.println(getKindleDocumentsDirectory());
			File[] kindleDocuments = new File(getKindleDocumentsDirectory()).listFiles();
			for(File currentFile : kindleDocuments){
				final String[] pathComponents = currentFile.toString().split("\\\\");
				final String finalPathName = pathComponents[pathComponents.length-1];
				if(finalPathName.equals(currentBook.getFilename())){
					// is equal, plz disable
					BookList.checkboxHashMap.put(currentElement, false);
					//currentElement.setEnabled(false);
					break;
				}
				else{
					BookList.checkboxHashMap.put(currentElement, true);
					//currentElement.setEnabled(true);
				}
			}
			System.out.println(currentElement.isEnabled());
		}
		BookList.repaint();
	}

	/**
	 * Listen for kindle.
	 */
	private void listenForKindle() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File[] f = File.listRoots();
		boolean success = false;
		for (int i = 0; i < f.length; i++) {
			// check if the drive is a kindle
			
			if (fsv.getSystemDisplayName(f[i]).toLowerCase().contains("kindle")
					&& fsv.isDrive(f[i]) && f[i].canRead() && f[i].canWrite()) {
				// bet bet that it is
				kindlePath = f[i].getPath();
				success = true;
				if (!kindleConnected) {
					kindleConnected = true;
					onKindleConnected();
				}
			}

			/*
			 * System.out.println("Drive: " + f[i]);
			 * System.out.println("Display name: " +
			 * fsv.getSystemDisplayName(f[i])); System.out.println("Is drive: "
			 * + fsv.isDrive(f[i])); System.out.println("Is floppy: " +
			 * fsv.isFloppyDrive(f[i])); System.out.println("Readable: " +
			 * f[i].canRead()); System.out.println("Writable: " +
			 * f[i].canWrite());
			 */
		}
		if (kindleConnected && success == false) {
			kindleConnected = false;
			onKindleDisconnected();
		}
	}

}
