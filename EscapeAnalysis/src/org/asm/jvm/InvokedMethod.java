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
    
    public String declaredType() {
        return this.declaredType;
    }
    
    public MethodSignature signature() {
        return this.signature;
    }

    public boolean withNew() {
        return this.withNew;
    }
    
    public boolean isConstructorCall() {
        return this.invocationType.equals(InvocationType.CONSTRUCTOR);
    }
}
