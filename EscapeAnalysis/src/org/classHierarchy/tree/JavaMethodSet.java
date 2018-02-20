package org.classHierarchy.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.asm.JarFile;
import org.asm.JarFileSet;

/*
 * Represents a set of methods.
 */
public class JavaMethodSet implements Iterable<JavaMethod> {

	private Map<String, JavaMethod> methods = new HashMap<String, JavaMethod>();

	public JavaMethodSet() {}
	
	public JavaMethodSet(Iterable<JavaMethod> items) {
		for(JavaMethod item : items) {
			add(item);
		}		
	}

	public void add(JavaMethod item) {
		if(!this.methods.containsKey(item.id())) {
			this.methods.put(item.id(), item);
		} else {
			System.out.println("Already added: " + item.id());
		}
	}
	
	public void addAll(Iterable<JavaMethod> items) {
		for(JavaMethod item : items) {
			add(item);
		}
	}
	
	public boolean contains(String id) {
		return this.methods.containsKey(id);
	}
	
	public boolean contains(JavaMethod method) {
		return this.methods.containsKey(method.id());
	}
	
	public JavaMethod getRandom() {
		return this.methods.values().iterator().next();
	}
	
	public JavaClassSet getClasses() {
		JavaClassSet classes = new JavaClassSet();
		
		for(JavaMethod method : this.methods.values()) {
			if(method.containedIn() instanceof JavaClass) {
				classes.add((JavaClass)method.containedIn());
			}
		}
		return classes;
	}
	
	public JavaMethodSet getMethodsOfClass(JavaClass javaClass) {
		JavaMethodSet methods = new JavaMethodSet();
		
		for(JavaMethod method : this.methods.values()) {
			if(method.containedIn().equals(javaClass)) {
				methods.add(method);
			}
		}
		return methods;
	}
	
	public void remove(String id) {
		this.methods.remove(id);
	}
	
	public JarFileSet jarFiles() {
		
		Set<JarFile> jarFiles = new HashSet<JarFile>();
		
		for(JavaMethod method : this.methods.values()) {
			jarFiles.add(method.jarFile());
		}
		return new JarFileSet(jarFiles);
	}
	
	public boolean isEmpty() {
		return this.methods.isEmpty();
	}
	
	public int size() {
		return this.methods.size();
	}
	
	@Override
	public Iterator<JavaMethod> iterator() {
		return this.methods.values().iterator();
	}
}