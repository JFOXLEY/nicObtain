package nic.api;

public interface IDownloader {
	public String name();
	public void attempt(IState state);
	int major();
	int minor();
	short release();
}
