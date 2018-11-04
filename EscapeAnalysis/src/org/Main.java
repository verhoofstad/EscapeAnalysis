package org;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.asm.JarFileSet;
import org.asm.validation.LibraryValidator;
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
        
        long startTime = System.nanoTime(); 
        
        //validateLibraries(programSet);
        analysePrograms(programSet);
        //validateLibraries(dataSet);
        //analyseLibraries(dataSet);
        //compareCounts(DataSet.getUnmodifiedSet());
        //analyseEntryPoints(dataSet);
        
        long runningTime = (System.nanoTime() - startTime);
        DecimalFormat formatter = new DecimalFormat("#.00");
        System.out.println("Total running time: " + formatter.format((double)runningTime / 1000 / 1000 / 1000) + " seconds");
    }
    
    private static void analysePrograms(DataSet dataSet) {

        // Because the JDK is a dependency of every other library,
        // we analyze it one time separately so we can re-use the results.
        JDKAnalyser jdkAnalyser = new JDKAnalyser(Environment.jdkFolder);
        JDKResults jdkResults = jdkAnalyser.analyseJDK();

        LibraryResultSet programResults = new LibraryResultSet();

        for (Library program : dataSet) {
            
            long startTime = System.nanoTime();

            LibraryAnalyser analyser = new LibraryAnalyser(program);
            analyser.setJDKResults(jdkResults.finalPackagePrivateClasses(), jdkResults.confinedClasses());

            LibraryResult programResult = analyser.analyse();
            programResult.totalAnalysisTime = (System.nanoTime() - startTime);
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
            
            long startTime = System.nanoTime();

            LibraryAnalyser analyser = new LibraryAnalyser(library);
            analyser.setJDKResults(jdkResults.finalPackagePrivateClasses(), jdkResults.confinedClasses());

            LibraryResult libraryResult = analyser.analyse();
            libraryResult.totalAnalysisTime = (System.nanoTime() - startTime);
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