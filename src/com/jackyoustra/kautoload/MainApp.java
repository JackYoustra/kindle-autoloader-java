package com.jackyoustra.kautoload;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileSystemView;

public class MainApp {

	private JFrame frame;
	private JLabel lblKindleStatus;
	private boolean kindleConnected = false;

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

		lblKindleStatus = new JLabel("Kindle Not Connected");
		frame.getContentPane().add(lblKindleStatus, BorderLayout.NORTH);
		// constantly check for kindle
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		ses.schedule(new Runnable() {
			@Override
			public void run() {
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
		listenForKindle();
	}
	
	private static String getDocumentsDirectory(){
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();
		return fw.getDefaultDirectory().toString();
	}
	
	private static String getManifestFileLocation(){
		// possibly one level too high for mac
		return getDocumentsDirectory() + File.separator + "ebooks" + File.separator + "thingToRead.xml";
	}

	private void onKindleDisconnected() {
		lblKindleStatus.setText("Kindle not connected!");
	}

	private void onKindleConnected() {
		lblKindleStatus.setText("Kindle connected");
		
		
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
