package com.jackyoustra.kautoload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

//http://stackoverflow.com/questions/2263062/how-to-monitor-progress-jprogressbar-with-filechannels-transferfrom-method
public class DownloadProgress {
	// example usage
	/*
    public static void main( String[] args ) {
        new Downloader( "/tmp/foo.mp3", "http://foo.com/bar.mp3" );
    }
    */

    public interface RBCWrapperDelegate {
        // The RBCWrapperDelegate receives rbcProgressCallback() messages
        // from the read loop.  It is passed the progress as a percentage
        // if known, or -1.0 to indicate indeterminate progress.
        // 
        // This callback hangs the read loop so a smart implementation will
        // spend the least amount of time possible here before returning.
        // 
        // One possible implementation is to push the progress message
        // atomically onto a queue managed by a secondary thread then
        // wake that thread up.  The queue manager thread then updates
        // the user interface progress bar.  This lets the read loop
        // continue as fast as possible.
        public void rbcProgressCallback( RBCWrapper rbc, double progress );
    }

    public static final class Downloader implements RBCWrapperDelegate {
    	private ProgressNotifierDelegate delegate;

    	public static interface ProgressNotifierDelegate{
    		// is passed in progress number 0-100
    		public void progressChanged(int progress);
    	}
    	
        public Downloader( String localPath, String remoteURL, int expectedLength, ProgressNotifierDelegate delegate) {
            FileOutputStream        fos;
            RBCWrapper     			rbc;
            URL                     url;

            try {
            	this.delegate = delegate;
                url = new URL( remoteURL );
                long time = System.nanoTime();
                rbcProgressCallback(null, Book.DOWNLOAD_HANG); // hang first
                rbc = new RBCWrapper( Channels.newChannel( url.openStream() ), expectedLength*1024, this ); // takes a while
                try{
                	Thread.sleep(1000*1000);
                }catch(Exception e){
                	System.err.println("interrupt");
                }
                rbcProgressCallback(rbc, 0); // better now
                System.out.println("Time to open stream: " + (System.nanoTime() - time)/Math.pow(10, 9) + " secs");
                fos = new FileOutputStream( localPath );
                System.out.println("URL: " + url.toString() + " RBC: " + rbc.toString() + " FOS: + " + fos.toString());
                
                fos.getChannel().transferFrom( rbc, 0, Long.MAX_VALUE );
                rbcProgressCallback(rbc, 100.0); // probably about 100% now, update
                fos.close();
            } catch ( Exception e ) {
                System.err.println( "Uh oh: " + e.getMessage() );
            }
        }

        public void rbcProgressCallback( RBCWrapper rbc, double progress ) {
            //System.out.println( String.format( "download progress %d bytes received, %.02f%%", rbc.getReadSoFar(), progress ) );
        	delegate.progressChanged((int)progress);
        }

        private int contentLength( URL url ) {
            HttpURLConnection           connection;
            int                         contentLength = -1;

            try {
                HttpURLConnection.setFollowRedirects( false );

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod( "HEAD" );

                contentLength = connection.getContentLength();
            } catch ( Exception e ) { }

            return contentLength;
        }
    }

    public static final class RBCWrapper implements ReadableByteChannel {
        private RBCWrapperDelegate              delegate;
        private long                            expectedSize;
        private ReadableByteChannel             rbc;
        private long                            readSoFar;

        RBCWrapper( ReadableByteChannel rbc, long expectedSize, RBCWrapperDelegate delegate ) {
            this.delegate = delegate;
            this.expectedSize = expectedSize;
            this.rbc = rbc;
        }

        public void close() throws IOException { rbc.close(); }
        public long getReadSoFar() { return readSoFar; }
        public boolean isOpen() { return rbc.isOpen(); }

        public int read( ByteBuffer bb ) throws IOException {
            int                     n;
            double                  progress;

            if ( ( n = rbc.read( bb ) ) >= 0 ) {
                readSoFar += n;
                progress = expectedSize > 0 ? (double) readSoFar / (double) expectedSize * 100.0 : -1.0;
                delegate.rbcProgressCallback( this, progress );
            }

            return n;
        }

		@Override
		public String toString() {
			return "RBCWrapper [delegate=" + delegate + ", expectedSize=" + expectedSize + ", rbc=" + rbc + ", readSoFar=" + readSoFar + "]";
		}
    }
}