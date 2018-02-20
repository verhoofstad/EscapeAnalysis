package org.classHierarchy.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * Represents a set of Java interfaces.
 */
public class JavaInterfaceSet implements Iterable<JavaInterface> {

	private Map<String, JavaInterface> interfaces = new HashMap<String, JavaInterface>();

	public JavaInterfaceSet() {}

	public JavaInterfaceSet(Iterable<JavaInterface> items) {
		for(JavaInterface javaInterface : items) {
			add(javaInterface);
		}
	}
	
	public void add(JavaInterface item) {
		
		if(!this.interfaces.containsKey(item.id())) {
			this.interfaces.put(item.id(), item);
		}
	}
	
	public void addAll(Iterable<JavaInterface> items) {
		for(JavaInterface item : items) {
			add(item);
		}
	}

	public boolean contains(String id) {
		return this.interfaces.containsKey(id);
	}

	public boolean contains(JavaInterface javaInterface) {
		return this.interfaces.containsKey(javaInterface.id());
	}

	/*
	 * Finds the interface with the given id. Returns null if the interface was not found.
	 */
	public JavaInterface find(String id) {
		if(this.contains(id)) {
			return this.interfaces.get(id);
		}
		return null;
	}
	
	public JavaInterfaceSet find(String[] identifiers) {
		
		JavaInterfaceSet interfaces = new JavaInterfaceSet();
		
		for(String id : identifiers) {
			if(this.contains(id)) {
				interfaces.add(this.interfaces.get(id));
			}
		}
		return interfaces;
	}
	
	/*
	 * Finds the interface with the given id. Throws an error if the interface was not found.
	 */
	public JavaInterface get(String id) {
		if(this.contains(id)) {
			return this.interfaces.get(id);
		}
		throw new Error("Could not find interface " + id + ".");
	}
	
	public int size() {
		return this.interfaces.size();
	}
		
	@Override
	public Iterator<JavaInterface> iterator() {
		return this.interfaces.values().iterator();
	}
}
