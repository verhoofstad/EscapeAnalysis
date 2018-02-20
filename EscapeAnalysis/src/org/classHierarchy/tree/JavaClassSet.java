package org.classHierarchy.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a set of classes. 
 */
public class JavaClassSet implements Iterable<JavaClass> {
	
	private Map<String, JavaClass> classes = new HashMap<String, JavaClass>();

	public JavaClassSet() {}

	public JavaClassSet(Iterable<JavaClass> items) {
		for(JavaClass javaClass : items) {
			add(javaClass);
		}
	}

	public void add(JavaClass item) {
		if(!this.classes.containsKey(item.id())) {
			this.classes.put(item.id(), item);
		} else {
			System.out.println("Already added: " + item.id());
		}
	}
		
	public void addAll(Iterable<JavaClass> items) {
		for(JavaClass javaClass : items) {
			add(javaClass);
		}
	}

	public boolean contains(String id) {
		return this.classes.containsKey(id);
	}
	
	public boolean contains(JavaClass javaClass) {
		return this.classes.containsKey(javaClass.id());
	}

	/*
	 * Finds the class with the given id. Returns null if the class was not found.
	 */
	public JavaClass find(String id) {
		if(this.contains(id)) {
			return this.classes.get(id);
		}
		return null;
	}
	
	/*
	 * Finds the class with the given id. Throws an error if the class was not found.
	 */
	public JavaClass get(String id) {
		if(this.contains(id)) {
			return this.classes.get(id);
		}
		throw new Error("Could not find class " + id + ".");
	}
	
	public int size() {
		return this.classes.size();
	}
	
	@Override
	public Iterator<JavaClass> iterator() {
		return this.classes.values().iterator();
	}
}
