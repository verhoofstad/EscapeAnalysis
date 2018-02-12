package org.classHierarchy.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Represents a list of Java interfaces.
 */
public class JavaInterfaceList implements Iterable<JavaInterface> {

	private List<JavaInterface> interfaces = new ArrayList<JavaInterface>();

	public void add(JavaInterface item) {
		
		if(!contains(item.name())) {
			this.interfaces.add(item);
		}
	}
	
	public void addAll(List<JavaInterface> items) {
		this.interfaces.addAll(items);
	}
	
	public void addAll(JavaInterfaceList items) {
		for(JavaInterface item : items) {
			add(item);
		}
	}

	public boolean contains(String name) {
		for(JavaInterface item : this.interfaces) {
			if(item.name().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public JavaInterface find(String name) {
		
		for(JavaInterface javaInterface : this.interfaces) {
			if(javaInterface.name().equals(name)) {
				return javaInterface;
			}
		}
		return null;
	}
	
	public JavaInterfaceList find(String[] names) {
		
		JavaInterfaceList interfaces = new JavaInterfaceList();
		
		for(JavaInterface javaInterface : this.interfaces) {
			for(String name : names) {
				if(javaInterface.name().equals(name)) {
					interfaces.add(javaInterface);
				}
			}
		}
		return interfaces;
	}
	
	public int size() {
		return this.interfaces.size();
	}
		
	@Override
	public Iterator<JavaInterface> iterator() {
		return this.interfaces.iterator();
	}
}
