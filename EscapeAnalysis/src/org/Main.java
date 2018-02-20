package org;

import org.dataSets.DataSet;
import org.dataSets.Library;

public class Main {

	public static void main(String[] args) {
	
		//DataSet dataSet = DataSet.getDevelopmentSet();
		DataSet dataSet = DataSet.getCompleteSet();
		
		for(Library library : dataSet) {
			
			LibraryAnalyser analyser = new LibraryAnalyser(library);
			
			analyser.analyse();
		}
	}
}
