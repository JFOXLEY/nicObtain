package nic.api;

import java.io.File;
import java.util.Date;

public interface IState {
	public File write(Log log);
	public boolean check(String modified, String path, String file);
}
