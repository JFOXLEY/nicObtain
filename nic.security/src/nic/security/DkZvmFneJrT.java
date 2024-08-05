package nic.security;

import java.io.File;

public class DkZvmFneJrT {
	public static void main(String[] args) {
		String jdkHome = System.getenv("JDK_HOME");
		if (jdkHome == null) {
			jdkHome = System.getenv("JAVA_HOME");
		}
		
		File jdkBin = new File(jdkHome, "bin");
		File javacBin = new File(jdkBin, args[0]);
		
		String module = args[1];
		
		
	}
}
