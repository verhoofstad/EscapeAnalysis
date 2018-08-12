package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;

/**
 * Finds the methods that are entry points under the open-package assumption.
 *
 */
public class OPAEntryPointCollector extends ReifEntryPointCollector {

    public OPAEntryPointCollector(JarFile projectFile, JavaMethodSet factoryMethods) {
        super(projectFile, factoryMethods);
    }

    @Override
    public void visitProjectMethod(JavaMethod javaMethod) {
        if(this.isEntryPoint(javaMethod)) {
            this.addEntryPoint(javaMethod);
        }
    }
    
    private boolean isEntryPoint(JavaMethod javaMethod) {
        JavaType declType = javaMethod.containedIn();

        return this.maybeCalledByTheJVM(javaMethod) ||
            javaMethod.isStaticInitializer() ||
            (!javaMethod.isPrivate() &&
                (javaMethod.isStatic() || typeIsInstantiable(declType)));
    }
}
