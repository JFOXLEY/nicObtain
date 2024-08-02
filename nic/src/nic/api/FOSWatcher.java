package nic.api;

import java.io.FileOutputStream;
import java.io.IOException;

public class FOSWatcher extends Thread {
	private FileOutputStream fos;
	private long max;
	
	public FOSWatcher(FileOutputStream fos, long max) {
		this.fos = fos;
		this.max = max;
		System.out.printf("FOSWatcher initialised, waiting for %d" , max);
		this.start();
	}
	
	@Override
	public void run() {
		long current;
		try {
			current = fos.getChannel().position();
			
			while (current != max) {
				System.out.println(current);
				Thread.sleep(1000);
				current = fos.getChannel().position();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
