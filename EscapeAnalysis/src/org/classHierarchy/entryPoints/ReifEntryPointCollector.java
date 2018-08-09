package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.asm.jvm.MethodDescriptor;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaType;

abstract class ReifEntryPointCollector extends ProjectEntryPointCollector {

    protected ReifEntryPointCollector(JarFile projectFile) {
        super(projectFile);
    }
    
    protected boolean maybeCalledByTheJVM(JavaMethod javaMethod) {
        return javaMethod.name().equals("finalize") || this.isPotentiallySerializationRelated(javaMethod);
    }
    
    protected boolean typeIsInstantiable(JavaType javaType) {       
        return javaType.isClass() && classIsInstantiable((JavaClass)javaType);
    }
    
    protected boolean classIsInstantiable(JavaClass javaClass) {

        boolean hasFactoryMethod = false;
        
        for(JavaMethod declaredMethod : javaClass.declaredMethods()) {
            if(declaredMethod.isFactoryMethod() && declaredMethod.referenceReturnType() != null) {
                
                if(javaClass.coneSet().contains(declaredMethod.referenceReturnType())) {
                    hasFactoryMethod = true;
                }
            }
        }
        return (javaClass.hasNonPrivateConstructor() || hasFactoryMethod);
    }

    
    /**
     * 
     * From: OPAL\ai\src\main\scala\org\opalj\ai\analyses\cg\CallGraphFactory
     */
    protected boolean isPotentiallySerializationRelated(JavaMethod javaMethod) {
        
        boolean nonFinal = !javaMethod.containedIn().isFinal(); /*we may inherit from Serializable later on...*/
        boolean isExternalizable = javaMethod.containedIn().isSubTypeOf("java/io/Externalizable");
        boolean isSerializable = javaMethod.containedIn().isSubTypeOf("java/io/Serializable");

        boolean isInheritedBySerializableOnlyClass = !isExternalizable && (isSerializable || nonFinal);
        boolean isInheritedByExternalizableClass = isExternalizable || nonFinal;
        
        return (javaMethod.signatureEquals("<init>", MethodDescriptor.noArgsAndReturnVoid) ||
            javaMethod.signatureEquals("readObjectNoData", MethodDescriptor.noArgsAndReturnVoid) ||
            javaMethod.signatureEquals("readResolve", MethodDescriptor.justReturnsObject) ||
            javaMethod.signatureEquals("writeReplace", MethodDescriptor.justReturnsObject) ||
            ((
                javaMethod.signatureEquals("readObject", MethodDescriptor.readObjectDescriptor) ||
                javaMethod.signatureEquals("writeObject", MethodDescriptor.writeObjectDescriptor)
            ) && isInheritedBySerializableOnlyClass) ||
                (
                    javaMethod.isPublic() /*we are implementing an interface...*/ &&
                    (
                        javaMethod.signatureEquals("readExternal", MethodDescriptor.readObjectInputDescriptor) ||
                        javaMethod.signatureEquals("writeExternal", MethodDescriptor.writeObjectOutputDescriptor)
                    ) &&
                        isInheritedByExternalizableClass
                )
            );
    }
}
