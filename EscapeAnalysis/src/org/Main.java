package org;

import java.util.ArrayList;
import java.util.List;

import org.asm.JarFileSet;
import org.dataSets.DataSet;
import org.dataSets.Library;
import org.results.JDKResults;
import org.results.LibraryResult;
import org.results.LibraryResultSet;
import org.validation.LibraryValidator;

public class Main {

    public static void main(String[] args) {
        
        DataSet dataSet = DataSet.getCorrectSet();
        //DataSet dataSet = DataSet.getTestSet();
        
        //validateLibraries(dataSet);
        analyseLibraries(dataSet);
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
        
        libResults.printCallEdgeTable();
        libResults.printMonomorphicCallSitesTable();
        libResults.printDeadMethodsTable();
        libResults.printLatexTable4();
        
        System.out.println("Finished");
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
