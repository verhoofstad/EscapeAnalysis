package org.classHierarchy.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.asm.JarFile;
import org.asm.JarFileSet;

public class JavaMethodList implements Iterable<JavaMethod> {

	private ArrayList<JavaMethod> methods = new ArrayList<JavaMethod>();

	public JavaMethodList() {}
	
	public JavaMethodList(List<JavaMethod> items) {
		for(JavaMethod javaClass : items) {
			add(javaClass);
		}		
	}

	public JavaMethodList(JavaMethodList items) {
		for(JavaMethod javaClass : items) {
			add(javaClass);
		}
	}
	
	public void add(JavaMethod item) {
		
		if(!contains(item)) {
			this.methods.add(item);
		}
	}

	public boolean contains(JavaMethod method) {
		for(JavaMethod javaMethod : this.methods) {
			if(javaMethod.equals(method)) {
				return true;
			}
		}
		return false;
	}
	
	public JavaClassList getClasses() {
		JavaClassList classes = new JavaClassList();
		
		for(JavaMethod method : this.methods) {
			if(method.containedIn() instanceof JavaClass) {
				classes.add((JavaClass)method.containedIn());
			}
		}
		return classes;
	}
	
	public JavaMethodList getMethodsOfClass(JavaClass javaClass) {
		JavaMethodList methods = new JavaMethodList();
		
		for(JavaMethod method : this.methods) {
			if(method.containedIn().equals(javaClass)) {
				methods.add(method);
			}
		}
		return methods;
	}
	
	public JarFileSet jarFiles() {
		
		Set<JarFile> jarFiles = new HashSet<JarFile>();
		
		for(JavaMethod method : this.methods) {
			jarFiles.add(method.jarFile());
		}
		return new JarFileSet(jarFiles);
	}
	
	public int size() {
		return this.methods.size();
	}
	
	@Override
	public Iterator<JavaMethod> iterator() {
		return this.methods.iterator();
	}
}
