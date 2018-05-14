package org;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.asm.JarFile;
import org.asm.JarFileSet;
import org.callGraphs.CallGraph;
import org.callGraphs.cha.ClassHierarchyAnalysis;
import org.callGraphs.rta.RapidTypeAnalysis;
import org.classHierarchy.ClassHierachyBuilder;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaTypeSet;
import org.counting.ClassCounter;
import org.counting.CountResults;
import org.dataSets.Library;
import org.dataSets.LibraryResult;
import org.escapeAnalysis.EscapeAnalysis;
import org.methodFinding.JarFileSetMethodFinder;

public class LibraryAnalyser {

    private Library library;
    private LibraryResult result;
    private File resultsFile;

    private boolean buildGraphs = true;
    private boolean includeEscapeAnalysis = true;

    private JavaTypeSet jdkPackagePrivateClasses;
    private JavaTypeSet jdkConfinedClasses;

    public LibraryAnalyser(Library library, LibraryResult result, File resultsFile) {
        this.library = library;
        this.result = result;
        this.resultsFile = resultsFile;
    }

    public void setJDKResults(JavaTypeSet jdkPackagePrivateClasses, JavaTypeSet jdkConfinedClasses) {
        this.jdkPackagePrivateClasses = jdkPackagePrivateClasses;
        this.jdkConfinedClasses = jdkConfinedClasses;
    }

    public void analyse() {

        JarFileSet jarFiles = this.library.jarFiles();
        JarFile cpFile = this.library.cpFile();

        System.out.format("PROCESSING: %s with %s | %s | %s\n", library.id(), library.organisation(), library.name(), library.revision());
        System.out.println();
        System.out.format("CPFILE: %s\n", cpFile.toString());
        System.out.format("JAR FILES: %s\n", jarFiles.size());
        System.out.println();

        System.out.print("Counting classes and methods...");
        ClassCounter classCounter = new ClassCounter();
        jarFiles.accept(classCounter);
        CountResults totalCounts = classCounter.countResults();
        
        ClassCounter libraryCounter = new ClassCounter();
        cpFile.accept(libraryCounter);
        CountResults libraryCounts = libraryCounter.countResults();
        System.out.println("Ok");
        

        System.out.print("Building class hierarchy...");
        ClassHierachyBuilder builder = new ClassHierachyBuilder();
        jarFiles.accept(builder);
        ClassHierarchy classHierarchy = builder.classHierarchy();
        System.out.println("Ok");

        JavaTypeSet packagePrivateClasses = classHierarchy.getFinalPackagePrivateClasses();
        JavaTypeSet confinedClasses = new JavaTypeSet();

        JavaMethodSet entryPoints = classHierarchy.getExportedMethods(this.library.cpFile());
        
        System.out.format("There are %s final package-private classes.\n", packagePrivateClasses.size());
        System.out.format("Number of RTA entry points: %s\n", entryPoints.size());

        if (this.buildGraphs) {

            System.out.print("Performing Class Hierarchy Analysis...");
            ClassHierarchyAnalysis cha = new ClassHierarchyAnalysis(classHierarchy);
            jarFiles.accept(cha);
            CallGraph chaGraph = cha.callGraph();
            System.out.println("Ok");

            System.out.print("Performing Rapid Type Analysis...");
            RapidTypeAnalysis rta = new RapidTypeAnalysis(chaGraph);
            rta.setLibraryAnalysis(classHierarchy.getPublicClasses(), entryPoints);
            rta.analyse();
            CallGraph rtaGraph = rta.callGraph();
            System.out.println("Ok");

            CallGraph rtaGraphEA = new CallGraph();

            if (this.includeEscapeAnalysis) {

                // Since we already determined the confined classes of the JDK,
                // we do not need to analyze them again.
                packagePrivateClasses.difference(this.jdkPackagePrivateClasses);

                if (packagePrivateClasses.size() > 0) {

                    System.out.print("Find the methods in which package-private classes are instantiated...");
                    JarFileSetMethodFinder methodFinder = new JarFileSetMethodFinder(classHierarchy,
                            packagePrivateClasses);
                    jarFiles.accept(methodFinder);
                    System.out.println("Ok");
                    System.out.format("Total of %s methods found.\n", methodFinder.foundMethods().size());

                    if (methodFinder.foundMethods().size() > 0) {

                        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());

                        try {
                            escapeAnalysis.analyse(methodFinder.foundMethods(), jarFiles);

                            confinedClasses.addAll(packagePrivateClasses);
                            confinedClasses.difference(escapeAnalysis.escapingClasses());

                            System.out.format("Final package-private classes count: %s\n", packagePrivateClasses.size());
                            System.out.format("Escaping classes count:              %s\n", escapeAnalysis.escapingClasses().size());
                            System.out.format("Confined classes count:              %s\n", confinedClasses.size());
                            System.out.format("JDK confined classes count:          %s\n", this.jdkConfinedClasses.size());

                            confinedClasses.addAll(this.jdkConfinedClasses);
                            System.out.format("Total confined classes count:        %s\n", confinedClasses.size());

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

            System.out.print("Performing Rapid Type Analysis with Escape Analysis... (best-case)");
            RapidTypeAnalysis rtaEAMax = new RapidTypeAnalysis(chaGraph);
            rtaEAMax.setLibraryAnalysis(classHierarchy.getPublicClasses(), entryPoints);
            rtaEAMax.setConfinedClasses(classHierarchy.getFinalPackagePrivateClasses());
            rtaEAMax.analyse();
            CallGraph rtaGraphEAMax = rtaEAMax.callGraph();
            System.out.println("Ok");

            printGraphTotals(chaGraph, rtaGraph, rtaGraphEA, rtaGraphEAMax);
            printToFile(this.resultsFile, totalCounts, libraryCounts, this.result, entryPoints, confinedClasses, chaGraph, rtaGraph, rtaGraphEA, rtaGraphEAMax);
        }

        System.out.println();
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
                chaGraph.nrOfVirtualMonoCallSites(), rtaGraph.nrOfVirtualMonoCallSites(),
                rtaGraphEA.nrOfVirtualMonoCallSites(), rtaGraphEAMax.nrOfVirtualMonoCallSites());
        System.out.format("     (of which are empty)        | %9s | %9s | %10s | %10s |\n",
                chaGraph.nrOfVirtualEmptyCallSites(), rtaGraph.nrOfVirtualEmptyCallSites(),
                rtaGraphEA.nrOfVirtualEmptyCallSites(), rtaGraphEAMax.nrOfVirtualEmptyCallSites());
        System.out.format(" - Static call sites             | %9s | %9s | %10s | %10s |\n",
                chaGraph.nrOfStaticCallSites(), rtaGraph.nrOfStaticCallSites(), rtaGraphEA.nrOfStaticCallSites(),
                rtaGraphEAMax.nrOfStaticCallSites());
        System.out.println("------------------------------------------------------------------------------------");

    }

    
    private void printToFile(File file, CountResults totalCounts, CountResults libraryCounts, LibraryResult chaCpaResult, JavaMethodSet entryPoints, 
            JavaTypeSet confinedClasses, CallGraph chaGraph, CallGraph rtaGraph, CallGraph rtaGraphEA, CallGraph rtaGraphEAMax) {
        
        List<String> fields = new ArrayList<String>();
        fields.add("" + this.library.id()); 
        fields.add(this.library.organisation());
        fields.add(library.name());
        fields.add(library.revision());
        
        fields.add("" + totalCounts.classCount);
        fields.add("" + totalCounts.packagePrivateClassCount);
        fields.add("" + libraryCounts.classCount);
        fields.add("" + libraryCounts.packagePrivateClassCount);
        
        fields.add("" + confinedClasses.size());

        fields.add("" + entryPoints.size());
        fields.add("" + chaCpaResult.cpa_entryPoints);
        fields.add("" + chaCpaResult.old_entryPoints);

        fields.add("" + chaGraph.nrOfEdges());
        fields.add("" + chaGraph.nrOfCallSites());
        fields.add("" + chaGraph.nrOfVirtualCallSites());
        fields.add("" + chaGraph.nrOfStaticCallSites());

        fields.add("" + rtaGraph.nrOfEdges());
        fields.add("" + rtaGraph.nrOfCallSites());
        fields.add("" + rtaGraph.nrOfVirtualCallSites());
        fields.add("" + rtaGraph.nrOfStaticCallSites());

        fields.add("" + rtaGraphEA.nrOfEdges());
        fields.add("" + rtaGraphEA.nrOfCallSites());
        fields.add("" + rtaGraphEA.nrOfVirtualCallSites());
        fields.add("" + rtaGraphEA.nrOfStaticCallSites());
        fields.add("" + rtaGraphEA.newMonoMorphicCallSites);

        fields.add("" + rtaGraphEAMax.nrOfEdges());
        fields.add("" + rtaGraphEAMax.nrOfCallSites());
        fields.add("" + rtaGraphEAMax.nrOfVirtualCallSites());
        fields.add("" + rtaGraphEAMax.nrOfStaticCallSites());
        fields.add("" + rtaGraphEAMax.newMonoMorphicCallSites);

        List<String> line = new ArrayList<String>();
        line.add(String.join(";", fields));
        
        try {
            Files.write(file.toPath(), line, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
