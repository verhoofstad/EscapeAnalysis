package org;

public class Main {

	public static void main(String[] args)
	{
		T ta = new T();
		ta.text = "Hello";
		
		printIt(ta);
		
		System.out.println(ta.text);
	}
	
	public static void printIt(T obj) {
		T tb = new T();
		tb.text = "World";
			
		obj = tb;
	}
}
