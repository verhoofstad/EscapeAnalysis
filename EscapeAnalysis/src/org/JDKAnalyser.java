package org;

import org.asm.JarFileSet;
import org.classHierarchy.ClassHierachyBuilder;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaTypeSet;
import org.escapeAnalysis.EscapeAnalysis;
import org.methodFinding.JarFileSetMethodFinder;

public class JDKAnalyser {

	private JarFileSet jdkFiles;
	private JavaTypeSet jdkPackagePrivateClasses;
	private JavaTypeSet jdkConfinedClasses;

	public JDKAnalyser(String jdkFolder) {
		this.jdkFiles = new JarFileSet(jdkFolder);
	}
	
	public JavaTypeSet jdkPackagePrivateClasses() {
		return this.jdkPackagePrivateClasses;
	}
	
	public JavaTypeSet jdkConfinedClasses() {
		return this.jdkConfinedClasses;
	}
	
	public void analyseJDK() {
				
		System.out.println("Pre-analyse the JDK library separately...");
		
		System.out.print("Building class hierarchy...");
		ClassHierachyBuilder builder = new ClassHierachyBuilder();
		jdkFiles.accept(builder);
		ClassHierarchy classHierarchy = builder.classHierarchy();
		System.out.println("Ok");
		
		this.jdkPackagePrivateClasses = classHierarchy.getFinalPackagePrivateClasses();

		System.out.print("Find the methods in which package-private classes are instantiated...");
		JarFileSetMethodFinder methodFinder = new JarFileSetMethodFinder(classHierarchy, this.jdkPackagePrivateClasses);
		jdkFiles.accept(methodFinder);
		System.out.println("Ok");
		System.out.format("Total of %s methods found.\n", methodFinder.foundMethods().size());
		
		EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());
		
		escapeAnalysis.analyse(methodFinder.foundMethods(), jdkFiles);
		
		this.jdkConfinedClasses = new JavaTypeSet(this.jdkPackagePrivateClasses);
		this.jdkConfinedClasses.difference(escapeAnalysis.escapingClasses());
		
		System.out.format("Final package-private classes count: %s\n", this.jdkPackagePrivateClasses.size());
		System.out.format("Escaping classes count:              %s\n", escapeAnalysis.escapingClasses().size());
		System.out.format("Confined classes count:              %s\n", this.jdkConfinedClasses.size());
	}
}
