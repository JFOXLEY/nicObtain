package nic.api;

import java.util.Date;

import nic.State;

public interface IDownloader {
	public String name();
	public void attempt(IState state);
	int major();
	int minor();
	short release();
}
