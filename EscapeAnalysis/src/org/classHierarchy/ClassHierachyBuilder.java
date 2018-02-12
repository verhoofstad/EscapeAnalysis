package org.classHierarchy;

import java.util.ArrayList;
import java.util.List;

import org.asm.JarClass;
import org.asm.JarFile;
import org.asm.JarFileSetVisitor;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaInterface;
import org.classHierarchy.tree.JavaInterfaceList;
import org.classHierarchy.tree.JavaMethod;

/*
 * Builds a class hierarchy.
 */
public class ClassHierachyBuilder extends JarFileSetVisitor  {
	
	private boolean includeInterfaces = true;
	private boolean includeMethods = true;
	private boolean resolveOverrides = true;
	private boolean verbose = true;
	
	private JarFile currentJarFile;
	
	private List<JavaTempClass> tempClasses = new ArrayList<JavaTempClass>();
	private List<JavaTempClass> tempInterfaces = new ArrayList<JavaTempClass>();
		
	private JavaClass rootNode;
	private JavaInterfaceList interfaces = new JavaInterfaceList();
	
	public JavaClass rootNode() {
		return this.rootNode;
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
		JavaTempClass tempClass = new JavaTempClass(jarClass, this.currentJarFile);
		processMethods(jarClass, tempClass);
		this.tempClasses.add(tempClass);
	}
	
	private void processJarInterface(JarClass jarClass) {
		JavaTempClass tempInterface = new JavaTempClass(jarClass, this.currentJarFile);
		processMethods(jarClass, tempInterface);
		this.tempInterfaces.add(tempInterface);
	}
	
	private void processMethods(JarClass jarClass, JavaTempClass javaTempType) {
		if(this.includeMethods) {
			
			MethodLoader methodLoader = new MethodLoader(javaTempType);
			jarClass.accept(methodLoader);
		}
	}
	
	private JavaClass buildClassHierarchy() {
				
		List<JavaTempClass> rootClasses = findRootClasses();
		
		if(rootClasses.size() == 1) {
			return resolveTempClass(rootClasses.get(0), null);
		} else {
			throw new Error("Expected exactly 1 root class, " + rootClasses.size() + " found.");
		}
	}
	
	private JavaClass resolveTempClass(JavaTempClass tempClass, JavaClass superClass) {

		JavaInterfaceList implementedInterfaces = resolveImplementedInterfaces(tempClass);
		JavaClass javaClass = tempClass.resolveToJavaClass(superClass, implementedInterfaces);
				
		// Find sub-classes
		for(JavaTempClass tempSubClass : this.tempClasses) {
			
			if(tempSubClass.isSubClassOf(tempClass)) {
				javaClass.addSubClass(resolveTempClass(tempSubClass, javaClass));
			}
		}
		return javaClass;
	}
	
	private JavaInterfaceList resolveImplementedInterfaces(JavaTempClass tempClass) {
		
		JavaInterfaceList implementedInterfaces = new JavaInterfaceList();
		
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
	
	private List<JavaTempClass> findRootClasses() {
		
		List<JavaTempClass> rootClasses = new ArrayList<JavaTempClass>();
		
		for(JavaTempClass tempClass : this.tempClasses) {
			if(tempClass.superClass() == null) {
				rootClasses.add(tempClass);
			}
		}
		return rootClasses;
	}
	
	
	private void buildInterfaceHierarchy() {
		
		List<JavaTempClass> rootInterfaces = findRootInterfaces();
		
		for(JavaTempClass tempInterface : rootInterfaces) {
			
			JavaInterface javaInterface = tempInterface.resolveToJavaInterface(new JavaInterfaceList());
			
			this.interfaces.add(javaInterface);
			
			// Find sub-interfaces
			for(JavaTempClass tempSubInterface : this.tempInterfaces) {
				
				if(tempSubInterface.isSubInterfaceOf(tempInterface)) {
					javaInterface.addSubInterface(resolveTempInterface(tempSubInterface));
				}
			}
		}
	}
	
	private JavaInterface resolveTempInterface(JavaTempClass tempInterface) {
		
		// Resolve super-interfaces
		JavaInterfaceList superInterfaces = new JavaInterfaceList();
		
		for(JavaTempClass tempSuperInterface : this.tempInterfaces) {
	
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
		for(JavaTempClass tempSubInterface : this.tempInterfaces) {
			
			if(tempSubInterface.isSubInterfaceOf(tempInterface)) {
				javaInterface.addSubInterface(resolveTempInterface(tempSubInterface));
			}
		}
		return javaInterface;
	}
	
	private List<JavaTempClass> findRootInterfaces() {

		List<JavaTempClass> rootInterfaces = new ArrayList<JavaTempClass>();
		
		for(JavaTempClass tempInterface : this.tempInterfaces) {
			if(!tempInterface.hasSuperInterfaces()) {
				rootInterfaces.add(tempInterface);
			}
		}
		return rootInterfaces;		
	}
	
	private void resolveOverrides(JavaClass javaClass) {
		
		if(javaClass.hasSuperClass()) {
			for(JavaMethod method : javaClass.declaredMethods()) {
				method.setOverrides(findBaseMethodFor(javaClass.superClass(), method));
			}
		}
		
		for(JavaClass subClass : javaClass.subClasses()) {
			resolveOverrides(subClass);
		}
	}
		
	private JavaMethod findBaseMethodFor(JavaClass superClass, JavaMethod method) {
		
		for(JavaMethod baseMethod : superClass.declaredMethods()) {
			if(baseMethod.signatureEquals(method)) {
				return baseMethod;
			}
		}
		if(superClass.hasSuperClass()) {
			return findBaseMethodFor(superClass.superClass(), method);
		} else {
			return null;
		}
	}
	
	private void println(String format, Object... args) {
		if(this.verbose) {
			System.out.format(format + "\n", args);
		}
	}
}
