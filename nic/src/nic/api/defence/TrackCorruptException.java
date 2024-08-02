package nic.api.defence;

import java.io.IOException;

public class TrackCorruptException extends TrackException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 724084362346745217L;
	private IOException ioE;
	public TrackCorruptException(IOException ioE) {
		this.ioE = ioE;
	}
	public IOException getWrappedException() {
		return this.ioE;
	}
}
