package org.packagePrivateClasses;

public class JavaMethod {

	private int _access;
	private String _name;
	private String _desc;
	private String _signature;
	private String[] _exceptions;
	
	
	public JavaMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		_access = access;
		_name = name;
		_desc = desc;
		_signature = signature;
		_exceptions = exceptions;
	}
}
