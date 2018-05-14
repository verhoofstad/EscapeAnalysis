package org;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
        File resultsFile = createResultsFile();

        for (Library library : dataSet) {
            LibraryResult result = libResults.find(library);
            LibraryAnalyser analyser = new LibraryAnalyser(library, result, resultsFile);
            analyser.setJDKResults(jdkAnalyser.jdkPackagePrivateClasses(), jdkAnalyser.jdkConfinedClasses());

            analyser.analyse();
        }
        System.out.println("Finished");
    }

    private static File createResultsFile() {
        
        List<String> headers = new ArrayList<String>();
        headers.add("id");
        headers.add("organisation");
        headers.add("name");
        headers.add("revision");
        
        headers.add("all_classCount");
        headers.add("all_packageVisibleClassCount");
        headers.add("libraries_classCount");
        headers.add("libraries_packageVisibleClassCount");
        
        headers.add("confinedClassCount");

        headers.add("rta_entryPoints");
        headers.add("chaCpa_entryPoints");
        headers.add("old_entryPoints");

        headers.add("cha_edgeCount");
        headers.add("cha_callSiteCount");
        headers.add("cha_virtualCallSiteCount");
        headers.add("cha_staticCallSiteCount");

        headers.add("rta_edgeCount");
        headers.add("rta_callSiteCount");
        headers.add("rta_virtualCallSiteCount");
        headers.add("rta_staticCallSiteCount");

        headers.add("rtaEA_edgeCount");
        headers.add("rtaEA_callSiteCount");
        headers.add("rtaEA_virtualCallSiteCount");
        headers.add("rtaEA_staticCallSiteCount");
        headers.add("rtaEA_newMonoMorphicCallSiteCount");

        headers.add("rtaEAMax_edgeCount");
        headers.add("rtaEAMax_callSiteCount");
        headers.add("rtaEAMax_virtualCallSiteCount");
        headers.add("rtaEAMax_staticCallSiteCount");
        headers.add("rtaEAMax_newMonoMorphicCallSiteCount");

        List<String> header = new ArrayList<String>();
        header.add(String.join(";", headers));

        File resultsFile = new File(Environment.resultFile);
        try {
            Files.write(resultsFile.toPath(), header, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return resultsFile;
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

    private static LibraryResultSet readResultFile() {

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
