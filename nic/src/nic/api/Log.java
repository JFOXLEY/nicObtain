package nic.api;

import java.util.Date;

public class Log {
	private Date created;
	private String path;
	private String file;
	private String modified;
	private long size;
	
	public Log(Date created, String path, String file, String modified, long size) {
		this.created = created;
		this.path = path;
		this.file = file;
		this.modified = modified;
		this.size = size;
	}
	
	public Date created() {
		return this.created;
	}
	
	public String path() {
		return this.path;
	}
	
	public String file() {
		return this.file;
	}
	
	public String modified() {
		return this.modified;
	}
	
	public long size() {
		return this.size;
	}
}
