package org;

import java.io.File;

import org.asm.JarFileSet;
import org.classHierarchy.ClassHierachyBuilder;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaClassList;
import org.classHierarchy.tree.JavaMethod;
import org.counting.ClassCounter;
import org.methodFinding.JarFileSetMethodFinder;
import org.soot.BytecodeConverter;

public class Main {

	public static void main(String[] args) {
	
		String path1 = "C:\\CallGraphData\\JavaJDK\\java-8-openjdk-amd64";
		String path2 = "C:\\CallGraphData\\TestProject.jar";
		
		JarFileSet jarFiles = new JarFileSet(path1);
		jarFiles.add(new File(path2));
		
		System.out.format("Number of JAR-files: %s\n", jarFiles.size());

		/*
		System.out.println("Getting some totals...");
		ClassCounter classCounter = new ClassCounter();
		jarFiles.accept(classCounter);
		classCounter.printTotals();
		*/
		System.out.println("Get class hierarchy...");
		ClassHierachyBuilder builder = new ClassHierachyBuilder();
		jarFiles.accept(builder);

		JavaClass javaObject = builder.rootNode();
		System.out.format("Class hierarchy contains %s classes.\n", javaObject.classCount());
		/*
		System.out.println("Get package private classes...");
		JavaClassList packagePrivateClasses = javaObject.getFinalPackagePrivateClasses();
		System.out.format("Final package-private classes: %s\n", packagePrivateClasses.size());

		System.out.println("Find the methods in which they are instantiated...");
		JarFileSetMethodFinder methodFinder = new JarFileSetMethodFinder(javaObject, packagePrivateClasses);
		jarFiles.accept(methodFinder);
		System.out.format("Total of %s methods found.\n", methodFinder.foundMethods().size());
		
		System.out.format("Abstract method count: %s\n", javaObject.abstractMethodCount(true));
		*/
		JavaClass someClass = javaObject.findClass("org/SomeClass");

		BytecodeConverter conv = new BytecodeConverter();
		
		conv.test(someClass.declaredMethods(), jarFiles);
		System.out.println("Done");
	}
}
