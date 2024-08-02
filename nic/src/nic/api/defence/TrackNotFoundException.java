package nic.api.defence;

public class TrackNotFoundException extends TrackException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4492225172142341521L;
	public boolean root;
	public TrackNotFoundException(boolean root) {
		this.root = root;
	}
}
