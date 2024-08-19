package nic.ripe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Date;

import org.apache.commons.io.stream.CountingOutputStream;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import nic.api.IDownloader;
import nic.api.IState;

public class Downloader implements IDownloader {
	public static String SERVER = "ftp.ripe.net";
	public static int PORT = 21;
	public static String USERNAME = "anonymous";
	public static String PASSWORD = "";
	public static String PATH = "/ripe/dbase/split/";
	public static String TARGETV4 = "ripe.db.inetnum.gz";
	public static String TARGETV6 = "ripe.db.inet6num.gz";

	@Override
	public String name() {
		return "RIPE";
	}
	
	@Override
	public int major() {
		return 0;
	}
	
	@Override
	public int minor() {
		return 1;
	}
	
	@Override
	public short release() {
		return 2;
	}

	@Override
	public void attempt(IState state) {
		System.out.println("RIPE.NET WHOIS DATABASE");
	    FTPClient ftpClient = new FTPClient();
	    
	    ftpClient.addProtocolCommandListener(new ProtocolCommandListener() {
	        @Override
	        public void protocolCommandSent(ProtocolCommandEvent protocolCommandEvent) {
	          System.out.printf("[%s][%d] Command sent : [%s]-%s", Thread.currentThread().getName(),
	              System.currentTimeMillis(), protocolCommandEvent.getCommand(),
	              protocolCommandEvent.getMessage());
	        }

	        @Override
	        public void protocolReplyReceived(ProtocolCommandEvent protocolCommandEvent) {
	          System.out.printf("[%s][%d] Reply received : %s", Thread.currentThread().getName(),
	              System.currentTimeMillis(), protocolCommandEvent.getMessage());
	        }
	    });

	    
	    try {
			ftpClient.connect(SERVER, PORT);
			System.out.println("Connected to RIPE");
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    try {
			ftpClient.login(USERNAME, PASSWORD);
			System.out.println("FTP logged in without error");
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    String db = PATH.concat(TARGET);
	    try {
	    	System.out.println("Attempting to retrieve stat: MLIST");
	    	FTPFile stat = ftpClient.mlistFile(db);
	    	ftpClient.enterLocalPassiveMode();
	    	ftpClient.type(FTP.BINARY_FILE_TYPE);
	    	long lRetrieval = System.currentTimeMillis();
	    	Date retrieval = new Date(lRetrieval);
	    	String name = stat.getName();
	    	
	    	if (state.check(retrieval)) {
	    		long size = stat.getSize();
	    		Date modified = new Date(stat.getTimestamp().getTimeInMillis());
			    System.out.printf("[%s] Downloading %s - %s - %d%n", retrieval.toString(), name, modified.toString(), size);
				
			    File destination = state.write(modified, size);
			    FileOutputStream cos = new FileOutputStream(destination);
			    /*CountingOutputStream cos = new CountingOutputStream(out) {
			    	private long cosRetrieval = 0L;
			        protected void beforeWrite(int n) {
			            super.beforeWrite(n);
			            if (cosRetrieval == 0) {
			            	cosRetrieval = lRetrieval;
			            }
			            long current = System.currentTimeMillis();
			            long elapsed = current - cosRetrieval;
			            if (elapsed > 1000) {
			            	cosRetrieval = current;
			            	long count = getCount();
			            	float percent = (float)count / (float)size;
			            	double percentage = (double) (Math.round(percent * 100.0));
				            System.out.println("[" + new Date(current).toString() + "] Downloaded " + getCount() + "/" + size + " (" + percentage + "%)");
			            }
			        }
			    };*/
			    
		        System.out.println("RETFILE");
		        
			    boolean in = ftpClient.retrieveFile(db, cos);
			   
			    System.out.println("BP : " + (in ? "YES" : "NO"));
			    
			    //out.flush();
			    cos.close();
			    
			    if (in) {
			    	File file = state.write(modified, size);
			    	System.out.println();
			    	System.out.printf("File retrieved: ", file.getAbsolutePath());
			    	System.out.print(file.toString());
			    	System.exit(0);
			    } else {
			    	System.out.println("Retrieval failed.");
			    }
	    	} else {
			    System.out.printf("[%s] No change to %s", retrieval.toString(), name);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
