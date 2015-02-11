package com.jackyoustra.kautoload;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

public class MainApp {

	private JFrame frame;
	private JLabel lblKindleStatus;
	private boolean kindleConnected = false;
	private JList<String> BookList;
	private JLabel lblBooks;
	private JTabbedPane tabbedPane;
	private JPanel infopanel;

	/**
	 * Launch the application.
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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 434, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 0, 0, 248, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		lblKindleStatus = new JLabel("Kindle Not Connected");
		lblKindleStatus.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblKindleStatus = new GridBagConstraints();
		gbc_lblKindleStatus.anchor = GridBagConstraints.NORTH;
		gbc_lblKindleStatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblKindleStatus.insets = new Insets(0, 0, 5, 5);
		gbc_lblKindleStatus.gridx = 0;
		gbc_lblKindleStatus.gridy = 0;
		frame.getContentPane().add(lblKindleStatus, gbc_lblKindleStatus);
		
		lblBooks = new JLabel("Books:");
		lblBooks.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblBooks = new GridBagConstraints();
		gbc_lblBooks.insets = new Insets(0, 0, 5, 5);
		gbc_lblBooks.gridx = 0;
		gbc_lblBooks.gridy = 1;
		frame.getContentPane().add(lblBooks, gbc_lblBooks);

		ListModel<String> blm = initBookListModel();
		BookList = new JList<String>(blm);
		GridBagConstraints gbc_BookList_1 = new GridBagConstraints();
		gbc_BookList_1.insets = new Insets(0, 0, 0, 5);
		gbc_BookList_1.fill = GridBagConstraints.BOTH;
		gbc_BookList_1.gridx = 0;
		gbc_BookList_1.gridy = 3;
		frame.getContentPane().add(BookList, gbc_BookList_1);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.gridheight = 4;
		gbc_tabbedPane.gridwidth = 2;
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 1;
		gbc_tabbedPane.gridy = 0;
		frame.getContentPane().add(tabbedPane, gbc_tabbedPane);
		
		infopanel = new JPanel();
		tabbedPane.addTab("Info", null, infopanel, null);
		
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}, 0, TimeUnit.MILLISECONDS);

	}

	ListModel<String> initBookListModel() {
		Library localBooks = null;
		try {
			localBooks = new Library(getManifestFileLocation());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame,
					"Error getting book list!\nPlease restart");
			e.printStackTrace();
			return null;
		}
		List<Book> books = localBooks.getBooks();

		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (Book b : books) {
			listModel.addElement(b.getTitle() + " - " + b.getAuthor());
		}

		return listModel;

	}

	private static String getDocumentsDirectory() {
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();
		return fw.getDefaultDirectory().toString();
	}

	private static String getManifestFileLocation() {
		// possibly one level too high for mac
		return getDocumentsDirectory() + File.separator + "ebooks"
				+ File.separator + "thingToRead.xml";
	}

	private void onKindleDisconnected() {
		lblKindleStatus.setText("Kindle not connected!");
		BookList.setEnabled(false);
	}

	private void onKindleConnected() {
		lblKindleStatus.setText("Kindle connected");
		BookList.setEnabled(true);
	}

	private void listenForKindle() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File[] f = File.listRoots();
		boolean success = false;
		for (int i = 0; i < f.length; i++) {
			// check if the drive is a kindle
			if (fsv.getSystemDisplayName(f[i]).contains("Kindle")
					&& fsv.isDrive(f[i]) && f[i].canRead() && f[i].canWrite()) {
				// bet bet that it is
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
