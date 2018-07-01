package org.results;

import org.classHierarchy.tree.JavaTypeSet;

public class JDKResults {
    
    private JavaTypeSet finalPackagePrivateClasses;
    private JavaTypeSet confinedClasses;
    
    public JDKResults(JavaTypeSet finalPackagePrivateClasses, JavaTypeSet confinedClasses) {
        
        this.finalPackagePrivateClasses = finalPackagePrivateClasses;
        this.confinedClasses = confinedClasses;
    }
    
    public JavaTypeSet finalPackagePrivateClasses() {
        return this.finalPackagePrivateClasses;
    }

    public JavaTypeSet confinedClasses() {
        return this.confinedClasses;
    }
}