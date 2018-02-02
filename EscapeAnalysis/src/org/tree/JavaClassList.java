package org.tree;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a list of classes. 
 */
public class JavaClassList implements Iterable<JavaClass> {
	

	private ArrayList<JavaClass> _classes = new ArrayList<JavaClass>();
	
	
	public ArrayList<JavaClass> getArrayList() {
		return _classes;
	}
	
	public void add(JavaClass item) {
		
		if(!contains(item.name())) {
			_classes.add(item);
		}
	}
	
	public void addAll(ArrayList<JavaClass> items) {
		_classes.addAll(items);
	}
	
	public void addAll(JavaClassList items) {
		for(JavaClass javaClass : items) {
			add(javaClass);
		}
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
			
			JavaClass result = javaClass.find(name);
			
			if(result != null) {
				return result;
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
	
	
	public JavaClass toTree() {
	
		JavaClassList classesWithoutParent = findClassesWithNoParent();
		
		if(classesWithoutParent.size() == 1) {
			JavaClass rootNode = classesWithoutParent.getArrayList().get(0);
			
			addSubClasses(rootNode);
			return rootNode;
		}
		return null;
	}
	
	private void addSubClasses(JavaClass javaClassNode) {
	
		for(JavaClass javaClass : this._classes) {
			
			if(javaClass.inheritsFrom(javaClassNode)) {

				addSubClasses(javaClass);
				
				javaClassNode.addSubClass(javaClass);
			}
		}
	}
	
	private JavaClassList findClassesWithNoParent() {
		
		JavaClassList classesWithoutParent = new JavaClassList();
		
		for(JavaClass javaClass : this._classes) {
			if(!javaClass.hasSuperClass() ) {
				classesWithoutParent.add(javaClass);
			}
		}
		return classesWithoutParent;
	}
	
	public Boolean containsPublicClass(Boolean transitive) {
		
		for(JavaClass javaClass : this._classes) {
			if(javaClass.isPublic() || javaClass.hasPublicSubClass(true)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Iterator<JavaClass> iterator() {
		return this._classes.iterator();
	}
}
