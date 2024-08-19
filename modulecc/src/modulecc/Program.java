package modulecc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Program {
	public static String SPECIFIER = "NIC-Class";
	
	public static void main(String[] args) {
		System.out.println("modulecc v1.0");
		try {
			File output = new File(args[0]);
			Manifest manifest = new Manifest();
		    manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
			switch(args[2]) {
			case "module":
		    	manifest.getMainAttributes().put(Program.SPECIFIER, args[3]);
		    	break;
			case "jar":
		    	manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, args[3]);
		    	break;
			default:
				System.out.println("Warning: Non-modular class");
			}
			System.out.println("Output: " + output.getAbsolutePath());
			run(args[0], args[1], manifest);
			System.out.println("");
			System.out.println("Successful output");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void run(String out, String in, Manifest manifest) throws IOException {
	    JarOutputStream target = new JarOutputStream(new FileOutputStream(out), manifest);

        for (File nestedFile : new File(in).listFiles()) {
    	    add(nestedFile, target);
        }
        
	    target.close();
	}

	private static void add(File source, JarOutputStream target) throws IOException {
	    String name = source.getPath().replace("\\", "/");
	    System.out.println((source.isDirectory() ? "Directory" : "File") + ": " + name);
	    if (source.isDirectory()) {
	        if (!name.endsWith("/")) {
	            name += "/";
	        }
	        JarEntry entry = new JarEntry(name);
	        entry.setTime(source.lastModified());
	        target.putNextEntry(entry);
	        target.closeEntry();
	        for (File nestedFile : source.listFiles()) {
	            add(nestedFile, target);
	        }
	    } else {
	        JarEntry entry = new JarEntry(name);
	        entry.setTime(source.lastModified());
	        target.putNextEntry(entry);
	        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source))) {
	            byte[] buffer = new byte[1024];
	            while (true) {
	                int count = in.read(buffer);
	                if (count == -1)
	                    break;
	                target.write(buffer, 0, count);
	            }
	            target.closeEntry();
	        }
	    }
	}
}
