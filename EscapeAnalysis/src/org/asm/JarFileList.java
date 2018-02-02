package org.asm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class JarFileList {

	private ArrayList<JarFile> jarFiles = new ArrayList<JarFile>();
	
	public JarFileList(File location) {
		
		findJarFiles(location);
	}
	
	public JarFileList(String location) {
		findJarFiles(new File(location));
	}

	public int size() {
		return this.jarFiles.size();
	}
	
	public void accept(JarFileListVisitor visitor) {
		
		for(JarFile jarFile : this.jarFiles) {

			visitor.visitJarFile(jarFile);
			
			try {
				jarFile.accept(visitor);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		visitor.visitEnd();
	}

	private void findJarFiles(final File fileOrFolder) {
		
		if(fileOrFolder.isDirectory()) {
			for (final File fileEntry : fileOrFolder.listFiles()) {
				findJarFiles(fileEntry);
			}
		} else {
        	if(getFileExtension(fileOrFolder).equalsIgnoreCase("jar")) {
	            try {
					this.jarFiles.add(new JarFile(fileOrFolder));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
        	}
		}
	}
	
	private static String getFileExtension(final File file) {
	    String name = file.getName();
	    try {
	        return name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
	
	public String getClassPath() {
		
		StringBuilder classPath = new StringBuilder();
		
		for(JarFile jarFile : this.jarFiles) {
			classPath.append(jarFile.getAbsolutePath() + ";");
		}
		return classPath.toString();
	}
}
