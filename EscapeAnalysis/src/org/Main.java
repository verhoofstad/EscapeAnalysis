package org;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.asm.JarFileSet;
import org.dataSets.DataSet;
import org.dataSets.Library;
import org.dataSets.LibraryResult;
import org.dataSets.LibraryResultSet;
import org.validation.LibraryValidator;

public class Main {

    public static void main(String[] args) {

        DataSet dataSet = DataSet.getCorrectSet();

        //validateLibraries(dataSet);
        analyseLibraries(dataSet);
    }

    private static void analyseLibraries(DataSet dataSet) {

        // Because the JDK is a dependency of every other library,
        // we analyze it one time separately so we can re-use the results.
        JDKAnalyser jdkAnalyser = new JDKAnalyser(Environment.jdkFolder);
        jdkAnalyser.analyseJDK();

        LibraryResultSet libResults = readResultFile();

        for (Library library : dataSet) {
            LibraryResult result = libResults.find(library);
            LibraryAnalyser analyser = new LibraryAnalyser(library, result);
            analyser.setJDKResults(jdkAnalyser.jdkPackagePrivateClasses(), jdkAnalyser.jdkConfinedClasses());

            analyser.analyse();
        }
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
            System.out.format("CPFILE: %s\n", library.cpFile().getAbsolutePath());
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

    public static LibraryResultSet readResultFile() {

        String csvFile = Environment.rootFolder + "results.txt";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\t";

        LibraryResultSet libResults = new LibraryResultSet();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] results = line.split(cvsSplitBy);

                if (!results[0].equals("organisation")) {
                    libResults.add(new LibraryResult(results));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return libResults;
    }
}
