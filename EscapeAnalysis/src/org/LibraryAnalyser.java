package org;

import org.asm.JarFileSet;
import org.callGraphs.CallGraph;
import org.callGraphs.cha.ClassHierarchyAnalysis;
import org.callGraphs.rta.RapidTypeAnalysis;
import org.classHierarchy.ClassHierachyBuilder;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaClass;
import org.counting.ClassCounter;
import org.dataSets.Library;

public class LibraryAnalyser {

	private Library library;
	
	private boolean buildChaGraph = false;
	
	
	public LibraryAnalyser(Library library) {
		this.library = library;
	}
	
	public void analyse() {
		
		JarFileSet jarFiles = this.library.jarFiles();
		
		System.out.format("PROCESSING: %s with %s | %s | %s\n", library.id(), library.organisation(), library.name(), library.revision());
		System.out.println();
		
		System.out.format("Number of JAR-files: %s\n", jarFiles.size());
		
		System.out.println("Getting some totals...");
		ClassCounter classCounter = new ClassCounter();
		jarFiles.accept(classCounter);
		classCounter.printTotals();
		/*
		System.out.print("Building class hierarchy...");
		ClassHierachyBuilder builder = new ClassHierachyBuilder();
		jarFiles.accept(builder);
		ClassHierarchy javaObject = builder.classHierarchy();
		System.out.println("Ok");

		System.out.format("Class hierarchy contains %s classes.\n", javaObject.classCount());
		
		System.out.print("Performing Class Hierarchy Analysis...");
		ClassHierarchyAnalysis cha = new ClassHierarchyAnalysis(javaObject);
		jarFiles.accept(cha);
		CallGraph chaGraph = cha.callGraph();
		System.out.println("Ok");
		chaGraph.printReport();
		
		System.out.print("Performing Rapid Type Analysis...");
		RapidTypeAnalysis rta = new RapidTypeAnalysis(chaGraph);
		rta.setLibraryAnalysis(javaObject.getPublicClasses(), javaObject.getExportedMethods());
		rta.analyse();
		CallGraph rtaGraph = rta.callGraph();
		System.out.println("Ok");
		rtaGraph.printReport();
		
		
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
		//JavaClass someClass = javaObject.getClass("org/SomeClass");

		/*
		BytecodeConverter conv = new BytecodeConverter();
		
		conv.test(someClass.declaredMethods(), jarFiles);
		*/
		System.out.println("Done");
	}
}
