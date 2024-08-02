package nic.api;

import java.util.Date;
import java.util.UUID;

public class Status {
	private Date retrieval;
	private Date modified;
	private long size;
	
	public Status(Date retrieval) {
		this.retrieval = retrieval;
	}
	
	public boolean check(Date modified) {
		return modified != null && retrieval.before(modified);
	}
	
	public String save(Date modified, long size) {
		UUID uuid = UUID.randomUUID();
		this.retrieval = new Date();
		this.modified = modified;
		this.size = size;
		return uuid.toString();
	}
	
	public Date retrieval() {
		return retrieval;
	}
	
	public Date modified() {
		return modified;
	}
}
