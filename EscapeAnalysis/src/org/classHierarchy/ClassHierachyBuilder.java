package org.classHierarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.asm.JarClass;
import org.asm.JarFile;
import org.asm.JarFileSetVisitor;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaInterface;
import org.classHierarchy.tree.JavaInterfaceSet;
import org.classHierarchy.tree.JavaMethod;

/*
 * Builds a class hierarchy.
 */
public class ClassHierachyBuilder extends JarFileSetVisitor  {
	
	private boolean includeInterfaces = true;
	private boolean includeMethods = true;
	private boolean resolveOverrides = true;
	private boolean verbose = false;
	
	private JarFile currentJarFile;
	
	private Map<String, JavaTempType> tempClasses = new HashMap<String, JavaTempType>();
	private Map<String, JavaTempType> tempInterfaces = new HashMap<String, JavaTempType>();
		
	private JavaClass rootNode;
	private JavaInterfaceSet interfaces = new JavaInterfaceSet();
	
	public ClassHierarchy classHierarchy() {
		return new ClassHierarchy(this.rootNode, this.interfaces);
	}
	
	@Override
	public void visitJarFile(JarFile jarFile) {
		this.currentJarFile = jarFile;
	}
	
	@Override 
	public void visitPublicClass(JarClass jarClass) {
		processJarClass(jarClass);
	}
	
	@Override 
	public void visitPublicEnum(JarClass jarClass) {
		processJarClass(jarClass);
	}
	
	@Override
	public void visitPackagePrivateClass(JarClass jarClass) {
		processJarClass(jarClass);
	}

	@Override
	public void visitPackagePrivateEnum(JarClass jarClass) {
		processJarClass(jarClass);
	}
	
	@Override
	public void visitPublicInterface(JarClass jarClass) {
		processJarInterface(jarClass);
	}
	
	@Override
	public void visitPackagePrivateInterface(JarClass jarClass) {
		processJarInterface(jarClass);
	}
	
	@Override
	public void visitEnd() {
		
		if(this.includeInterfaces) {
			println("Building interface hierarchy...");
			buildInterfaceHierarchy();
			println("Total interface count: %s\n", this.interfaces.size());

			if(this.tempInterfaces.size() != this.interfaces.size()) {
				throw new Error("Interface count mismatch.");
			}
		}
		
		this.rootNode = buildClassHierarchy();
		
		if(this.resolveOverrides) {
			println("Resolving overrides...");
			this.resolveOverrides(this.rootNode);
		}
		
		this.tempClasses = null;
		this.tempInterfaces = null;
	}
	
	private void processJarClass(JarClass jarClass) {
		JavaTempType tempClass = new JavaTempType(jarClass, this.currentJarFile);
		processMethods(jarClass, tempClass);
		this.tempClasses.put(tempClass.name(), tempClass);
	}
	
	private void processJarInterface(JarClass jarClass) {
		JavaTempType tempInterface = new JavaTempType(jarClass, this.currentJarFile);
		processMethods(jarClass, tempInterface);
		this.tempInterfaces.put(tempInterface.name(), tempInterface);
	}
	
	private void processMethods(JarClass jarClass, JavaTempType javaTempType) {
		if(this.includeMethods) {
			
			MethodLoader methodLoader = new MethodLoader(javaTempType);
			jarClass.accept(methodLoader);
		}
	}
	
	private JavaClass buildClassHierarchy() {
				
		List<JavaTempType> rootClasses = findRootClasses();
		
		if(rootClasses.size() == 1) {
			return resolveTempClass(rootClasses.get(0), null);
		} else {
			throw new Error("Expected exactly 1 root class, " + rootClasses.size() + " found.");
		}
	}
	
	private JavaClass resolveTempClass(JavaTempType tempClass, JavaClass superClass) {

		JavaInterfaceSet implementedInterfaces = resolveImplementedInterfaces(tempClass);
		JavaClass javaClass = tempClass.resolveToJavaClass(superClass, implementedInterfaces);
				
		// Find sub-classes
		for(JavaTempType tempSubClass : this.tempClasses.values()) {
			
			if(tempSubClass.isSubClassOf(tempClass)) {
				javaClass.addSubClass(resolveTempClass(tempSubClass, javaClass));
			}
		}
		// Add the current class as sub-class of each implemented interface.
		for(JavaInterface implementedInterface : implementedInterfaces) {
			implementedInterface.addSubClass(javaClass);
		}
		return javaClass;
	}
	
	private JavaInterfaceSet resolveImplementedInterfaces(JavaTempType tempClass) {
		
		JavaInterfaceSet implementedInterfaces = new JavaInterfaceSet();
		
		if(this.includeInterfaces) {
			
			implementedInterfaces = this.interfaces.find(tempClass.superInterfaces());
			
			if(implementedInterfaces.size() != tempClass.superInterfaces().length) {
				String error = String.format("Interfaces missing. Expected: %s, actual: %s\n", 
					tempClass.superInterfaces().length, implementedInterfaces.size());
				
				throw new Error(error + " " + tempClass.superInterfaces()[0]);
			}
		}
		return implementedInterfaces;
	}
	
	private List<JavaTempType> findRootClasses() {
		
		List<JavaTempType> rootClasses = new ArrayList<JavaTempType>();
		
		for(JavaTempType tempClass : this.tempClasses.values()) {
			if(tempClass.superClass() == null) {
				rootClasses.add(tempClass);
			}
		}
		return rootClasses;
	}
	
	
	private void buildInterfaceHierarchy() {
		
		List<JavaTempType> rootInterfaces = findRootInterfaces();
		
		for(JavaTempType tempInterface : rootInterfaces) {
			
			JavaInterface javaInterface = tempInterface.resolveToJavaInterface(new JavaInterfaceSet());
			
			this.interfaces.add(javaInterface);
			
			// Find sub-interfaces
			for(JavaTempType tempSubInterface : this.tempInterfaces.values()) {
				
				if(tempSubInterface.isSubInterfaceOf(tempInterface)) {
					javaInterface.addSubInterface(resolveTempInterface(tempSubInterface));
				}
			}
		}
	}
	
	private JavaInterface resolveTempInterface(JavaTempType tempInterface) {
		
		// Resolve super-interfaces
		JavaInterfaceSet superInterfaces = new JavaInterfaceSet();
		
		for(JavaTempType tempSuperInterface : this.tempInterfaces.values()) {
	
			if(tempInterface.isSubInterfaceOf(tempSuperInterface)) {

				JavaInterface superInterface = this.interfaces.find(tempSuperInterface.name());
				
				if(superInterface != null) {
					superInterfaces.add(superInterface);
				} else {
					superInterfaces.add(resolveTempInterface(tempSuperInterface));
				}
			}
		}

		if(superInterfaces.size() != tempInterface.superInterfaces().length) {
					
			String error = String.format("Interfaces missing 2. Expected: %s, actual: %s\n", 
					tempInterface.superInterfaces().length, superInterfaces.size());
				
				throw new Error(error + " " + tempInterface.superInterfaces()[0]);			
		}
		
		
		JavaInterface javaInterface = tempInterface.resolveToJavaInterface(superInterfaces);
		this.interfaces.add(javaInterface);
		
		// Find sub-interfaces
		for(JavaTempType tempSubInterface : this.tempInterfaces.values()) {
			
			if(tempSubInterface.isSubInterfaceOf(tempInterface)) {
				javaInterface.addSubInterface(resolveTempInterface(tempSubInterface));
			}
		}
		return javaInterface;
	}
	
	private List<JavaTempType> findRootInterfaces() {

		List<JavaTempType> rootInterfaces = new ArrayList<JavaTempType>();
		
		for(JavaTempType tempInterface : this.tempInterfaces.values()) {
			if(!tempInterface.hasSuperInterfaces()) {
				rootInterfaces.add(tempInterface);
			}
		}
		return rootInterfaces;		
	}
	
	private void resolveOverrides(JavaClass javaClass) {
		
		for(JavaMethod method : javaClass.declaredMethods()) {
			findOverridesFor(method, javaClass);
		}
		
		// Traverse the remaining classes in the class hierarchy.
		for(JavaClass subClass : javaClass.subClasses()) {
			resolveOverrides(subClass);
		}
	}
		
	private void findOverridesFor(JavaMethod baseMethod, JavaClass javaClass) {
		
		for(JavaClass subClass : javaClass.subClasses()) {
			
			JavaMethod override = subClass.findMethod(baseMethod.name(), baseMethod.desc());
			
			if(override != null) {
				baseMethod.overridenBy(override);
			} else {
				findOverridesFor(baseMethod, subClass);
			}
		}
	}
		
	private void println(String format, Object... args) {
		if(this.verbose) {
			System.out.format(format + "\n", args);
		}
	}
}
