package org;

import org.asm.JarFile;
import org.asm.JarFileSet;
import org.callGraphs.CallGraph;
import org.callGraphs.cha.ClassHierarchyAnalysis;
import org.callGraphs.rta.RapidTypeAnalysis;
import org.classHierarchy.ClassHierachyBuilder;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;
import org.classHierarchy.tree.JavaTypeSet;
import org.counting.ClassCounter;
import org.counting.CountResults;
import org.dataSets.Library;
import org.results.LibraryResult;
import org.escapeAnalysis.EscapeAnalysis;
import org.methodFinding.JarFileSetMethodFinder;

public class LibraryAnalyser {

    private Library library;

    private boolean includeEscapeAnalysis = true;

    private JavaTypeSet jdkPackagePrivateClasses;
    private JavaTypeSet jdkConfinedClasses;

    public LibraryAnalyser(Library library) {
        this.library = library;
    }

    public void setJDKResults(JavaTypeSet jdkPackagePrivateClasses, JavaTypeSet jdkConfinedClasses) {
        this.jdkPackagePrivateClasses = jdkPackagePrivateClasses;
        this.jdkConfinedClasses = jdkConfinedClasses;
    }

    public LibraryResult analyse() {

        JarFileSet jarFiles = this.library.jarFiles();
        JarFile cpFile = this.library.cpFile();

        System.out.format("PROCESSING: %s with %s | %s | %s\n", library.id(), library.organisation(), library.name(), library.revision());
        System.out.println();
        System.out.format("CPFILE: %s\n", cpFile.toString());
        System.out.format("JAR FILES: %s\n", jarFiles.size());
        System.out.println();

        System.out.print("Counting classes and methods...");
        ClassCounter libraryCounter = new ClassCounter();
        cpFile.accept(libraryCounter);
        CountResults libraryCounts = libraryCounter.countResults();
        System.out.println("Ok");
        
        System.out.print("Building class hierarchy...");
        ClassHierachyBuilder builder = new ClassHierachyBuilder();
        jarFiles.accept(builder);
        ClassHierarchy classHierarchy = builder.classHierarchy();
        System.out.println("Ok");

        System.out.print("Performing Class Hierarchy Analysis...");
        ClassHierarchyAnalysis cha = new ClassHierarchyAnalysis(classHierarchy);
        jarFiles.accept(cha);
        CallGraph chaGraph = cha.callGraph();
        System.out.println("Ok");

        JavaMethodSet entryPoints = classHierarchy.getExportedMethods(this.library.cpFile());

        System.out.print("Performing Rapid Type Analysis...");
        RapidTypeAnalysis rta = new RapidTypeAnalysis(chaGraph);
        rta.setLibraryAnalysis(classHierarchy.getPublicClasses(), entryPoints);
        rta.analyse();
        CallGraph rtaGraph = rta.callGraph();
        System.out.println("Ok");
        
        
        JavaTypeSet packagePrivateClasses = classHierarchy.getFinalPackagePrivateClasses();
        JavaTypeSet confinedClasses = new JavaTypeSet();

        System.out.format("There are %s final package-private classes.\n", packagePrivateClasses.size());
        System.out.format("Number of RTA entry points: %s\n", entryPoints.size());

        CallGraph rtaGraphEA = new CallGraph();
        int libraryConfinedClassCount = 0;

        if (this.includeEscapeAnalysis) {

            // Since we already determined the confined classes of the JDK,
            // we do not need to analyze them again.
            packagePrivateClasses = packagePrivateClasses.difference(this.jdkPackagePrivateClasses);

            if (packagePrivateClasses.size() > 0) {

                System.out.print("Find the methods in which package-private classes are instantiated...");
                JarFileSetMethodFinder methodFinder = new JarFileSetMethodFinder(classHierarchy, packagePrivateClasses);
                jarFiles.accept(methodFinder);
                System.out.println("Ok");
                System.out.format("Total of %s methods found.\n", methodFinder.foundMethods().size());

                if (methodFinder.foundMethods().size() > 0) {

                    EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());

                    try {
                        escapeAnalysis.analyse(methodFinder.foundMethods(), jarFiles);

                        confinedClasses = packagePrivateClasses.difference(escapeAnalysis.escapingClasses());

                        System.out.format("Final package-private classes count: %s\n", packagePrivateClasses.size());
                        System.out.format("Escaping classes count:              %s\n", escapeAnalysis.escapingClasses().size());
                        System.out.format("Confined classes count:              %s\n", confinedClasses.size());
                        System.out.format("JDK confined classes count:          %s\n", this.jdkConfinedClasses.size());

                        confinedClasses.addAll(this.jdkConfinedClasses);
                        System.out.format("Total confined classes count:        %s\n", confinedClasses.size());
                        
                        // Count the confined final package-private classes in the library.
                        JavaTypeSet libraryFinalPackagePrivateClasses = new JavaTypeSet();
                        for(JavaType finalPackagePrivateClass : classHierarchy.getFinalPackagePrivateClasses()) {
                            if(finalPackagePrivateClass.jarFile().equals(this.library.cpFile())) {
                                libraryFinalPackagePrivateClasses.add(finalPackagePrivateClass);
                            }
                        }
                        System.out.format("Library final package-private class count: %s -- %s\n", 
                                libraryCounts.packagePrivateClassCount, libraryFinalPackagePrivateClasses.size());
                        JavaTypeSet libraryConfinedClasses = libraryFinalPackagePrivateClasses.difference(escapeAnalysis.escapingClasses());
                        libraryConfinedClassCount = libraryConfinedClasses.size();

                    } catch (Exception ex) {
                        System.out.println("Soot exception occured!");
                        System.out.format("Message: %s\n", ex.getMessage());
                    }
                } else {
                    System.out.println("No methods found which instantiate a package-private class.");
                    confinedClasses.addAll(this.jdkConfinedClasses);
                }
            } else {
                System.out.println("Library has no package-private classes.");
                confinedClasses.addAll(this.jdkConfinedClasses);
            }

            System.out.print("Performing Rapid Type Analysis with Escape Analysis...");
            RapidTypeAnalysis rtaEA = new RapidTypeAnalysis(chaGraph);
            rtaEA.setLibraryAnalysis(classHierarchy.getPublicClasses(), entryPoints);
            rtaEA.setConfinedClasses(confinedClasses);
            rtaEA.analyse();
            rtaGraphEA = rtaEA.callGraph();
            System.out.println("Ok");
        }

        System.out.print("Performing Rapid Type Analysis with Escape Analysis (best-case)...");
        RapidTypeAnalysis rtaEAMax = new RapidTypeAnalysis(chaGraph);
        rtaEAMax.setLibraryAnalysis(classHierarchy.getPublicClasses(), entryPoints);
        rtaEAMax.setConfinedClasses(classHierarchy.getFinalPackagePrivateClasses());
        rtaEAMax.analyse();
        CallGraph rtaGraphEAMax = rtaEAMax.callGraph();
        System.out.println("Ok");

        System.out.print("Determining dead methods...");
        JavaMethodSet rtaDeadMethods = rtaGraph.getDeadMethods(classHierarchy, this.library.cpFile());
        JavaMethodSet rtaEaDeadMethods = rtaGraphEA.getDeadMethods(classHierarchy, this.library.cpFile());
        JavaMethodSet rtaMaxDeadMethods = rtaGraphEAMax.getDeadMethods(classHierarchy, this.library.cpFile());
        System.out.println("Ok");        
        
        printGraphTotals(chaGraph, rtaGraph, rtaGraphEA, rtaGraphEAMax);
        System.out.println();
        
        LibraryResult libraryResult = new LibraryResult(this.library);
        libraryResult.libraryPublicClassCount = libraryCounts.publicClassCount;
        libraryResult.libraryPackagePrivateClassCount = libraryCounts.packagePrivateClassCount;
        libraryResult.libraryConfinedClassCount = libraryConfinedClassCount;
        
        libraryResult.rtaEdgeCount = rtaGraph.nrOfEdges(this.library.cpFile());
        libraryResult.rtaEaEdgeCount = rtaGraphEA.nrOfEdges(this.library.cpFile());
        libraryResult.rtaMaxEdgeCount = rtaGraphEAMax.nrOfEdges(this.library.cpFile());
        
        libraryResult.rtaCallSiteCount = rtaGraph.nrOfCallSites(this.library.cpFile());
        libraryResult.rtaVirtualCallSiteCount = rtaGraph.nrOfVirtualCallSites(this.library.cpFile());
        libraryResult.rtaMonomorphicCallSiteCount = rtaGraph.nrOfMonomorphicCallSites(this.library.cpFile());
        libraryResult.rtaEaCallSiteCount = rtaGraphEA.nrOfCallSites(this.library.cpFile());
        libraryResult.rtaEaVirtualCallSiteCount = rtaGraphEA.nrOfVirtualCallSites(this.library.cpFile());
        libraryResult.rtaEaMonomorphicCallSiteCount = rtaGraphEA.nrOfMonomorphicCallSites(this.library.cpFile());
        libraryResult.rtaEaNewMonomorphicCallSiteCount = rtaGraphEA.nrOfNewMonomorphicCallSites(this.library.cpFile());
        libraryResult.rtaMaxCallSiteCount = rtaGraphEAMax.nrOfCallSites(this.library.cpFile());
        libraryResult.rtaMaxVirtualCallSiteCount = rtaGraphEAMax.nrOfVirtualCallSites(this.library.cpFile());
        libraryResult.rtaMaxMonomorphicCallSiteCount = rtaGraphEAMax.nrOfMonomorphicCallSites(this.library.cpFile());
        libraryResult.rtaMaxNewMonomorphicCallSiteCount = rtaGraphEAMax.nrOfNewMonomorphicCallSites(this.library.cpFile());
        
        libraryResult.rtaDeadMethods = rtaDeadMethods.size();
        libraryResult.rtaEaDeadMethods = rtaEaDeadMethods.size();
        libraryResult.rtaMaxDeadMethods = rtaMaxDeadMethods.size();

        return libraryResult;
    }

    private void printGraphTotals(CallGraph chaGraph, CallGraph rtaGraph, CallGraph rtaGraphEA,
            CallGraph rtaGraphEAMax) {

        System.out.println("------------------------------------------------------------------------------------");
        System.out.format("                                 | %9s | %9s | %10s | %10s |\n", "CHA", "RTA", "RTA EA", "RTA EA max");
        System.out.println("------------------------------------------------------------------------------------");

        System.out.format("Total number edges               | %9s | %9s | %10s | %10s |\n",
                chaGraph.nrOfEdges(), rtaGraph.nrOfEdges(), rtaGraphEA.nrOfEdges(), rtaGraphEAMax.nrOfEdges());
        System.out.format("Total number of call sites       | %9s | %9s | %10s | %10s |\n", chaGraph.nrOfCallSites(),
                rtaGraph.nrOfCallSites(), rtaGraphEA.nrOfCallSites(), rtaGraphEAMax.nrOfCallSites());
        System.out.format(" - Virtual call sites            | %9s | %9s | %10s | %10s |\n",
                chaGraph.nrOfVirtualCallSites(), rtaGraph.nrOfVirtualCallSites(), rtaGraphEA.nrOfVirtualCallSites(),
                rtaGraphEAMax.nrOfVirtualCallSites());
        System.out.format("     (of which are monomorphic)  | %9s | %9s | %10s | %10s |\n",
                chaGraph.nrOfMonomorphicCallSites(), rtaGraph.nrOfMonomorphicCallSites(),
                rtaGraphEA.nrOfMonomorphicCallSites(), rtaGraphEAMax.nrOfMonomorphicCallSites());
        System.out.format("     (of which are empty)        | %9s | %9s | %10s | %10s |\n",
                chaGraph.nrOfVirtualEmptyCallSites(), rtaGraph.nrOfVirtualEmptyCallSites(),
                rtaGraphEA.nrOfVirtualEmptyCallSites(), rtaGraphEAMax.nrOfVirtualEmptyCallSites());
        System.out.format(" - Static call sites             | %9s | %9s | %10s | %10s |\n",
                chaGraph.nrOfStaticCallSites(), rtaGraph.nrOfStaticCallSites(), rtaGraphEA.nrOfStaticCallSites(),
                rtaGraphEAMax.nrOfStaticCallSites());
        System.out.println("------------------------------------------------------------------------------------");
    }
}