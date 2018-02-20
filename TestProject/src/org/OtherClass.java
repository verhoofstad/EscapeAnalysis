package org;

public class OtherClass {

	public static int count = 5;
	

	public void callMethods() {
		
		
		this.printNrOfArguments();
		
		this.printNrOfArguments(1);
		
		this.printNrOfArguments(1, 2, 3);
		
	}
	
	
	public void printNrOfArguments(int... args) {
		
		System.out.println(args.length);
		
	}
	
	
	@Override
	public String toString() {
		return "Other class" + count;
	}
	
}
