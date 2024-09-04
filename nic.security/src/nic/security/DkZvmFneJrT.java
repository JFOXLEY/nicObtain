package nic.security;

import java.io.File;

public class DkZvmFneJrT {
	public static boolean DEBUG = true;
	
	public static String relativeF(File r, File f) {
		return f.getAbsolutePath().replace(r.getAbsolutePath() + File.pathSeparator, "");
	}
	
	public static String scanSources(File relative, File folder, String r) {
		for (File f : folder.listFiles()) { 
			if (f.isDirectory()) {
				r = scanSources(relative, f, r);
			} else if (f.isFile() && !f.getName().contains("-")) {
				r += "\"" + relativeF(relative, f) + "\" ";
			}
		}
		
		return r;
	}
	
	public static String logic2(String[] args) {
		String jdkHome = System.getenv("JDK_HOME");
		if (jdkHome == null) {
			jdkHome = System.getenv("JAVA_HOME");
		}
		
		if (jdkHome == null) {
			throw new SecurityException();
		}
		
		File jdkBin = new File(jdkHome, "bin");
		return new File(jdkBin, args[1]).getAbsolutePath();
	}
	
	public static String logic1(String[] args) {
		String env = System.getenv(args[1]);
		String prlg = "javac" + (args[2].equals("WIN") ? ".exe" : "");
		for (String part : env.split(args[3])) {
			File t = new File(part, prlg);
			if (t.exists()) {
				return t.getAbsolutePath();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		args = new String[] {"nic.ripe", "PATH", "WIN", ";"};
		File module = new File("..", args[0]);
		if (DEBUG) {
			module = new File(module, "src");
		}
		
		String r = System.getProperty("LOGIC");
		if (r == null) {
			r = "1";
		}
		String javac = null;
		if (r.equals("0")) {
			javac = args[0];
		}
		else if (r.equals("1")) {
			javac = logic1(args);
		}
		else if(r.equals("2")) {
			javac = logic2(args);
		}
		
		if (javac == null) {
			System.err.println("Unable to locate javac. Closing application.");
			System.exit(0);
		}
		
		System.out.println("MODULE: " + module.getAbsolutePath());
		System.out.println("JAVAC: " + javac);
		
		String directory = String.format("modbin-%s", args[0]);
		File output = new File(directory);
		output.mkdir();
		String sources = scanSources(module, module, "");
		String command = String.format("\"%s\" -d %s -cp ..\\org.json.simple.jar;..\\ftp.jar;..\nic.jar %s", javac, directory, sources);
		System.out.println(command);
	}
}
