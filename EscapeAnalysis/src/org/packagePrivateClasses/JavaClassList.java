package org.packagePrivateClasses;

import java.util.ArrayList;

/**
 * Represents a list of classes. 
 */
public class JavaClassList {

	private ArrayList<JavaClass> _classes = new ArrayList<JavaClass>();
	
	
	public ArrayList<JavaClass> getArrayList() {
		return _classes;
	}
	
	public void add(JavaClass item) {
		_classes.add(item);
	}
	
	public void addAll(ArrayList<JavaClass> items) {
		_classes.addAll(items);
	}
	
	public void addAll(JavaClassList items) {
		_classes.addAll(items.getArrayList());
	}

	public Boolean contains(String name) {
		for(JavaClass javaClass : _classes) {
			
			if(javaClass.name().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public JavaClass find(String name) {
		for(JavaClass javaClass : _classes) {
			
			if(javaClass.name().equals(name)) {
				return javaClass;
			}
		}
		return null;
	}
	
	public int size() {
		return _classes.size();
	}
	
	public int methodCount() {
		int count = 0;
		for(JavaClass javaClass : _classes) {
			count += javaClass.methodCount();
		}
		return count;
	}
}
