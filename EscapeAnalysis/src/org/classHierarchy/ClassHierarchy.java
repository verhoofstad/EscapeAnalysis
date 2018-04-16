package org.classHierarchy;

import java.util.HashSet;
import java.util.Set;

import org.asm.JarFile;
import org.asm.JarFileSet;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;
import org.classHierarchy.tree.JavaTypeSet;

public class ClassHierarchy {

	private JavaType rootNode;
	private JavaTypeSet interfaces;
	private JavaTypeSet classes;
	
	public ClassHierarchy(JavaType rootNode, JavaTypeSet classes, JavaTypeSet interfaces) {
		
		this.rootNode = rootNode;
		this.classes = classes;
		this.interfaces = interfaces;
	}
	
	public JavaType findType(String internalName) 
	{
		if(this.classes.contains(internalName)) {
			return this.classes.get(internalName);
		} else if(this.interfaces.contains(internalName)) {
			return this.interfaces.get(internalName);
		} else {
			return null;
		}
	}
	
	public JavaType findClass(String internalName) {
		return this.classes.find(internalName);
	}

	public JavaType getClass(String internalName) {
		return this.classes.get(internalName);
	}
	
	public JavaType getInterface(String internalName) {
		return this.interfaces.get(internalName);
	}
	
	public JavaTypeSet getClasses() {
		return this.classes;
	}
	
	public JavaTypeSet getInterfaces() {
		return this.interfaces;
	}
	
	/*
	 * Gets the public classes (exported classes for RTA).
	 */
	public JavaTypeSet getPublicClasses() {
		JavaTypeSet publicClasses = new JavaTypeSet();
		for(JavaType javaClass : this.classes) {
			if(javaClass.isPublic()) {
				publicClasses.add(javaClass);
			}
		}
		return publicClasses;
	}
	
	/*
	 * Gets the exported methods for RTA.
	 */
	public JavaMethodSet getExportedMethods() {
		JavaMethodSet exportedMethods = new JavaMethodSet();
		for(JavaType publicClass : this.getPublicClasses()) {

			boolean isFinal = publicClass.isFinal();
			for(JavaMethod method : publicClass.declaredMethods()) {
				
				if(method.isPublic() || (method.isProtected() && isFinal)) {
					exportedMethods.add(method);
				}
			}
		}
		return exportedMethods;
	}
	
	/*
	 * Gets the entry points.
	 */
	public JavaMethodSet getLibCHAcpaEntryPoints(JarFile cpFile) {
		
		JavaMethodSet entryPoints = new JavaMethodSet();
		
		for(JavaType javaClass : this.classes) {
			
			if(javaClass.jarFile().equals(cpFile)) {
				
				for(JavaMethod declaredMethod : javaClass.declaredMethods()) {

					if(isEntryPoint((JavaClass)javaClass, declaredMethod)) {
						entryPoints.add(declaredMethod);
					}
				}
			}
		}

		for(JavaType javaInterface : this.interfaces) {
			
			if(javaInterface.jarFile().equals(cpFile)) {
				
				for(JavaMethod declaredMethod : javaInterface.declaredMethods()) {

					if(!declaredMethod.isAbstract()
						&& declaredMethod.isClientCallable()) {
						entryPoints.add(declaredMethod);
					}
				}
			}
		}
		
		return entryPoints;
	}
	
	/*
	 * 1 def isEntryPoint(declType,method):Boolean =
2 maybeCalledByTheJVM(method) || 
3 (method.isSaticInitializer && declType.isAccessible) ||
 4 (method.isClientCallable &&
5 ( method.isStatic || declType.isInstantiable))
	 */
	private boolean isEntryPoint(JavaClass declType, JavaMethod method) {
		
		return maybeCalledByTheJVM(method) ||
			(method.isStaticInitializer() && declType.isAccessible()) ||
			(method.isClientCallable() &&
				(method.isStatic() || declType.isInstantiable()));
	}
	
	private boolean maybeCalledByTheJVM(JavaMethod method) {
		return method.name().equals("finalize");
	}

	public JavaTypeSet getFinalPackagePrivateClasses() {
		JavaTypeSet finalPackagePrivateClasses = new JavaTypeSet();
		for(JavaType javaClass : this.classes) {
			if(javaClass.isFinalPackagePrivate()) {
				finalPackagePrivateClasses.add(javaClass);
			}
		}
		return finalPackagePrivateClasses;
	}
	
	public int classCount() {
		return this.classes.size();
	}
	
	public JarFileSet jarFiles() {
		Set<JarFile> jarFiles = new HashSet<JarFile>();
		getJarFiles(this.rootNode, jarFiles);
		return new JarFileSet(jarFiles);
	}
	
	private void getJarFiles(JavaType javaClass, Set<JarFile> jarFiles) {
		
		jarFiles.add(javaClass.jarFile());
		for(JavaType subClass : javaClass.subClasses()) {
			getJarFiles(subClass, jarFiles);
		}
	}
}