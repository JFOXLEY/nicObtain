package nic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.generics.MapHolder;

import nic.api.IState;
import nic.api.Log;
import nic.api.Status;
import nic.api.defence.TrackCorruptException;
import nic.api.defence.TrackNotFoundException;

public class State implements IState {
	public static boolean trackRoot = true;
	
	private File state;
	private Status status;
	private JSONObject cache;
	
	public State(String encoding, String server) throws TrackCorruptException {
		File track = new File("track");
		
		try {
			if (!track.exists() || !track.isDirectory()) {
				throw new TrackNotFoundException(true);
			}
			this.state = new File(track, server);
			this.status = read(encoding);
		} catch (TrackNotFoundException trackNotFoundException) {
			if (trackNotFoundException.root) {
				track.mkdir();
				this.state = new File(track, server);
			}
			
			try {
				this.state.createNewFile();
				this.status = new Status(new Date());
				write(null);
				this.status = read(encoding);
			} catch (IOException ioE) {
				ioE.printStackTrace();
				throw new TrackCorruptException(ioE);
			} catch (TrackNotFoundException e) {
				e.printStackTrace();
			}
		} catch (TrackCorruptException trackCorruptException) {
			System.err.println("Track corrupt! %s %s".formatted(encoding, server));
			trackCorruptException.getWrappedException().printStackTrace();
		}
	}
	
	private nic.api.Status read(String encoding) throws TrackCorruptException, TrackNotFoundException {
		if (!this.state.exists()) {
			this.cache = new JSONObject();
			throw new TrackNotFoundException(false);
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.state), encoding));
			this.cache = JSONValue.parseObject(reader);
			reader.close();
			
			if (this.cache == null) {
				return new nic.api.Status(new Date());
			}
			
			Object last = this.cache.get("latest");
			if (last == null || (last instanceof Long && last == (Long)0L) || (last instanceof String && last.equals("")) || last instanceof Boolean) {
				return new nic.api.Status(new Date());
			}
			JSONObject entry = this.cache.getObject(last);
			nic.api.Status status = new nic.api.Status(new Date());
			JSONArray files = entry.getArray("files");
			Iterator<Object> fileIterator = files.iterator();
			while (fileIterator.hasNext()) {
				Object file = fileIterator.next();
				JSONObject log;
				if (file instanceof MapHolder) {
					log = (JSONObject) ((MapHolder)file).map();
				} else {
					log = (JSONObject) file;
				}
				
				Date created = new Date((Long) log.get("created"));
				String path = (String) log.get("path");
				String filename = (String) log.get("filename");
				String modified = (String) log.get("modified");
				Long size = (Long) log.get("size");
				status.add(new Log(created, path, filename, modified, size));
			}
			return status;
		} catch (IOException ioE) {
			throw new TrackCorruptException(ioE);
		}
	}
	
	public boolean check(String path, String modified) {
		return this.status != null ? this.status.check(path, modified) : true;
	}
	
	public File write(Log log) {
		String uuid = this.status.uuid().toString();
		
		if (log == null) {
			this.cache = new JSONObject();
			this.cache.put("latest", null);
		} else {
			if (this.status.add(log)) {
				JSONObject entry = new JSONObject();
				entry.put("modified", log.modified());
				entry.put("path", log.path());
				entry.put("filename", log.filename());
				entry.put("created", log.created());
				JSONArray files;
				if (this.cache == null) {
					this.cache = new JSONObject();
					files = new JSONArray();
					this.cache.put("files", files);
				} else {
					files = this.cache.getArray("files");
				}
				
				files.add(entry);
				this.cache.put("latest", uuid);
				this.cache.put(uuid, entry);
			}
		}
		
		try {
			PrintWriter out = new PrintWriter(this.state);
			out.write(this.cache.toJSONString());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		File dataPath = new File("data", uuid);
		if (!dataPath.exists()) {
			dataPath.mkdir();
		}
		return dataPath;
	}
}
