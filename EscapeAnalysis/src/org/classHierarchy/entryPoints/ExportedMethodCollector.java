package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.classHierarchy.tree.JavaMethod;

/**
 * Gets the exported methods for RTA which are contained in a given library project (JAR-file).
 * Abstract methods are excluded since they do not affect the call graph.
 * //We also filter out synthetic methods since those 'should' not be called directly. --> We assume a Closed Package scenario.
 */
public class ExportedMethodCollector extends ProjectEntryPointCollector {
    
    public ExportedMethodCollector(JarFile projectFile) {
        super(projectFile);
    }

    @Override
    public void visitProjectMethod(JavaMethod javaMethod) {
        
        boolean isEntryPoint = !javaMethod.isAbstract() && javaMethod.containedIn().isPublic()
            && (javaMethod.isPublic() || (javaMethod.isProtected() && !javaMethod.containedIn().isFinal()));
        
        if(isEntryPoint) {
            this.addEntryPoint(javaMethod);
        }
    }
}
