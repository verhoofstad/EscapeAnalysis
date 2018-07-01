package org.results.reif;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.Environment;
import org.dataSets.Library;

public class ReifLibraryResultSet {

    List<ReifLibraryResult> libraryResults = new ArrayList<ReifLibraryResult>();

    public void add(ReifLibraryResult libraryResult) {
        this.libraryResults.add(libraryResult);
    }

    public ReifLibraryResult find(Library library) {

        for (ReifLibraryResult libraryResult : this.libraryResults) {

            if (libraryResult.name.equals(library.name()) && libraryResult.organisation.equals(library.organisation())
                    && libraryResult.revision.equals(library.revision())) {

                return libraryResult;
            }
        }
        return null;
    }

    public static ReifLibraryResultSet readFromFile() {

        String csvFile = Environment.rootFolder + "results.txt";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\t";

        ReifLibraryResultSet libResults = new ReifLibraryResultSet();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] results = line.split(cvsSplitBy);

                if (!results[0].equals("organisation")) {
                    libResults.add(new ReifLibraryResult(results));
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