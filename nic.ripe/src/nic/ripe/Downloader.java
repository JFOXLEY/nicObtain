package nic.ripe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public static Map<String, List<String>> PATHS; // see static constructor, map of Path->File
	
	static {
		PATHS = new HashMap<String, List<String>>();
		
		List<String> DBASE = new ArrayList<String>();
		DBASE.add("ripe.db.gz");
		PATHS.put("/ripe/dbase/", DBASE);
		
		List<String> SPLIT_DBASE = new ArrayList<String>();
		SPLIT_DBASE.add("ripe.db.inetnum.gz");
		SPLIT_DBASE.add("ripe.db.inet6num.gz");
		PATHS.put("/ripe/dbase/split/", SPLIT_DBASE);
		
		List<String> STATS = new ArrayList<String>();
		STATS.add("delegated-ripencc-extended-latest");
		PATHS.put("/ripe/stats/", STATS);
	}
	
	public FTPClient ftpClient;
	
	public Downloader() {
	    this.ftpClient = new FTPClient();
	}

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
		return 2;
	}
	
	@Override
	public short release() {
		return 2;
	}
	
	public void download(IState state, String path, String file) {
		String target = path.concat(file);
		
		try {
			System.out.printf("Attempting to retrieve target stat: MLIST %s", target);
			System.out.println();
			
			FTPFile stat = this.ftpClient.mlistFile(target);
			this.ftpClient.enterLocalPassiveMode();
			this.ftpClient.type(FTP.BINARY_FILE_TYPE);
			long time = System.currentTimeMillis();
			Date retrieval = new Date(time);
			Calendar timestamp = stat.getTimestamp();
			String modified = timestamp.toString();
			String name = stat.getName();

			if (state.check(modified, path, file)) {
				long size = stat.getSize();
				System.out.printf("[%s] Downloading %s - %s - %d%n", retrieval.toString(), name, modified.toString(), size);

				File destination = new File(state.write(new nic.api.Log(retrieval, path, file, modified, size)), file);
				destination.createNewFile();
				
				FileOutputStream fos = new FileOutputStream(destination);
				CountingOutputStream cos = new CountingOutputStream(fos) {
    			    	private long cosRetrieval = 0L;
    			        protected void beforeWrite(int n) {
    			            super.beforeWrite(n);
    			            if (cosRetrieval == 0) {
    			            	cosRetrieval = time;
    			            }
    			            long current = System.currentTimeMillis();
    			            long elapsed = current - cosRetrieval;
    			            if (elapsed > 1000) {
    			            	cosRetrieval = current;
    			            	long count = getCount();
    			            	float fraction = (float)count / (float)size;
    			            	int total = (int) (Math.floor(fraction * 100.0));
    				            System.out.println("[" + new Date(current).toString() + "] Downloaded " + getCount() + "/" + size + " (" + total + "%)");
    			            }
    			        }
    			    };

				System.out.println("RETFILE");
				boolean in = ftpClient.retrieveFile(target, cos);

				System.out.println("BP : " + (in ? "YES" : "NO"));
				cos.close();

				if (in) {
					System.out.println();
					System.out.printf("File retrieved: ", destination.getAbsolutePath());
					System.out.print(file.toString());
				} else {
					System.out.println("Retrieval failed.");
				}
			} else {
				System.out.printf("[%s] No change to %s", retrieval.toString(), name);
			}
		} catch (IOException e) {
			System.err.printf("Error retrieving file: %s", target);
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void attempt(IState state) {
		System.out.println("RIPE.NET WHOIS DATABASE");
	    
	    this.ftpClient.addProtocolCommandListener(new ProtocolCommandListener() {
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
	    	this.ftpClient.connect(SERVER, PORT);
			System.out.println("Connected to RIPE");
		} catch (SocketException e) {
			System.err.println("Failed to establish connection");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.err.println("Login failed");
			e.printStackTrace();
			return;
		}
	    
	    try {
	    	this.ftpClient.login(USERNAME, PASSWORD);
			System.out.println("FTP logged in without error");
		} catch (IOException e) {
			System.err.println("Login failed");
			e.printStackTrace();
			return;
		}
	    
	    for (String path : PATHS.keySet()) {
	    	for (String file : PATHS.get(path)) {
		    	download(state, path, file);
	    	}
	    }
	}
}
