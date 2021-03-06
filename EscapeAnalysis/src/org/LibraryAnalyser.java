package org;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.asm.JarFile;
import org.asm.JarFileSet;
import org.asm.classHierarchyBuilding.ClassHierachyBuilder;
import org.callGraphs.CallGraph;
import org.callGraphs.cha.ClassHierarchyAnalysis;
import org.callGraphs.rta.RapidTypeAnalysis;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;
import org.classHierarchy.counting.ClassAndMethodCounter;
import org.classHierarchy.counting.CountResults;
import org.classHierarchy.entryPoints.ProjectEntryPointCollector;
import org.classHierarchy.methodFinding.PackagePrivateClassMethodCollector;
import org.dataSets.Library;
import org.results.LibraryResult;
import org.results.reif.ReifLibraryResult;
import org.escapeAnalysis.EscapeAnalysis;

public class LibraryAnalyser {
    
    private boolean includeEscapeAnalysis = true;

    private ProjectEntryPointCollector entryPointCollector;
    
    private JavaTypeSet jdkPackagePrivateClasses;
    private JavaTypeSet jdkConfinedClasses;
    
    public LibraryAnalyser(ProjectEntryPointCollector entryPointCollector) {
        this.entryPointCollector = entryPointCollector;
    }

    public void setJDKResults(JavaTypeSet jdkPackagePrivateClasses, JavaTypeSet jdkConfinedClasses) {
        this.jdkPackagePrivateClasses = jdkPackagePrivateClasses;
        this.jdkConfinedClasses = jdkConfinedClasses;
    }

    public LibraryResult analyse(Library library) {

        JarFileSet jarFiles = library.jarFiles();
        JarFile cpFile = library.cpFile();
        LibraryResult libraryResult = new LibraryResult(library);
        long startTime = 0;

        System.out.format("PROCESSING: %s with %s | %s | %s\n", library.id(), library.organisation(), library.name(), library.revision());
        System.out.println();
        System.out.format("CPFILE: %s\n", cpFile.toString());
        System.out.format("LIB FILES: %s\n", jarFiles.size());
        
        System.out.println();

        System.out.print("Building class hierarchy...");
        Timer.start("classHierarchy");
        ClassHierachyBuilder builder = new ClassHierachyBuilder();
        jarFiles.accept(builder);
        ClassHierarchy classHierarchy = builder.classHierarchy();
        Timer.stop("classHierarchy");
        libraryResult.classHierarchyBuildTime = Timer.get("classHierarchy");
        System.out.println("Ok");
        
        System.out.print("Resolving applies-to sets...");
        classHierarchy.resolveAppliesToSets();
        System.out.println("Ok");

        System.out.print("Counting classes and methods...");
        ClassAndMethodCounter libraryCounter = new ClassAndMethodCounter(cpFile);
        classHierarchy.accept(libraryCounter);
        CountResults libraryCounts = libraryCounter.countResults();
        System.out.println("Ok");
        
        System.out.print("Find entry methods...");
        JavaMethodSet entryPoints = this.entryPointCollector.collectEntryPointsFrom(classHierarchy);
        System.out.println("Ok");
        
        System.out.print("Performing Class Hierarchy Analysis...");
        Timer.start("chaAnalysis");
        ClassHierarchyAnalysis cha = new ClassHierarchyAnalysis(classHierarchy, entryPoints);
        CallGraph chaGraph = cha.computeCallGraph();
        Timer.stop("chaAnalysis");
        libraryResult.chaBuildTime = Timer.get("chaAnalysis");
        System.out.println("Ok");
        
        System.out.print("Performing Rapid Type Analysis...");
        Timer.start("rtaAnalysis");
        RapidTypeAnalysis rta = new RapidTypeAnalysis(chaGraph);
        rta.setLibraryAnalysis(classHierarchy.getPublicClasses(), entryPoints);
        CallGraph rtaGraph = rta.buildGraph();
        Timer.stop("rtaAnalysis");
        libraryResult.rtaBuildTime = Timer.get("rtaAnalysis");
        System.out.println("Ok");

        JavaTypeSet packagePrivateClasses = classHierarchy.getFinalPackagePrivateClasses();
        JavaTypeSet confinedClasses = new JavaTypeSet();
        CallGraph rtaGraphEA = new CallGraph();
        int libraryConfinedClassCount = 0;

        System.out.format("There are %s final package-private classes.\n", packagePrivateClasses.size());

        if (this.includeEscapeAnalysis) {

            // Since we already determined the confined classes of the JDK,
            // we do not need to analyze them again.
            packagePrivateClasses = packagePrivateClasses.difference(this.jdkPackagePrivateClasses);

            if (packagePrivateClasses.size() > 0) {

                System.out.print("Find the methods in which package-private classes are instantiated...");
                PackagePrivateClassMethodCollector methodFinder = new PackagePrivateClassMethodCollector(packagePrivateClasses);
                JavaMethodSet foundMethods = methodFinder.findMethodsIn(classHierarchy);
                System.out.println("Ok");
                System.out.format("Total of %s methods found.\n", foundMethods.size());
                
                if (foundMethods.size() > 0) {

                    EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());

                    try {
                        startTime = System.nanoTime();
                        escapeAnalysis.analyse(foundMethods, jarFiles);
                        libraryResult.escapeAnalysisTime = (System.nanoTime() - startTime);
                        
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
                            if(finalPackagePrivateClass.isLoadedFrom(cpFile)) {
                                libraryFinalPackagePrivateClasses.add(finalPackagePrivateClass);
                            }
                        }
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
            startTime = System.nanoTime();
            RapidTypeAnalysis rtaEA = new RapidTypeAnalysis(chaGraph);
            rtaEA.setLibraryAnalysis(classHierarchy.getPublicClasses(), entryPoints);
            rtaEA.setConfinedClasses(confinedClasses);
            rtaGraphEA = rtaEA.buildGraph();
            libraryResult.rtaEaTime = (System.nanoTime() - startTime);
            System.out.println("Ok");
        }

        System.out.print("Performing Rapid Type Analysis with Escape Analysis (best-case)...");
        startTime = System.nanoTime();
        RapidTypeAnalysis rtaEAMax = new RapidTypeAnalysis(chaGraph);
        rtaEAMax.setLibraryAnalysis(classHierarchy.getPublicClasses(), entryPoints);
        rtaEAMax.setConfinedClasses(classHierarchy.getFinalPackagePrivateClasses());
        CallGraph rtaGraphEAMax = rtaEAMax.buildGraph();
        libraryResult.rtaMaxTime = (System.nanoTime() - startTime);
        System.out.println("Ok");

        System.out.print("Determining dead methods...");
        JavaMethodSet rtaDeadMethods = rtaGraph.getDeadMethods(classHierarchy, entryPoints, cpFile);
        JavaMethodSet rtaEaDeadMethods = rtaGraphEA.getDeadMethods(classHierarchy, entryPoints, cpFile);
        JavaMethodSet rtaMaxDeadMethods = rtaGraphEAMax.getDeadMethods(classHierarchy, entryPoints, cpFile);
        System.out.println("Ok");        
        printGraphTotals(chaGraph, rtaGraph, rtaGraphEA, rtaGraphEAMax);
        System.out.println();

        libraryResult.libraryPublicClassCount = libraryCounts.project_publicClassCount;
        libraryResult.libraryPackagePrivateClassCount = libraryCounts.project_packageVisibleClassCount;
        libraryResult.libraryConfinedClassCount = libraryConfinedClassCount;
        libraryResult.libraryConcreteMethodCount = classHierarchy.getConcreteMethods(cpFile).size();
        libraryResult.libraryEntryPointMethodCount = entryPoints.size();
        libraryResult.libraryCompilerMethodCount = classHierarchy.getCompilerGeneratedMethods(cpFile).size();
        
        libraryResult.libraryPackagePrivateClassInheritFromObjectCount = libraryCounts.project_packageVisibleClassInheritFromJavaLangObject;
        libraryResult.libraryPackagePrivateClassInheritFromOtherCount = libraryCounts.project_packageVisibleClassInheritFromOther;
        libraryResult.libraryPackagePrivateClassOverridingObjectMethodCount = libraryCounts.project_packageVisibleClassOverrideObjectMethods;
        libraryResult.libraryPackagePrivateClassOverridingMethodCount = libraryCounts.project_packageVisibleClassOverrideNonObjectMethods;
        
        libraryResult.rtaEdgeCount = rtaGraph.nrOfEdges(cpFile);
        libraryResult.rtaEaEdgeCount = rtaGraphEA.nrOfEdges(cpFile);
        libraryResult.rtaMaxEdgeCount = rtaGraphEAMax.nrOfEdges(cpFile);
        
        libraryResult.rtaCallSiteCount = rtaGraph.nrOfCallSites(cpFile);
        libraryResult.rtaVirtualCallSiteCount = rtaGraph.nrOfVirtualCallSites(cpFile);
        libraryResult.rtaMonomorphicCallSiteCount = rtaGraph.nrOfMonomorphicCallSites(cpFile);
        libraryResult.rtaEaCallSiteCount = rtaGraphEA.nrOfCallSites(cpFile);
        libraryResult.rtaEaVirtualCallSiteCount = rtaGraphEA.nrOfVirtualCallSites(cpFile);
        libraryResult.rtaEaMonomorphicCallSiteCount = rtaGraphEA.nrOfMonomorphicCallSites(cpFile);
        libraryResult.rtaEaNewMonomorphicCallSiteCount = rtaGraphEA.nrOfNewMonomorphicCallSites(cpFile);
        libraryResult.rtaMaxCallSiteCount = rtaGraphEAMax.nrOfCallSites(cpFile);
        libraryResult.rtaMaxVirtualCallSiteCount = rtaGraphEAMax.nrOfVirtualCallSites(cpFile);
        libraryResult.rtaMaxMonomorphicCallSiteCount = rtaGraphEAMax.nrOfMonomorphicCallSites(cpFile);
        libraryResult.rtaMaxNewMonomorphicCallSiteCount = rtaGraphEAMax.nrOfNewMonomorphicCallSites(cpFile);
        
        libraryResult.rtaDeadMethods = rtaDeadMethods.size();
        libraryResult.rtaEaDeadMethods = rtaEaDeadMethods.size();
        libraryResult.rtaMaxDeadMethods = rtaMaxDeadMethods.size();

        // Print dead methods to file
        List<String> content = new ArrayList<String>();
        
        content.add("Dead methods in " + library.name() + "\r\n");
        for(JavaMethod deadMethod : rtaMaxDeadMethods) {
            content.add(deadMethod.modifiers() + " " + deadMethod.toString());
        }
        
        File resultsFile = new File(Environment.rootFolder + "deadMethods_" + library.id() + ".txt");
        try {
            Files.write(resultsFile.toPath(), content, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static void printTotals(org.asm.counting.CountResults countResult, ReifLibraryResult result2) {

        printLine("Public class count", countResult.all_publicClassCount, result2.all_publicClassCount);
        printLine("Package-private class count", countResult.all_packageVisibleClassCount, result2.all_packageVisibleClassCount);
        printLine("Total class count", countResult.all_classCount, result2.all_classCount);
        System.out.println();
        printLine("Public interface count", countResult.all_publicInterfaceCount, result2.all_publicInterfaceCount);
        printLine("Package-private interface count", countResult.all_packageVisibleInterfaceCount, result2.all_packageVisibleInterfaceCount);
        printLine("Total interface count", countResult.all_interfaceCount, result2.all_interfaceCount);
        System.out.println();
        printLine("Public method count", countResult.all_publicMethods, result2.all_publicMethods);
        printLine("Protected method count", countResult.all_protectedMethods, result2.all_protectedMethods);
        printLine("Package-private method count", countResult.all_packagePrivateMethods, result2.all_packagePrivateMethods);
        printLine("Private method count", countResult.all_privateMethods, result2.all_privateMethods);
        printLine("Total method count", countResult.all_methodCount, result2.all_methodCount);
        System.out.println();
    }
    
    private static void printTotals(CountResults countResult, ReifLibraryResult result2) {

        printLine("Public class count", countResult.all_publicClassCount, result2.all_publicClassCount);
        printLine("Package-private class count", countResult.all_packageVisibleClassCount, result2.all_packageVisibleClassCount);
        printLine("Total class count", countResult.all_classCount, result2.all_classCount);
        System.out.println();
        printLine("Public interface count", countResult.all_publicInterfaceCount, result2.all_publicInterfaceCount);
        printLine("Package-private interface count", countResult.all_packageVisibleInterfaceCount, result2.all_packageVisibleInterfaceCount);
        printLine("Total interface count", countResult.all_interfaceCount, result2.all_interfaceCount);
        System.out.println();
        printLine("Public method count", countResult.all_publicMethods, result2.all_publicMethods);
        printLine("Protected method count", countResult.all_protectedMethods, result2.all_protectedMethods);
        printLine("Package-private method count", countResult.all_packagePrivateMethods, result2.all_packagePrivateMethods);
        printLine("Private method count", countResult.all_privateMethods, result2.all_privateMethods);
        printLine("Total method count", countResult.all_methodCount, result2.all_methodCount);
        System.out.println();
    }
    
    private static void printLine(String description, int result1, int result2) {
        
        System.out.format("%-38s : %9s  %9s", description, result1, result2);
        if(result1 != result2) {
            System.out.format("%9s", result1 - result2);
        }
        System.out.println();
    }
}