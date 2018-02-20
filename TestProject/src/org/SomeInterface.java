package org;

public interface SomeInterface {

	
	public void printIf();
	
	
	public default void printIt( ) {
		System.out.println("Hallo");
	}
	
}
