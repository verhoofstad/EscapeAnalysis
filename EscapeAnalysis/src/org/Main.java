package org;

import java.util.ArrayList;
import java.util.List;

import org.asm.JarFileSet;
import org.asm.validation.LibraryValidator;
import org.classHierarchy.entryPoints.ExportedMethodCollector;
import org.classHierarchy.entryPoints.MainMethodCollector;
import org.dataSets.DataSet;
import org.dataSets.Library;
import org.results.JDKResults;
import org.results.LibraryResult;
import org.results.LibraryResultSet;
import org.results.reif.ReifLibraryResult;
import org.results.reif.ReifLibraryResultSet;

public class Main {

    public static void main(String[] args) {
        
        //DataSet dataSet = DataSet.getCorrectSet();
        //DataSet dataSet = DataSet.getTestSet();
        //DataSet dataSet = DataSet.getUnmodifiedSet();
        
        DataSet programSet = DataSet.getApplicationSet();
        
        Timer.start("completeAnalysis");
        
        //validateLibraries(programSet);
        //analysePrograms(programSet);
        validateLibraries(programSet);
        //analyseLibraries(dataSet);
        //compareCounts(DataSet.getUnmodifiedSet());
        //analyseEntryPoints(dataSet);
        
        Timer.stop("completeAnalysis");
        Timer.print("completeAnalysis");
    }
    
    private static void analysePrograms(DataSet dataSet) {

        // Because the JDK is a dependency of every other program,
        // we analyze it one time separately so we can re-use the results.
        JDKAnalyser jdkAnalyser = new JDKAnalyser(Environment.jdkFolder);
        JDKResults jdkResults = jdkAnalyser.analyseJDK();

        LibraryResultSet programResults = new LibraryResultSet();

        for (Library program : dataSet) {
            
            Timer.start("programAnalysis");

            LibraryAnalyser analyser = new LibraryAnalyser(new MainMethodCollector(program.cpFile()));
            analyser.setJDKResults(jdkResults.finalPackagePrivateClasses(), jdkResults.confinedClasses());

            LibraryResult programResult = analyser.analyse(program);
            
            Timer.stop("programAnalysis");
            programResult.totalAnalysisTime = Timer.get("programAnalysis");
            programResults.add(programResult);
        }
        
        programResults.printCallEdgeTable("progCallEdges");
        programResults.printMonomorphicCallSitesTable("progMonomorphicCallSites");
        programResults.printDeadMethodsTable("progDeadMethods");
        programResults.printEntryPointTable("progEntryPoints");
        programResults.printPackagePrivateClassDistribution("progClassDistribution");
    }
    
    private static void analyseLibraries(DataSet dataSet) {

        // Because the JDK is a dependency of every other library,
        // we analyze it one time separately so we can re-use the results.
        JDKAnalyser jdkAnalyser = new JDKAnalyser(Environment.jdkFolder);
        JDKResults jdkResults = jdkAnalyser.analyseJDK();

        LibraryResultSet libResults = new LibraryResultSet();

        for (Library library : dataSet) {
            
            Timer.start("libraryAnalysis");
            
            LibraryAnalyser analyser = new LibraryAnalyser(new ExportedMethodCollector(library.cpFile()));
            analyser.setJDKResults(jdkResults.finalPackagePrivateClasses(), jdkResults.confinedClasses());

            LibraryResult libraryResult = analyser.analyse(library);
            
            Timer.stop("libraryAnalysis");
            
            libraryResult.totalAnalysisTime = Timer.get("libraryAnalysis");
            libResults.add(libraryResult);
        }
        
        libResults.printCallEdgeTable("libCallEdges");
        libResults.printDeadMethodsTable("libDeadMethods");
        libResults.printEntryPointTable("libEntryPoints");
    }

    private static void analyseEntryPoints(DataSet dataSet) {

        ReifLibraryResultSet reifResults = ReifLibraryResultSet.readFromFile();

        for (Library library : dataSet) {
            
            ReifLibraryResult reifResult = reifResults.find(library);

            EntryPointAnalyser analyser = new EntryPointAnalyser(library, reifResult);

            analyser.analyse();
        }
    }

    /**
     * Validates that all programs/libraries in a given dataSet are complete and have no missing dependencies. 
     * i.e. all classes and interfaces that are extended, implemented, instantiated, invoked, etc. are 
     * present in the program/library.
     */
    private static void validateLibraries(DataSet dataSet) {

        List<String> validLibraries = new ArrayList<String>();

        for (Library library : dataSet) {

            LibraryValidator validator = new LibraryValidator();
            JarFileSet jarFiles = library.jarFiles();

            System.out.format("PROCESSING: %s with %s | %s | %s\n", library.id(), library.organisation(),
                    library.name(), library.revision());
            System.out.println();
            System.out.format("CPFILE: %s\n", library.cpFile().toString());
            System.out.format("JAR FILES: %s\n", library.jarFiles().size());
            System.out.println();

            jarFiles.accept(validator);
            if (validator.isValid()) {
                validLibraries.add("" + library.id());
            }
        }
        System.out.format("Finished. %s out of %s libraries are complete.\n", validLibraries.size(), dataSet.size());
        System.out.format("Identifiers: %s\n\n", String.join(",", validLibraries));
    }
}