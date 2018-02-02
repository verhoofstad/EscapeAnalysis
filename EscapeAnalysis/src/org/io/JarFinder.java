package org.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class JarFinder 
	implements Iterable<File> {
	
	private ArrayList<File> jarFiles = new ArrayList<File>();

	public JarFinder(final File location) {
		
		findJarFiles(location);
	}
	

	public JarFinder(String location) {
		findJarFiles(new File(location));
	}


	private void findJarFiles(final File fileOrFolder) {
		
		if(fileOrFolder.isDirectory()) {
			for (final File fileEntry : fileOrFolder.listFiles()) {
				findJarFiles(fileEntry);
			}
		} else {
        	if(getFileExtension(fileOrFolder).equalsIgnoreCase("jar")) {
	            jarFiles.add(fileOrFolder);
        	}
		}
	}
	
	@Override
	public Iterator<File> iterator() {
		// TODO Auto-generated method stub
		return jarFiles.iterator();
	}
	
	private static String getFileExtension(final File file) {
	    String name = file.getName();
	    try {
	        return name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
}
