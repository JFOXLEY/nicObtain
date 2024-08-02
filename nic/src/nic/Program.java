package nic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import nic.api.IDownloader;
import nic.api.defence.TrackCorruptException;

public class Program {
	public static int MAJOR = 0;
	public static int MINOR = 1;
	public static short RELEASE = 6;
	
	public static void main(String[] args) {
		System.out.println("nicObtain v%d.%d_%d".formatted(MAJOR, MINOR, RELEASE));
		
		if (args.length == 0) {
			System.err.println("No JAR given. Exiting.");
			System.exit(0);
		}
		
		try {
			run(args[0]);
		} catch (TrackCorruptException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void run(String server) throws TrackCorruptException, IOException {
		nic.api.IDownloader downloader = null;
		File jar = new File(server);
		State state = new State("UTF-8", server);
		
		URLClassLoader child;
		try {
			child = new URLClassLoader(
			        new URL[] { jar.toURI().toURL() },
			        Program.class.getClassLoader()
			);
			Class<? extends IDownloader> classToLoad = (Class<? extends IDownloader>) Class.forName("nic.ripe.Downloader", true, child);
			downloader = (IDownloader) classToLoad.getConstructors()[0].newInstance();
			System.out.println("%s: %d.%d.%s".formatted(downloader.name(), downloader.major(), downloader.minor(), downloader.release()));
			downloader.attempt(state);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
