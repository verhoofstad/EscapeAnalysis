package org.dataSets;

import java.util.HashSet;
import java.util.Set;

import org.asm.JarFile;
import org.asm.JarFileSet;

public class Library {

	private int id;
	private String organisation;
	private String name;
	private String revision;
	private JarFile cpFile;
	private JarFileSet libFiles;
	
	public Library(int id, String organisation, String name, String revision, JarFile cpFile, JarFileSet libFiles) {
		this.id = id;
		this.organisation = organisation;
		this.name = name;
		this.revision = revision;
		this.cpFile = cpFile;
		this.libFiles = libFiles;
	}
	
	public int id() {
		return this.id;
	}
	
	public String organisation() {
		return this.organisation;
	}
	
	public String name() {
		return this.name;
	}
	
	public String revision() {
		return this.revision;
	}
	
	public JarFile cpFile() {
		return this.cpFile;
	}
	
	public JarFileSet libFiles() {
		return this.libFiles;
	}
	
	public JarFileSet jarFiles() {
		
		Set<JarFile> allFiles = new HashSet<JarFile>();
		allFiles.add(this.cpFile);
		for(JarFile libFile : this.libFiles) {
			allFiles.add(libFile);
		}
		return new JarFileSet(allFiles);
	}
}
