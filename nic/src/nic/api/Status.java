package nic.api;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Status {
	//private Date retrieval;
	//private Date modified;
	//private long size;
	private List<Log> logs;
	private Date initialised;
	private UUID uuid;
	
	public Status(Date initialised) {
		this.initialised = initialised;
		this.uuid = UUID.randomUUID();
	}
	
	public boolean check(String path, String modified) {
		for (Log log : logs) {
			if (log.path().equalsIgnoreCase(path)) {
				return modified.equals(log.modified());
			}
		}
		
		return false;
	}
	
	public boolean add(Log log) {
		return logs.add(log);
	}
	
	public UUID uuid() {
		return this.uuid;
	}
}
