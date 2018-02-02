package org;

import org.asm.JarFileList;
import org.classHierachy.ClassHierachyBuilder;
import org.methodFinding.JarFileMethodFinder;
import org.soot.BytecodeConverter;
import org.tree.JavaClass;
import org.tree.JavaClassList;

public class Main {

	
	public static void main(String[] args) {
	
		String path1 = "C:\\CallGraphData\\JavaJDK\\java-8-openjdk-amd64";

		JarFileList jarFiles = new JarFileList(path1);
		
		System.out.format("Classpath: %s\n", jarFiles.getClassPath());
		
		ClassHierachyBuilder builder = new ClassHierachyBuilder();
		
		System.out.format("Number of JAR-files: %s\n", jarFiles.size());
		
		System.out.println("Get class list...");
		jarFiles.accept(builder);
		JavaClassList classList =  builder.getClassList();

		System.out.println("Convert to tree...");
		JavaClass javaObject = classList.toTree();
		
		System.out.format("List contains %s classes.\n", classList.size());
		System.out.format("Tree contains %s classes.\n", javaObject.size());
		
		System.out.println("Get package private classes...");
		JavaClassList packagePrivateClasses = javaObject.getFinalPackagePrivateClasses();
		
		System.out.format("Final package-private classes: %s\n", packagePrivateClasses.size());
		
		JarFileMethodFinder methodFinder = new JarFileMethodFinder(javaObject, packagePrivateClasses);
		
		System.out.println("Find the methods in which they are instantiated...");
		jarFiles.accept(methodFinder);

		System.out.format("Total of %s methods found.\n", javaObject.allMethodCount());
		
		
		BytecodeConverter conv = new BytecodeConverter();
		
		conv.test(javaObject, jarFiles);
	
	}
}
