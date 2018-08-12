package org;

public class SubInsClass extends NewInsClass {

    public SubInsClass(int value) {
        super(value, "Hello");
    }
    
    public static void factory() 
    {
        SubInsClass cls1 = new SubInsClass(4);
        
        SubInsClass[] array1 = new SubInsClass[2]; 
    }
}
