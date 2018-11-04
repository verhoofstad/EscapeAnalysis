package org.asm.jvm;

public class InvokedMethod {

    private InvocationType invocationType;
    private String declaredType;
    private MethodSignature signature;
    private boolean withNew;
    
    public InvokedMethod(InvocationType invocationType, String declaredType, MethodSignature signature, boolean withNew) {
        this.invocationType = invocationType;
        this.declaredType = declaredType;
        this.signature = signature;
        this.withNew = withNew;
    }
    
    public InvocationType invocationType() {
        return this.invocationType;
    }
    
    public String declaredType() {
        return this.declaredType;
    }
    
    public MethodSignature signature() {
        return this.signature;
    }

    /**
     * Returns a value indicating whether the constructor was called directly with the 'new' keyword (i.e. it was not a call from a constructor to a parent constructor).
     */
    public boolean withNew() {
        return this.withNew;
    }
    
    public boolean isConstructorCall() {
        return this.invocationType.equals(InvocationType.CONSTRUCTOR);
    }
}