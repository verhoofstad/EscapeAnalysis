package org.classHierarchy.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a list of classes. 
 */
public class JavaClassList implements Iterable<JavaClass> {
	
	private ArrayList<JavaClass> classes = new ArrayList<JavaClass>();

	public JavaClassList(List<JavaClass> items) {
		for(JavaClass javaClass : items) {
			add(javaClass);
		}
	}

	public JavaClassList() {}

	public JavaClassList(JavaClassList items) {
		for(JavaClass javaClass : items) {
			add(javaClass);
		}
	}
	
	public void add(JavaClass item) {
		
		if(!contains(item.name())) {
			this.classes.add(item);
		}
	}
	
	public void addAll(List<JavaClass> items) {
		this.classes.addAll(items);
	}
	
	public void addAll(JavaClassList items) {
		for(JavaClass javaClass : items) {
			add(javaClass);
		}
	}

	public boolean contains(String name) {
		for(JavaClass javaClass : this.classes) {
			if(javaClass.name().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public JavaClass find(String name) {
		for(JavaClass javaClass : this.classes) {
			if(javaClass.name().equals(name)) {
				return javaClass;
			}
		}
		return null;
	}
	
	public int size() {
		return this.classes.size();
	}
	
	@Override
	public Iterator<JavaClass> iterator() {
		return this.classes.iterator();
	}
}
