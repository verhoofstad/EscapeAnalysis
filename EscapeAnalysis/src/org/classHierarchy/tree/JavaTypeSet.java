package org.classHierarchy.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JavaTypeSet implements Iterable<JavaType> {
	private Map<String, JavaType> types = new HashMap<String, JavaType>();

	public JavaTypeSet() {}

	public JavaTypeSet(JavaType item) {
		add(item);
	}
	
	public JavaTypeSet(Iterable<JavaType> items) {
		for(JavaType item : items) {
			add(item);
		}
	}

	public void add(JavaType item) {
		if(!this.types.containsKey(item.id())) {
			this.types.put(item.id(), item);
		} else {
			System.out.println("Type already added: " + item.id());
		}
	}
		
	public void addAll(Iterable<JavaType> items) {
		for(JavaType item : items) {
			add(item);
		}
	}

	public boolean contains(String id) {
		return this.types.containsKey(id);
	}
	
	public boolean contains(JavaType item) {
		return this.types.containsKey(item.id());
	}
	
	public void remove(String id) {
		this.types.remove(id);
	}

	/*
	 * Finds the Java type with the given id. Returns null if the class was not found.
	 */
	public JavaType find(String id) {
		if(this.contains(id)) {
			return this.types.get(id);
		}
		return null;
	}
	
	/*
	 * Finds the Java type with the given id. Throws an error if the class was not found.
	 */
	public JavaType get(String id) {
		if(this.contains(id)) {
			return this.types.get(id);
		}
		throw new Error("Could not find type " + id + ".");
	}
	
	public void difference(JavaTypeSet typeSet) {

		for(JavaType javaType : typeSet) {
			if(this.contains(javaType)) {
				this.remove(javaType.id());
			}
		}
	}
	
	public int size() {
		return this.types.size();
	}
	
	@Override
	public Iterator<JavaType> iterator() {
		return this.types.values().iterator();
	}
}
