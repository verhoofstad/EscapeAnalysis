package org.tree;

public class JavaMethod {

	private int access;
	private String name;
	private String desc;
	private String signature;
	private String[] exceptions;
	
	
	public JavaMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
	}
	
	public String desc() {
		return this.desc;
	}
}
