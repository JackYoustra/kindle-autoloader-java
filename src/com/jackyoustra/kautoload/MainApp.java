package com.jackyoustra.kautoload;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;

import com.jackyoustra.kautoload.DownloadProgress.Downloader.ProgressNotifierDelegate;

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
	
	/** The btn download. */
	private JButton btnDownload;
	
	TrayIcon trayIcon;
	private JTextField txtSearch;
	private JScrollPane scrollPane;
	private JTable table;
	private LibgenTableModel tableModel;

	/**
	 * Launch the application.
	 *
	 * @param args the arguments
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
			        //setDefaultSize(36);
					window.frmKindleAutoloader.setVisible(true);
					window.table.setRowHeight((int) (36*1.2));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void setDefaultSize(int size) {
        Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
        Object[] keys = keySet.toArray(new Object[keySet.size()]);

        for (Object key : keys) {

            if (key != null && key.toString().toLowerCase().contains("font")) {

                System.out.println(key);
                Font font = UIManager.getDefaults().getFont(key);
                if (font != null) {
                    font = font.deriveFont((float)size);
                    UIManager.put(key, font);
                }

            }

        }

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
		}

		frmKindleAutoloader = new JFrame();
		frmKindleAutoloader.setTitle("Kindle Autoloader");
		// set image icons
		List<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/src/assets/KindleXferIcon 128x128.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/src/assets/KindleXferIcon 64x64.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/src/assets/KindleXferIcon 32x32.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/src/assets/KindleXferIcon 16x16.png")));
		frmKindleAutoloader.setIconImages(icons);
		// end set
		frmKindleAutoloader.setBounds(100, 100, 454, 297);
		frmKindleAutoloader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 434 };
		gridBagLayout.rowHeights = new int[] {14, 0, 160};
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
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
		
		txtSearch = new JTextField();
		txtSearch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Book[] books = Libgen.search(txtSearch.getText());
					tableModel.update(books);
				} catch (IOException e1) {
					lblKindleStatus.setText("Internet Error");
					lblKindleStatus.setForeground(Color.RED);
					e1.printStackTrace();
				}
				
			}
		});
		txtSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if(txtSearch.getForeground() == Color.GRAY){
					txtSearch.setText("");
					txtSearch.setForeground(Color.BLACK);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if(txtSearch.getText().equals("")){
					txtSearch.setText("Search");
					txtSearch.setForeground(Color.GRAY);
				}
			}
		});
		
		txtSearch.setForeground(Color.GRAY);
		txtSearch.setText("Search");
		GridBagConstraints gbc_txtSearch = new GridBagConstraints();
		gbc_txtSearch.insets = new Insets(0, 0, 5, 0);
		gbc_txtSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSearch.gridx = 0;
		gbc_txtSearch.gridy = 1;
		frmKindleAutoloader.getContentPane().add(txtSearch, gbc_txtSearch);
		txtSearch.setColumns(10);
		
		tableModel = new LibgenTableModel();
		
		table = new DownloadTable(tableModel);
		
		scrollPane = new JScrollPane(table);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		frmKindleAutoloader.getContentPane().add(scrollPane, gbc_scrollPane);
		
		btnDownload = new JButton("Put on kindle");
		GridBagConstraints gbc_btnDownload = new GridBagConstraints();
		gbc_btnDownload.insets = new Insets(0, 0, 5, 0);
		gbc_btnDownload.gridx = 0;
		gbc_btnDownload.gridy = 3;
		frmKindleAutoloader.getContentPane().add(btnDownload, gbc_btnDownload);
		
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				final int selectedRow = table.getSelectedRow();
				final Book selectedBook = tableModel.getBook(selectedRow);
				final String md5 = selectedBook.getMD5();
				final String dlPath = getKindleDocumentsDirectory() + selectedBook.getFilename();
				final ProgressNotifierDelegate delegate = new ProgressNotifierDelegate() {
					
					@Override
					public void progressChanged(int progress) {
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {
								if(progress == -1){
									System.out.println(progress);
								}
								selectedBook.setProgress(progress);
								tableModel.fireBookProgressUpdated(selectedBook);
							}
						});
					}
				};
				// return true on success, false on failure
				SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
					@Override
					protected Boolean doInBackground(){
						try{
							Libgen.download(md5, dlPath, selectedBook.getSize(), delegate);
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(frmKindleAutoloader, "Book successfuly downloaded to Kindle", "Success", JOptionPane.INFORMATION_MESSAGE);
								}
							});
							return true;
						}catch(IOException e){
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(frmKindleAutoloader, "Error", "Error downloading book", JOptionPane.ERROR_MESSAGE);
								}
							});
							return false;
						}
					}
				};
				worker.execute();
			}
		});
		
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
			trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/src/assets/KindleXferIcon 16x16.png")));
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
		// startup check to be accurate
		kindleConnected = false;
		listenForKindle();
		kindleConnected = true;
		listenForKindle();
		ses.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				listenForKindle();
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	public void maximizeFrame(){
		frmKindleAutoloader.setVisible(true);
		frmKindleAutoloader.setExtendedState(Frame.NORMAL);
	}
	
	private static boolean isMac(){
		return System.getProperty("os.name").contains("Mac OS");
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
		btnDownload.setEnabled(false);
	}

	/**
	 * On kindle connected.
	 */
	private void onKindleConnected() {
		lblKindleStatus.setText("Kindle connected");
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
	}

	/**
	 * Listen for kindle.
	 */
	private void listenForKindle() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File[] f = File.listRoots();
		
		if(isMac()){
			f = new File("/Volumes").listFiles();
		}
		
		boolean success = false;
		for (int i = 0; i < f.length; i++) {
			// check if the drive is a kindle
			if (fsv.getSystemDisplayName(f[i]).toLowerCase().contains("kindle") && f[i].canRead() && f[i].canWrite()) {
				// bet bet that it is
				kindlePath = f[i].getPath();
				success = true;
				if (!kindleConnected) {
					kindleConnected = true;
					onKindleConnected();
				}
			}

			/*
			  System.out.println("Drive: " + f[i]);
			  System.out.println("Display name: " +
			  fsv.getSystemDisplayName(f[i])); System.out.println("Is drive: "
			  + fsv.isDrive(f[i])); System.out.println("Is floppy: " +
			  fsv.isFloppyDrive(f[i])); System.out.println("Readable: " +
			  f[i].canRead()); System.out.println("Writable: " +
			  f[i].canWrite());
			 */
		}
		if (kindleConnected && success == false) {
			kindleConnected = false;
			onKindleDisconnected();
		}
	}

}
