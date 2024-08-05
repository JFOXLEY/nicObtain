package nic.api;

import java.io.File;
import java.util.Date;

public interface IState {
	public File write(Date modified, long size);
	public boolean check(Date modified);
}
