package org;

public class SomeClass {

	private T privatePropterty;
	
	
	public String noEscape() 
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Hello");
		sb.append(" ");
		sb.append("World!");
		
		return sb.toString();
	}
	
	public T returnEscape() 
	{
		T objectEscapes = new T();
		
		return objectEscapes;
	}
	
	public T indirectReturnEscape() 
	{
		T objectEscapes = new T();

		U objectEscapesNot = new U();
		
		objectEscapesNot.f = objectEscapes;
		
		return objectEscapesNot.f;
	}
	
	public void noParameterEscape(T a, T b, T c) {
		
		T objectEscapesNot = new T();

		a = objectEscapesNot;
	}

	public void parameterEscape(T a, T b, T c) {
		
		T objectEscapes = new T();

		a.f = objectEscapes;
	}
	
	public void indirectParameterEscape(T a) {
		
		T objectEscapes = new T();
		a.f.f.f = objectEscapes;
	}

    public void paperExample(T a) {
        a.b.c.d = new T();
    }
	
	public void thisEscape() {
		
		T objectEscapes = new T();
		
		this.privatePropterty = objectEscapes;
	}
	
	public void methodEscape() {
		
		T objectEscapes = new T();
		
		parameterEscape(objectEscapes, objectEscapes, null);
	}
	
	
	public void indirectMethodEscape() {
		
		T objectEscapes = new T();

		T objectEscapesNot = new T();
		
		objectEscapesNot.f = objectEscapes;

		parameterEscape(objectEscapesNot.f, null, null);
	}
}
