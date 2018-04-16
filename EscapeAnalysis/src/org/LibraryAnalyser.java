package org;

import org.asm.JarFileSet;
import org.callGraphs.CallGraph;
import org.callGraphs.cha.ClassHierarchyAnalysis;
import org.callGraphs.rta.RapidTypeAnalysis;
import org.classHierarchy.ClassHierachyBuilder;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaTypeSet;
import org.counting.ClassCounter;
import org.dataSets.Library;
import org.dataSets.LibraryResult;
import org.escapeAnalysis.EscapeAnalysis;
import org.methodFinding.JarFileSetMethodFinder;

public class LibraryAnalyser {

	private Library library;
	private LibraryResult result;
	
	private boolean buildHierarchies = true;
	private boolean buildGraphs = true;
	private boolean includeEscapeAnalysis = true;
	
	private JavaTypeSet jdkPackagePrivateClasses;
	private JavaTypeSet jdkConfinedClasses;
	
	public LibraryAnalyser(Library library, LibraryResult result) {
		this.library = library;
		this.result = result;
	}

	
	public void setJDKResults(JavaTypeSet jdkPackagePrivateClasses, JavaTypeSet jdkConfinedClasses) {
		this.jdkPackagePrivateClasses = jdkPackagePrivateClasses;
		this.jdkConfinedClasses = jdkConfinedClasses;
	}
	
	public void analyse() {
		
		JarFileSet jarFiles = this.library.jarFiles();
		
		System.out.format("PROCESSING: %s with %s | %s | %s\n", library.id(), library.organisation(), library.name(), library.revision());
		System.out.println();
		System.out.format("CPFILE: %s\n", library.cpFile().getAbsolutePath());
		System.out.format("JAR FILES: %s\n", library.jarFiles().size());
		System.out.println();

		ClassCounter classCounter = new ClassCounter(library);
		jarFiles.accept(classCounter);
		printTotals(classCounter.libraryResult(), this.result);
		
		if(this.buildHierarchies) {
		
			System.out.print("Building class hierarchy...");
			ClassHierachyBuilder builder = new ClassHierachyBuilder();
			jarFiles.accept(builder);
			ClassHierarchy javaObject = builder.classHierarchy();
			System.out.println("Ok");
			
			JavaTypeSet packagePrivateClasses = javaObject.getFinalPackagePrivateClasses();
			
			JavaMethodSet exportedMethods = javaObject.getExportedMethods();
	
			System.out.format("Class hierarchy contains %s classes.\n", javaObject.classCount());
			System.out.format("There are %s final package-private classes.\n", packagePrivateClasses.size());
			System.out.format("Number of RTA entry points: %s\n", exportedMethods.size());
			
			int exportedLibMethods = 0;
			for(JavaMethod exportedMethod : exportedMethods) {
				if(exportedMethod.jarFile().equals(this.library.cpFile())) {
					exportedLibMethods++;
				}
			}
			System.out.format("Number of RTA entry points (library): %s\n", exportedLibMethods);
			JavaMethodSet entryPoints = javaObject.getLibCHAcpaEntryPoints(library.cpFile());
			
			System.out.format("Number of entry points (library): %s\n", entryPoints.size());
			
			//System.out.format("Number of LibCHAcpa entry points: %s\n", this.result.cpa_entryPoints);
			
			if(this.buildGraphs) {
	
				System.out.print("Performing Class Hierarchy Analysis...");
				ClassHierarchyAnalysis cha = new ClassHierarchyAnalysis(javaObject);
				jarFiles.accept(cha);
				CallGraph chaGraph = cha.callGraph();
				System.out.println("Ok");
				System.out.format("CHA Graph has %s edges in %s call sites.\n", chaGraph.nrOfEdges(), chaGraph.nrOfCallSites());
				
				System.out.print("Performing Rapid Type Analysis...");
				RapidTypeAnalysis rta = new RapidTypeAnalysis(chaGraph);
				rta.setLibraryAnalysis(javaObject.getPublicClasses(), entryPoints);
				rta.analyse();
				CallGraph rtaGraph = rta.callGraph();
				System.out.println("Ok");
				
				CallGraph rtaGraphEA = new CallGraph(); 
				
				if(this.includeEscapeAnalysis) {
	
					// Since we already determined the confined classes of the JDK,
					// we do not need to analyze them again.
					packagePrivateClasses.difference(this.jdkPackagePrivateClasses);
					
					System.out.print("Find the methods in which package-private classes are instantiated...");
					JarFileSetMethodFinder methodFinder = new JarFileSetMethodFinder(javaObject, packagePrivateClasses);
					jarFiles.accept(methodFinder);
					System.out.println("Ok");
					System.out.format("Total of %s methods found.\n", methodFinder.foundMethods().size());
					
					EscapeAnalysis escapeAnalysis = new EscapeAnalysis(javaObject.getClasses());
					
					try {
						escapeAnalysis.analyse(methodFinder.foundMethods(), jarFiles);
						
						JavaTypeSet confinedClasses = new JavaTypeSet(packagePrivateClasses);
						confinedClasses.difference(escapeAnalysis.escapingClasses());
						
						System.out.format("Final package-private classes count: %s\n", packagePrivateClasses.size());
						System.out.format("Escaping classes count:              %s\n", escapeAnalysis.escapingClasses().size());
						System.out.format("Confined classes count:              %s\n", confinedClasses.size());
						System.out.format("JDK confined classes count:          %s\n", this.jdkConfinedClasses.size());

						confinedClasses.addAll(this.jdkConfinedClasses);
						System.out.format("Total confined classes count:        %s\n", confinedClasses.size());

						System.out.print("Performing Rapid Type Analysis with Escape Analysis...");
						RapidTypeAnalysis rtaEA = new RapidTypeAnalysis(chaGraph);
						rtaEA.setLibraryAnalysis(javaObject.getPublicClasses(), entryPoints);
						rtaEA.setConfinedClasses(confinedClasses);
						rtaEA.analyse();
						rtaGraphEA = rtaEA.callGraph();
						System.out.println("Ok");
						
					} catch(Exception ex) {
						System.out.println("Soot exception occured!");
						System.out.format("Message: %s\n", ex.getMessage());
					}
				}

				System.out.print("Performing Rapid Type Analysis with Escape Analysis... (best-case)");
				RapidTypeAnalysis rtaEAMax = new RapidTypeAnalysis(chaGraph);
				rtaEAMax.setLibraryAnalysis(javaObject.getPublicClasses(), entryPoints);
				rtaEAMax.setConfinedClasses(javaObject.getFinalPackagePrivateClasses());
				rtaEAMax.analyse();
				CallGraph rtaGraphEAMax = rtaEAMax.callGraph();
				System.out.println("Ok");
				
				System.out.println("--------------------------------------------------------------------------------------------------------");
				System.out.format("                                 | %9s | %9s | %10s | %10s | %9s | %9s |\n", 
					"CHA", "RTA", "RTA EA", "RTA EA max", "LibCPA", "LibCPA CBS");
				System.out.println("--------------------------------------------------------------------------------------------------------");
				
				System.out.format("Total number edges               | %9s | %9s | %10s | %10s | %9s | %9s |\n",
					chaGraph.nrOfEdges(), rtaGraph.nrOfEdges(), rtaGraphEA.nrOfEdges(), rtaGraphEAMax.nrOfEdges(), this.result.cpa_callEdgesCount, this.result.cpa_callBySignatureEdgesCount);
				System.out.format("Total number of call sites       | %9s | %9s | %10s | %10s |\n",
					chaGraph.nrOfCallSites(), rtaGraph.nrOfCallSites(), rtaGraphEA.nrOfCallSites(), rtaGraphEAMax.nrOfCallSites());
				System.out.format(" - Virtual call sites            | %9s | %9s | %10s | %10s |\n",
					chaGraph.nrOfVirtualCallSites(), rtaGraph.nrOfVirtualCallSites(), rtaGraphEA.nrOfVirtualCallSites(), rtaGraphEAMax.nrOfVirtualCallSites());
				System.out.format("     (of which are monomorphic)  | %9s | %9s | %10s | %10s |\n",
					chaGraph.nrOfVirtualMonoCallSites(), rtaGraph.nrOfVirtualMonoCallSites(), rtaGraphEA.nrOfVirtualMonoCallSites(), rtaGraphEAMax.nrOfVirtualMonoCallSites());
				System.out.format("     (of which are empty)        | %9s | %9s | %10s | %10s |\n",
						chaGraph.nrOfVirtualEmptyCallSites(), rtaGraph.nrOfVirtualEmptyCallSites(), rtaGraphEA.nrOfVirtualEmptyCallSites(), rtaGraphEAMax.nrOfVirtualEmptyCallSites());
				System.out.format(" - Static call sites             | %9s | %9s | %10s | %10s |\n",
					chaGraph.nrOfStaticCallSites(), rtaGraph.nrOfStaticCallSites(), rtaGraphEA.nrOfStaticCallSites(), rtaGraphEAMax.nrOfStaticCallSites());
				System.out.println("------------------------------------------------------------------------------------");
			}
		}
		System.out.println();
	}
	
	public void printTotals(LibraryResult result) {
		System.out.format("Public class count:              %9s\n", result.all_publicClassCount);
		System.out.format("Package-private class count:     %9s\n", result.all_packageVisibleClassCount);
		System.out.format("Total class count:               %9s\n", result.all_classCount);
		System.out.println();
		System.out.format("Public interface count:          %9s\n", result.all_publicInterfaceCount);
		System.out.format("Package-private interface count: %9s\n", result.all_packageVisibleInterfaceCount);
		System.out.format("Total interface count:           %9s\n", result.all_interfaceCount);
		System.out.println();
		System.out.format("Public method count:             %9s\n", result.all_publicMethods);
		System.out.format("Protected method count:          %9s\n", result.all_protectedMethods);
		System.out.format("Package-private method count:    %9s\n", result.all_packagePrivateMethods);
		System.out.format("Private method count:            %9s\n", result.all_privateMethods);
		System.out.format("Total method count:              %9s\n", result.all_methodCount);
		System.out.println();
	}
	
	public void printTotals(LibraryResult result1, LibraryResult result2) {
		if(result2 == null) {
			printTotals(result1);
			return;
		}
		
		printLine("Public class count", result1.all_publicClassCount, result2.all_publicClassCount);
		printLine("Package-private class count", result1.all_packageVisibleClassCount, result2.all_packageVisibleClassCount);
		printLine("Total class count", result1.all_classCount, result2.all_classCount);
		System.out.println();
		printLine("Public interface count", result1.all_publicInterfaceCount, result2.all_publicInterfaceCount);
		printLine("Package-private interface count", result1.all_packageVisibleInterfaceCount, result2.all_packageVisibleInterfaceCount);
		printLine("Total interface count", result1.all_interfaceCount, result2.all_interfaceCount);
		System.out.println();
		printLine("Public method count", result1.all_publicMethods, result2.all_publicMethods);
		printLine("Protected method count", result1.all_protectedMethods, result2.all_protectedMethods);
		printLine("Package-private method count", result1.all_packagePrivateMethods, result2.all_packagePrivateMethods);
		printLine("Private method count", result1.all_privateMethods, result2.all_privateMethods);
		printLine("Total method count", result1.all_methodCount, result2.all_methodCount);
		System.out.println();
	}
	
	private void printLine(String description, int result1, int result2) {
		
		System.out.format("%-33s : %9s  %9s", description, result1, result2);
		if(result1 != result2) {
			System.out.format("%9s", result1 - result2);
		}
		System.out.println();
	}
}
