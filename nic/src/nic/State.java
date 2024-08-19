package nic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import nic.api.IState;
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
				this.status = new Status(new Date(0L));
				write(new Date(0), 0L);
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
				return new nic.api.Status(new Date(0));
			}
			
			Object last = this.cache.get("last");
			if (last == null || (last instanceof Long && last == (Long)0L) || (last instanceof String && last.equals("")) || last instanceof Boolean) {
				return new nic.api.Status(new Date(0));
			}
			JSONObject entry = (JSONObject) this.cache.getObject(last);
			Date retrieval = new Date((Long) entry.get("retrieval"));
			return new nic.api.Status(retrieval);
		} catch (IOException ioE) {
			throw new TrackCorruptException(ioE);
		}
	}
	
	public boolean check(Date modified) {
		return this.status != null ? this.status.check(modified) : true;
	}
	
	public File write(Date modified, long size) {
		String uuid = null;
		
		if (status == null) {
			this.cache = new JSONObject();
			this.cache.put("last", new JSONArray());
		} else {
			if ((uuid = this.status.save(modified, size)) != null) {
				JSONObject entry = new JSONObject();
				entry.put("modified", this.status.modified().getTime());
				entry.put("retrieval", this.status.retrieval().getTime());
				entry.put("size", size);
				if (this.cache == null) {
					this.cache = new JSONObject();
				}
				this.cache.put("last", uuid);
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
		
		return new File("data", uuid);
	}
}
