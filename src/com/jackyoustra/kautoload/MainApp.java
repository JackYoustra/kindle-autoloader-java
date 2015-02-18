package com.jackyoustra.kautoload;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;

import java.awt.Toolkit;

// TODO: Auto-generated Javadoc
/**
 * The Class MainApp.
 */
public class MainApp {

	/** The kindle path. */
	private static String kindlePath;
	
	/** The frame. */
	private JFrame frmKindleAutoloader;
	
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
	
	TrayIcon trayIcon;

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
					window.frmKindleAutoloader.setVisible(true);
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
		// mac stuff
		if(isMac()){
			// take the menu bar off the jframe
			System.setProperty("apple.laf.useScreenMenuBar", "true");

			// set the name of the application menu item
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Kindle Autoloader");
			// set the look and feel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		frmKindleAutoloader = new JFrame();
		frmKindleAutoloader.setTitle("Kindle Autoloader");
		// set image icons
		List<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/assets/KindleXferIcon 128x128.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/assets/KindleXferIcon 64x64.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/assets/KindleXferIcon 32x32.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/assets/KindleXferIcon 16x16.png")));
		frmKindleAutoloader.setIconImages(icons);
		// end set
		frmKindleAutoloader.setBounds(100, 100, 454, 297);
		frmKindleAutoloader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 434, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 0, 160, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		frmKindleAutoloader.getContentPane().setLayout(gridBagLayout);

		lblKindleStatus = new JLabel("Kindle Not Connected");
		lblKindleStatus.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblKindleStatus = new GridBagConstraints();
		gbc_lblKindleStatus.anchor = GridBagConstraints.NORTH;
		gbc_lblKindleStatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblKindleStatus.insets = new Insets(5, 0, 5, 0);
		gbc_lblKindleStatus.gridx = 0;
		gbc_lblKindleStatus.gridy = 0;
		frmKindleAutoloader.getContentPane().add(lblKindleStatus, gbc_lblKindleStatus);
		
		lblBooks = new JLabel("Books:");
		lblBooks.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblBooks = new GridBagConstraints();
		gbc_lblBooks.insets = new Insets(0, 0, 5, 0);
		gbc_lblBooks.gridx = 0;
		gbc_lblBooks.gridy = 1;
		frmKindleAutoloader.getContentPane().add(lblBooks, gbc_lblBooks);

		Library localBooks = null;
		try {
			localBooks = new Library(getManifestFileLocation());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frmKindleAutoloader,
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
		frmKindleAutoloader.getContentPane().add(btnDownload, gbc_btnDownload);
		
		final Library lambdaLocalBooks = localBooks;
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// download button clicked
				for(Book currentBook : lambdaLocalBooks.getBooks()){
					for(int currentIndex = 0; currentIndex < BookList.getModel().getSize(); currentIndex++){
						JCheckBox currentCheckBox = (JCheckBox) BookList.getModel().getElementAt(currentIndex);
						if(currentCheckBox.isSelected()){
							if(currentCheckBox.getText().equals(currentBook.getDisplayTitle())){
								// same thing, can download book
								try {
									currentBook.saveToPath(getKindleDocumentsDirectory(), getEbooksFileFolder());
									refreshKindleList();
									currentCheckBox.setSelected(false);
									BookList.clearSelection();
								} catch (IOException e) {
									JOptionPane.showMessageDialog(frmKindleAutoloader,
											"Error saving book list!");
									e.printStackTrace();
								}
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
		frmKindleAutoloader.getContentPane().add(BookList, gbc_BookList_1);
		
		frmKindleAutoloader.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmKindleAutoloader.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frmKindleAutoloader.setVisible(false);
			}
		});
		
		// create popup menu
		if(SystemTray.isSupported()){
			final PopupMenu popup = new PopupMenu();
			trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/assets/KindleXferIcon 16x16.png")));
			trayIcon.setImageAutoSize(true);
			final SystemTray tray = SystemTray.getSystemTray();
			
			MenuItem openItem = new MenuItem("Open");
			
			openItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					maximizeFrame();
				}
			});
			
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			
			popup.add(openItem);
			popup.add(exitItem);
			
			trayIcon.setPopupMenu(popup);
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				System.out.println("tray icon not added");
				e.printStackTrace();
			}
		}
		
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

	public void maximizeFrame(){
		frmKindleAutoloader.setVisible(true);
		frmKindleAutoloader.setExtendedState(Frame.NORMAL);
	}
	
	private static boolean isMac(){
		return System.getProperty("os.name").contains("Mac OS");
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
		if(isMac()){
			return fw.getDefaultDirectory().toString() + File.separator + "Documents" + File.separator;
		}
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
		
		if(!frmKindleAutoloader.isVisible()){
			
			trayIcon.displayMessage("Kindle Connected", "A Kindle has been connected", MessageType.INFO);
			trayIcon.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					maximizeFrame();
				}
			});
		}
		
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
