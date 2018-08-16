package org;

import org.asm.JarFileSet;
import org.asm.classHierarchyBuilding.ClassHierachyBuilder;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.JavaTypeSet;
import org.classHierarchy.methodFinding.PackagePrivateClassMethodCollector;
import org.escapeAnalysis.EscapeAnalysis;
import org.results.JDKResults;

public class JDKAnalyser {

    private JarFileSet jdkFiles;

    public JDKAnalyser(String jdkFolder) {
        this.jdkFiles = new JarFileSet(jdkFolder);
    }

    public JDKResults analyseJDK() {

        System.out.println("Pre-analyse the JDK library separately...");

        System.out.print("Building class hierarchy...");
        ClassHierachyBuilder builder = new ClassHierachyBuilder();
        jdkFiles.accept(builder);
        ClassHierarchy classHierarchy = builder.classHierarchy();
        System.out.println("Ok");

        JavaTypeSet jdkPackagePrivateClasses = classHierarchy.getFinalPackagePrivateClasses();

        System.out.print("Find the methods in which package-private classes are instantiated...");
        PackagePrivateClassMethodCollector methodFinder = new PackagePrivateClassMethodCollector(jdkPackagePrivateClasses);
        classHierarchy.accept(methodFinder);
        System.out.println("Ok");
        System.out.format("Total of %s methods found.\n", methodFinder.foundMethods().size());

        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());

        escapeAnalysis.analyse(methodFinder.foundMethods(), jdkFiles);

        JavaTypeSet confinedClasses = jdkPackagePrivateClasses.difference(escapeAnalysis.escapingClasses());

        System.out.format("Final package-private classes count: %s\n", jdkPackagePrivateClasses.size());
        System.out.format("Confined classes count:              %s\n", confinedClasses.size());
        System.out.println();
        
        return new JDKResults(jdkPackagePrivateClasses, confinedClasses);
    }
}