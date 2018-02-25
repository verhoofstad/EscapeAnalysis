package org.dataSets;

import java.util.ArrayList;
import java.util.List;

public class LibraryResultSet {

	List<LibraryResult> libraryResults = new ArrayList<LibraryResult>();
	
	public void add(LibraryResult libraryResult) {
		this.libraryResults.add(libraryResult);
	}
	
	public LibraryResult find(Library library) {
		
		for(LibraryResult libraryResult : this.libraryResults) {
			
			if(libraryResult.name.equals(library.name()) 
				&& libraryResult.organisation.equals(library.organisation())
				&& libraryResult.revision.equals(library.revision())) {
				
				return libraryResult;
			}
		}
		return null;
	}
	
}
