package nic.ripe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Date;

import org.apache.commons.io.stream.CountingOutputStream;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;

import nic.api.IDownloader;
import nic.api.IState;

public class Downloader implements IDownloader {
	public static String SERVER = "ftp.ripe.org";
	public static String USERNAME = "anonymous";
	public static String PASSWORD = "anonymous";
	public static String PATH = "/ripe/dbase/";
	public static String TARGET = "ripe.db.gz";

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
		return 1;
	}

	@Override
	public void attempt(IState state) {
		System.out.println("RIPE.NET WHOIS DATABASE");
	    FTPSClient ftpClient = new FTPSClient();
	    
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
			ftpClient.connect("ftp.ripe.net", 21);
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
	    	long lRetrieval = System.currentTimeMillis();
	    	Date retrieval = new Date(lRetrieval);
	    	String name = stat.getName();
	    	
	    	if (state.check(retrieval)) {
	    		long size = stat.getSize();
	    		Date modified = new Date(stat.getTimestamp().getTimeInMillis());
			    System.out.printf("[%s] Downloading %s - %s - %d%n", retrieval.toString(), name, modified.toString(), size);
				
			    File destination = state.write(modified, size);
			    FileOutputStream out = new FileOutputStream(destination);
			    CountingOutputStream cos = new CountingOutputStream(out) {
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
			            	double percentage = (double) (Math.round(percent * 100.0) / 100.0);
				            System.out.println("[" + new Date(current).toString() + "] Downloaded " + getCount() + "/" + size + " (" + percentage + "%)");
			            }
			        }
			    };
			    
		        System.out.println("RETFILE");
		        
			    boolean in = ftpClient.retrieveFile(db, cos);
			   
			    System.out.println("BP : " + (in ? "YES" : "NO"));
			    
			    out.flush();
			    out.close();
			    
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
