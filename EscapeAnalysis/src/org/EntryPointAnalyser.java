package org;

import org.asm.JarFile;
import org.asm.JarFileSet;
import org.asm.classHierarchyBuilding.ClassHierachyBuilder;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.counting.ClassAndMethodCounter;
import org.classHierarchy.counting.CountResults;
import org.classHierarchy.entryPoints.CPAEntryPointCollector;
import org.classHierarchy.entryPoints.OPAEntryPointCollector;
import org.classHierarchy.entryPoints.OldEntryPointCollector;
import org.dataSets.Library;
import org.results.LibraryResult;
import org.results.reif.ReifLibraryResult;

public class EntryPointAnalyser {

    private Library library;
    private ReifLibraryResult reifResult;

    public EntryPointAnalyser(Library library, ReifLibraryResult reifresult) {
        this.library = library;
        this.reifResult = reifresult;
    }

    public void analyse() {

        JarFileSet jarFiles = this.library.jarFiles();
        JarFile cpFile = this.library.cpFile();
        LibraryResult libraryResult = new LibraryResult(this.library);
        long startTime = 0;

        System.out.format("PROCESSING: %s with %s | %s | %s\n", library.id(), library.organisation(), library.name(), library.revision());
        System.out.println();
        System.out.format("CPFILE: %s\n", cpFile.toString());
        System.out.format("JAR FILES: %s\n", jarFiles.size());
        System.out.println();

        System.out.print("Building class hierarchy...");
        startTime = System.nanoTime(); 
        ClassHierachyBuilder builder = new ClassHierachyBuilder();
        jarFiles.accept(builder);
        ClassHierarchy classHierarchy = builder.classHierarchy();
        libraryResult.classHierarchyBuildTime = (System.nanoTime() - startTime);
        System.out.println("Ok");

        System.out.print("Counting classes and methods...");
        ClassAndMethodCounter counter = new ClassAndMethodCounter(cpFile);
        classHierarchy.accept(counter);
        CountResults libraryCounts = counter.countResults();
        System.out.println("Ok");
        
        
        System.out.print("Find types with factory method...");
        classHierarchy.loadFactoryMethods();
        System.out.println("Ok");
        //System.out.format("Found %s types with factory methods.\n", typesWithFactoryMethods.size());
        
        System.out.print("Find entry points for library...");
        OldEntryPointCollector oldEntryPointCollector = new OldEntryPointCollector(cpFile);
        JavaMethodSet libraryEntryPointsOld = oldEntryPointCollector.collectEntryPointsFrom(classHierarchy);
        OPAEntryPointCollector opaEntryPointCollector = new OPAEntryPointCollector(cpFile);
        JavaMethodSet libraryEntryPointsOpa = opaEntryPointCollector.collectEntryPointsFrom(classHierarchy);
        CPAEntryPointCollector cpaEntryPointCollector = new CPAEntryPointCollector(cpFile);
        JavaMethodSet libraryEntryPointsCpa = cpaEntryPointCollector.collectEntryPointsFrom(classHierarchy);
        System.out.println("Ok");

               
        printTotals(libraryCounts, this.reifResult);
        printLine("Old entry points", libraryEntryPointsOld.size(), this.reifResult.old_entryPoints);
        printLine("OPA entry points", libraryEntryPointsOpa.size(), this.reifResult.opa_entryPoints);
        printLine("CPA entry points", libraryEntryPointsCpa.size(), this.reifResult.cpa_entryPoints);
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