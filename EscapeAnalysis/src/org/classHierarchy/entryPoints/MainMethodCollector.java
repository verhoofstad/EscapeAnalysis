package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.asm.jvm.MethodDescriptor;
import org.classHierarchy.JavaMethod;

/**
 * Finds the main method(s) in a given project.
 */
public class MainMethodCollector extends ProjectEntryPointCollector {

    public MainMethodCollector(JarFile projectFile) {
        super(projectFile);
    }

    @Override
    public boolean isEntryPoint(JavaMethod javaMethod) {
        return javaMethod.isPublic() && javaMethod.isStatic() 
                && javaMethod.signatureEquals("main", MethodDescriptor.stringArrayArgAndReturnVoid);
    }
}