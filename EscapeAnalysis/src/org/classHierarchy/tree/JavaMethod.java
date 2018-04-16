package org.classHierarchy.tree;

import java.util.ArrayList;
import java.util.List;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;
import org.objectweb.asm.Type;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.VoidType;
import soot.ShortType;

public class JavaMethod {

	private String id;
	private AccessFlags accessFlags;
	private String name;
	private String desc;
	private String signature;
	
	private Type methodType;
		
	private JavaType containedIn;
	private JavaMethodSet overridenBy;

	// The method's applies-to set
	private JavaTypeSet appliesTo;
	
	public JavaMethod(JavaType containedIn, int access, String name, String desc) {
		
		if(containedIn == null) {throw new Error("Argument null");}
		
		this.accessFlags = new AccessFlags(access);
		this.name = name;
		this.desc = desc;
		this.signature = toSignature(name, desc);
	
		this.containedIn = containedIn;
		this.methodType = Type.getMethodType(desc);
		
		this.overridenBy = new JavaMethodSet();
		
		this.id = createId();
	}
	
	/*
	 * Gets the fully qualified name that uniquely identifies this method.
	 */
	public String id() {
		return this.id;
	}
	
	public String name() {
		return this.name;
	}
	
	public String desc() {
		return this.desc;
	}
	
	public String signature() {
		return this.signature;
	}
	
	public JavaType containedIn() {
		return this.containedIn;
	}
	
	public JavaMethodSet overridenBy() {
		return this.overridenBy;
	}
	
	public boolean isPublic() {
		return this.accessFlags.isPublic();
	}
	
	public boolean isProtected() {
		return this.accessFlags.isProtected();
	}
	
	public boolean isPrivate() {
		return this.accessFlags.isPrivate();
	}
	
	public boolean isAbstract() {
		return this.accessFlags.isAbstract();
	}
	
	public boolean isStatic() {
		return this.accessFlags.isStatic();
	}
	
	public boolean isConstructor() {
		return this.name.equals("<init>");
	}
	
	public boolean isStaticInitializer() {
		return this.name.equals("<clinit>");
	}
		
	/*
	 * Gets the JAR-file this method was loaded from.
	 */
	public JarFile jarFile() {
		return this.containedIn.jarFile();
	}
	
	/*
	 * Resolves the applies-to set for this method.
	 */
	void resolveAppliestoSet() {
		
		// Initialize the applies-to set with the cone set of the class or interface it's declared in.
		this.appliesTo = new JavaTypeSet(this.containedIn().coneSet());
		
		for(JavaMethod overridingMethod : this.overridenBy) {
			this.appliesTo.difference(overridingMethod.containedIn().coneSet());
		}
	}
	
	/*
	 * Returns the method's applies-to set as defined for CHA.
	 */
	public JavaTypeSet appliesTo() {
		return this.appliesTo;
	}
	
	public void overridenBy(JavaMethod overridingMethod) {
		this.overridenBy.add(overridingMethod);
	}
	
	
	/*
	 * 
	 */
	public boolean isClientCallable() {
		
		boolean hasPublicSubClassThatInheritsMethod = false;
		for(JavaType subType : this.appliesTo) {
			if(subType instanceof JavaClass && subType.isPublic()) {
				hasPublicSubClassThatInheritsMethod = true;
				break;
			}
		}
		
		return (this.isPublic() || this.isProtected()) &&
			(this.containedIn.isPublic() || hasPublicSubClassThatInheritsMethod);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof JavaMethod)) {
			return false;
		} else {
			return this.id.equals(((JavaMethod)obj).id());
		}
	}
	
	/*
	 * Determines whether a given method has the same signature (i.e. name, arguments and return type) as the current instance.
	 */
	public boolean signatureEquals(JavaMethod other) {
		
		if(other == null) { return false; }
		
		return this.signature.equals(other.signature());
	}

	public boolean signatureEquals(String name, String desc) {
		return this.name.equals(name) && this.desc.equals(desc);
	}
	
	public String sootName() {
		return this.name;
	}
	
	public List<soot.Type> sootParameters() {
		List<soot.Type> parameterTypes = new ArrayList<soot.Type>();
		
		for(Type argumentType : this.methodType.getArgumentTypes()) {
			parameterTypes.add(toSootType(argumentType));
		}
		
		return parameterTypes;
	}
	
	public soot.Type sootReturnType() {
		return toSootType(this.methodType.getReturnType());
	}
	
	private soot.Type toSootType(org.objectweb.asm.Type asmType) {
		switch(asmType.getSort()) {
		case Type.VOID:
			return VoidType.v();
		case Type.BOOLEAN:
			return BooleanType.v();
		case Type.CHAR:
			return CharType.v();
		case Type.BYTE:
			return ByteType.v();
		case Type.SHORT:
			return ShortType.v();
		case Type.INT:
			return IntType.v();
		case Type.FLOAT:
			return FloatType.v();
		case Type.LONG:
			return LongType.v();
		case Type.DOUBLE:
			return DoubleType.v();
		case Type.ARRAY:
			try {
				return ArrayType.v(toSootType(asmType.getElementType()), asmType.getDimensions());
			} catch(NullPointerException ex) {
				System.out.format("Null pointer exception. Method: %s", this.name);
			}
		case Type.OBJECT:
			return RefType.v(asmType.getClassName());
		case Type.METHOD:
			System.out.println("Unexpected type. [method]");
			return null;
		}
		System.out.format("Nothing found: %s\n", asmType.getSort());
		return null;
	}
	
	
	
	@Override
	public String toString() {
		
		List<String> arguments = new ArrayList<String>();
		for(Type argumentType : this.methodType.getArgumentTypes()) {
			arguments.add(argumentToString(argumentType));
		}		
		
		return String.format("%s/%s(%s)", this.containedIn.name(), this.name, String.join(",", arguments));
	}
	
	public static String toSignature(String name, String desc) {
		return String.format("%s::%s", name, desc);
	}
	
	public static String toName(String owner, String name, String desc) {
		List<String> arguments = new ArrayList<String>();
		for(Type argumentType : Type.getMethodType(desc).getArgumentTypes()) {
			arguments.add(argumentToString(argumentType));
		}		
		
		return String.format("%s/%s(%s)", owner, name, String.join(",", arguments));
	}
	
	private String createId() {
		List<String> arguments = new ArrayList<String>();
		for(Type argumentType : this.methodType.getArgumentTypes()) {
			arguments.add(argumentToString(argumentType));
		}		
		return String.format("%s/%s(%s):(%s)", this.containedIn.name(), this.name, 
			String.join(",", arguments), argumentToString(this.methodType.getReturnType()));
	}
	
	private static String argumentToString(org.objectweb.asm.Type argumentType) {
		switch(argumentType.getSort()) {
		case Type.BOOLEAN:
		case Type.CHAR:
		case Type.BYTE:
		case Type.SHORT:
		case Type.INT:
		case Type.FLOAT:
		case Type.LONG:
		case Type.DOUBLE:
			return argumentType.getClassName();
		case Type.ARRAY:
			try {
				return argumentToString(argumentType.getElementType()) 
						+ new String(new char[argumentType.getDimensions()]).replace("\0", "[]");
			} catch(NullPointerException ex) {
				System.out.format("Null pointer exception.");
			}
		case Type.OBJECT:
			return argumentType.getInternalName();
		case Type.VOID:
			return "V";
		default:
			throw new Error("Unexpected argument type for method.");
		}
	}
}
