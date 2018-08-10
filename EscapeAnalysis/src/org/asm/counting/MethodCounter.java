package org.asm.counting;

import org.asm.jvm.AccessFlags;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodCounter extends ClassVisitor {

    private CountResults result;
    private boolean isProjectMethod;
    private boolean isInterfaceMethod;

    public MethodCounter(CountResults result, boolean isProjectMethod, boolean isInterfaceMethod) {
        super(Opcodes.ASM6);

        this.result = result;
        this.isProjectMethod = isProjectMethod;
        this.isInterfaceMethod = isInterfaceMethod;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        AccessFlags accessFlags = new AccessFlags(access);

        this.result.all_methodCount++;

        if (accessFlags.isPublic()) {
            this.result.all_publicMethods++;
        } else if (accessFlags.isProtected()) {
            this.result.all_protectedMethods++;
        } else if (accessFlags.isPackagePrivate()) {
            this.result.all_packagePrivateMethods++;
        } else if (accessFlags.isPrivate()) {
            this.result.all_privateMethods++;
        } else {
            throw new Error();
        }
        
        if(this.isProjectMethod) {
            this.result.project_methodCount++;
            
            if (accessFlags.isPublic()) {
                this.result.project_publicMethods++;
                
                if(accessFlags.isAbstract()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_publicAbstractMethods++;
                    } else {
                        this.result.project_cls_publicAbstractMethods++;
                    }
                }
                if(accessFlags.isStatic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_publicStaticMethods++;
                    } else {
                        this.result.project_cls_publicStaticMethods++;
                    }
                }
                if(!accessFlags.isAbstract() && !accessFlags.isStatic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_publicInstanceMethods++;
                    } else {
                        this.result.project_cls_publicInstanceMethods++;
                    }
                }
                if(name.equals("<clinit>")) {
                    if(this.isInterfaceMethod) {
                        this.result.project_int_publicInitializerMethods++;
                    } else {
                        this.result.project_cls_publicInitializerMethods++;
                    }
                }
                if(accessFlags.isSynthetic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_publicSyntheticMethods++;
                    } else {
                        this.result.project_cls_publicSyntheticMethods++;
                    }
                }
                if(accessFlags.isBridge()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_publicBridgeMethods++;
                    } else {
                        this.result.project_cls_publicBridgeMethods++;
                    }
                }
                
            } else if (accessFlags.isProtected()) {
                this.result.project_protectedMethods++;
                if(accessFlags.isAbstract()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_protectedAbstractMethods++;
                    } else {
                        this.result.project_cls_protectedAbstractMethods++;
                    }
                }
                if(accessFlags.isStatic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_protectedStaticMethods++;
                    } else {
                        this.result.project_cls_protectedStaticMethods++;
                    }
                }
                if(!accessFlags.isAbstract() && !accessFlags.isStatic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_protectedInstanceMethods++;
                    } else {
                        this.result.project_cls_protectedInstanceMethods++;
                    }
                }
                if(name.equals("<clinit>")) {
                    if(this.isInterfaceMethod) {
                        this.result.project_int_protectedInitializerMethods++;
                    } else {
                        this.result.project_cls_protectedInitializerMethods++;
                    }
                }
                if(accessFlags.isSynthetic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_protectedSyntheticMethods++;
                    } else {
                        this.result.project_cls_protectedSyntheticMethods++;
                    }
                }
                if(accessFlags.isBridge()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_protectedBridgeMethods++;
                    } else {
                        this.result.project_cls_protectedBridgeMethods++;
                    }
                }

            } else if (accessFlags.isPackagePrivate()) {
                this.result.project_packagePrivateMethods++;
                if(accessFlags.isAbstract()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_packagePrivateAbstractMethods++;
                    } else {
                        this.result.project_cls_packagePrivateAbstractMethods++;
                    }
                }
                if(accessFlags.isStatic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_packagePrivateStaticMethods++;
                    } else {
                        this.result.project_cls_packagePrivateStaticMethods++;
                    }
                }
                if(!accessFlags.isAbstract() && !accessFlags.isStatic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_packagePrivateInstanceMethods++;
                    } else {
                        this.result.project_cls_packagePrivateInstanceMethods++;
                    }
                }
                if(name.equals("<clinit>")) {
                    if(this.isInterfaceMethod) {
                        this.result.project_int_packagePrivateInitializerMethods++;
                    } else {
                        this.result.project_cls_packagePrivateInitializerMethods++;
                    }
                }
                if(accessFlags.isSynthetic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_packagePrivateSyntheticMethods++;
                    } else {
                        this.result.project_cls_packagePrivateSyntheticMethods++;
                    }
                }
                if(accessFlags.isBridge()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_packagePrivateBridgeMethods++;
                    } else {
                        this.result.project_cls_packagePrivateBridgeMethods++;
                    }
                }

            } else if (accessFlags.isPrivate()) {
                this.result.project_privateMethods++;
                if(accessFlags.isAbstract()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_privateAbstractMethods++;
                    } else {
                        this.result.project_cls_privateAbstractMethods++;
                    }
                }
                if(accessFlags.isStatic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_privateStaticMethods++;
                    } else {
                        this.result.project_cls_privateStaticMethods++;
                    }
                }
                if(!accessFlags.isAbstract() && !accessFlags.isStatic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_privateInstanceMethods++;
                    } else {
                        this.result.project_cls_privateInstanceMethods++;
                    }
                }
                if(name.equals("<clinit>")) {
                    if(this.isInterfaceMethod) {
                        this.result.project_int_privateInitializerMethods++;
                    } else {
                        this.result.project_cls_privateInitializerMethods++;
                    }
                }
                if(accessFlags.isSynthetic()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_privateSyntheticMethods++;
                    } else {
                        this.result.project_cls_privateSyntheticMethods++;
                    }
                }
                if(accessFlags.isBridge()) {
                    if (this.isInterfaceMethod) {
                        this.result.project_int_privateBridgeMethods++;
                    } else {
                        this.result.project_cls_privateBridgeMethods++;
                    }
                }

            } else {
                throw new Error();
            }
            
            /**
             * Returns a list of all entry points that is well suited if we want to
             * analyze a library/framework.
             *
             * The set of all entry points consists of:
             *  - all static initializers,
             *  - every non-private static method,
             *  - every non-private constructor,
             *  - every non-private method,
             *  - every private method (including a default constructor) related to
             *    Serialization, even if the respective declaring class is not a current subtype
             *    of java.io.Serializable but maybe a subtype later on.
             *  - every private method that has an annotation that indicates that the method is
             *    implicitly called (e.g., using Java Reflection.)
             * Unless, one of the following conditions is met:
             *  - the method is an instance method, but
             *    the class cannot be instantiated (all constructors are private and no
             *    factory methods are provided) [we currently ignore self-calls using
             *    reflection; this is – however – very unlikely.]
             */
            
            if(!accessFlags.isAbstract()) {
                if(name.equals("<clinit>") ||
                    (!accessFlags.isPrivate() && accessFlags.isStatic()) ||
                    (!accessFlags.isPrivate() && name.equals("<init>")) ||
                    (!accessFlags.isPrivate() && !accessFlags.isSynthetic())
                    ) {
                    this.result.old_entryPoints++;
                }
            }
        } else {
            this.result.libraries_methodCount++;

            if (accessFlags.isPublic()) {
                this.result.libraries_publicMethods++;
            } else if (accessFlags.isProtected()) {
                this.result.libraries_protectedMethods++;
            } else if (accessFlags.isPackagePrivate()) {
                this.result.libraries_packagePrivateMethods++;
            } else if (accessFlags.isPrivate()) {
                this.result.libraries_privateMethods++;
            } else {
                throw new Error();
            }            
        }
        return null;
    }
}
