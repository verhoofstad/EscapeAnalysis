package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaType;

/**
 * Collects the exported methods as defined in Rapid Type Analysis.
 */
public class ExportedMethodCollector extends ProjectEntryPointCollector {
    
    public ExportedMethodCollector(JarFile projectFile) {
        super(projectFile);
    }

    @Override
    public void visitProjectMethod(JavaMethod javaMethod) {
        
        JavaType declType = javaMethod.containedIn();
        boolean isEntryPoint = !javaMethod.isAbstract() && declType.isPublic()
            && (javaMethod.isPublic() || (javaMethod.isProtected() && !declType.isFinal()));
        
        if(isEntryPoint) {
            this.addEntryPoint(javaMethod);
        }
    }
}
