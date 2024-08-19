package nic.api;

import java.io.File;
import java.util.Date;

public interface IState {
	public File write(String file, Date modified, long size);
	public boolean check(String file, Date modified);
}
