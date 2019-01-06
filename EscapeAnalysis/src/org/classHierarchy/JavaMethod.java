package org.classHierarchy;

import java.util.List;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;
import org.asm.jvm.InvokedMethod;
import org.asm.jvm.MethodSignature;
import org.objectweb.asm.Type;

/**
 * Represents a Java method in a specific class or interface.
 */
public class JavaMethod {

    private String id;
    private String name;
    private AccessFlags accessFlags;
    private MethodSignature signature;

    private JavaType containedIn;
    private JavaMethodSet overridenBy;
    private JavaMethod overrides;
    private List<InvokedMethod> invokedMethods;

    // The method's applies-to set
    private JavaTypeSet appliesTo;

    public JavaMethod(JavaType containedIn, int access, MethodSignature signature) {

        if (containedIn == null) { throw new IllegalArgumentException("Parameter 'containedIn' should not be null."); }
        if (signature == null) { throw new IllegalArgumentException("Parameter 'signature' should not be null."); }

        this.name = signature.name();
        this.accessFlags = new AccessFlags(access);
        this.signature = signature;

        this.containedIn = containedIn;
        this.overridenBy = new JavaMethodSet();
        this.overrides = null;

        this.id = containedIn.id() + "/" + this.signature.toString();
    }

    public JavaMethod(JavaType containedIn, int access, MethodSignature signature, List<InvokedMethod> invokedMethods) {
        this(containedIn, access, signature);
        
        this.invokedMethods = invokedMethods;
    }
    
    /**
     * Gets the fully qualified name that uniquely identifies this method.
     */
    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public MethodSignature signature() {
        return this.signature;
    }

    public JavaType containedIn() {
        return this.containedIn;
    }
    
    public List<InvokedMethod> invokedMethods() {
        return this.invokedMethods;
    }

    /**
     * Returns the set of methods that directly overrides this method (non-transitive).
     */
    public JavaMethodSet overridenBy() {
        return this.overridenBy;
    }
    
    /**
     * Returns the method that is overridden by this method -or- null if this method doen not override any method.
     */
    public JavaMethod overrides() {
        return this.overrides;
    }
    
    /**
     * Returns a value indicating whether this is a public method.
     */
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
    
    public boolean isSynthetic() {
        return this.accessFlags.isSynthetic();
    }
    
    public boolean isNative() {
        return this.accessFlags.isNative();
    }
    
    /**
     * Gets the JAR-file this method was loaded from.
     */
    public JarFile jarFile() {
        return this.containedIn.jarFile();
    }

    /**
     * Determines whether this method is part of a give Java type.
     */
    public boolean containedIn(JavaType javaType) {
        return this.containedIn.equals(javaType);
    }

    /**
     * Determines whether this method was loaded from a specified JAR-file.
     */
    public boolean isLoadedFrom(JarFile jarFile) {
        return this.containedIn.isLoadedFrom(jarFile);
    }
    
    void accept(ClassHierarchyVisitor visitor) {
        if(this.isPublic()) {
            visitor.visitPublicMethod(this);
        }
        else if(this.isProtected()) {
            visitor.visitProtectedMethod(this);
        }
        else if(this.isPrivate()) {
            visitor.visitPrivateMethod(this);
        }
        else {
            visitor.visitPackagePrivateMethod(this);
        }
    }
    
    void accept(ConcreteMethodVisitor visitor) {
        if(!this.isAbstract()) {
            visitor.visitConcreteMethod(this);
        }
    }

    /**
     * Resolves the applies-to set for this method. This method should only be
     * called when the entire class hierarchy is constructed.
     */
    void resolveAppliestoSet() {

        // Initialize the applies-to set with the cone set of the class or interface
        // it's declared in.
        JavaTypeSet appliesTo = new JavaTypeSet(this.containedIn().coneSet());

        // Subtract the applies-to set with the cone sets of the methods that directly
        // override this method.
        for (JavaMethod overridingMethod : this.overridenBy) {
            appliesTo = appliesTo.difference(overridingMethod.containedIn().coneSet());
        }
        this.appliesTo = appliesTo;
    }

    /**
     * Returns the method's applies-to set as defined for CHA.
     */
    public JavaTypeSet appliesTo() {
        return this.appliesTo;
    }

    /**
     * Adds a method that directly overrides this method.
     */
    public void overridenBy(JavaMethod overridingMethod) {
        this.overridenBy.add(overridingMethod);
    }
    
    /**
     * Set the concrete method this method overrides.
     */
    public void overrides(JavaMethod baseMethod) {
        if(this.overrides == null) {
            this.overrides = baseMethod;
        } else {
            throw new IllegalStateException("The base method can only be set once.");
        }
    }

    @Override
    public int hashCode() {
        return this.id().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JavaMethod)) {
            return false;
        } else {
            return this.id.equals(((JavaMethod) obj).id());
        }
    }

    public boolean signatureEquals(String name, String desc) {
        return this.signature.equals(name, desc);
    }
    
    public String referenceReturnType() {
        
        Type returnType = this.signature.returnType();

        switch(returnType.getSort()) {
        case Type.OBJECT:
            return returnType.getInternalName();
        case Type.ARRAY:
            if(returnType.getElementType().getSort() == Type.OBJECT) {
                return returnType.getElementType().getInternalName();
            }
        }
        return null;
    }
    
    public boolean hasReferenceReturnType() {
        return this.referenceReturnType() != null;
    }

    @Override
    public String toString() {
        return String.format("%s/%s", this.containedIn.toString(), this.signature.toString());
    }
    
    public String modifiers() {
        return this.accessFlags.toModifierString();
    }
}
