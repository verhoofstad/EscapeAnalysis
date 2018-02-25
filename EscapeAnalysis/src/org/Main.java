package org;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.dataSets.DataSet;
import org.dataSets.Library;
import org.dataSets.LibraryResult;
import org.dataSets.LibraryResultSet;

public class Main {

	public static void main(String[] args) {
	
		//DataSet dataSet = DataSet.getDevelopmentSet();
		DataSet dataSet = DataSet.getCorrectSet();
		
		//LocalDateTime start = LocalDateTime.now();
		
		LibraryResultSet libResults = readResultFile();
		
		for(Library library : dataSet) {
			
			LibraryResult result = libResults.find(library);
			LibraryAnalyser analyser = new LibraryAnalyser(library, result);
			
			analyser.analyse();
		}
		System.out.println("Finished");
	}
	
	
	public static LibraryResultSet readResultFile() {

        String csvFile = "C:\\CallGraphData\\results.txt";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\t";

        LibraryResultSet libResults = new LibraryResultSet();
        
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] results = line.split(cvsSplitBy);
                
                if(!results[0].equals("organisation")) {
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
