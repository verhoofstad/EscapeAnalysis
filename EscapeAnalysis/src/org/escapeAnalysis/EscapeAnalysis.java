package org.escapeAnalysis;

import java.util.HashSet;
import java.util.Set;

import org.asm.JarFileSet;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;
import org.escapeAnalysis.connectionGraph.EscapeState;
import org.escapeAnalysis.connectionGraph.ObjectNode;

import soot.Body;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class EscapeAnalysis {

    private JavaTypeSet allClasses;
    private boolean verbose = false;
    private JavaTypeSet escapingClasses;

    public EscapeAnalysis(JavaTypeSet allClasses) {
        this.allClasses = allClasses;
    }

    public JavaTypeSet escapingClasses() {
        return this.escapingClasses;
    }

    public void analyse(JavaMethodSet methods, JarFileSet jarFiles) {

        this.escapingClasses = new JavaTypeSet();

        soot.G.reset();

        Options.v().set_soot_classpath(jarFiles.getSootClassPath());

        Options.v().set_whole_program(true);
        Options.v().set_verbose(false);
        Options.v().set_keep_line_number(true);
        Options.v().set_app(true);
        // Options.v().set_src_prec(Options.src_prec_class);
        // Options.v().set_prepend_classpath(true);
        // Options.v().set_whole_program(true);

        Options.v().set_process_dir(methods.jarFiles().toSootStringList());
        println("Process-dir: %s", methods.jarFiles().toSootStringList());

        PhaseOptions.v().setPhaseOption("bb", "off");
        // PhaseOptions.v().setPhaseOption("tag.ln", "on");
        // PhaseOptions.v().setPhaseOption("jj.a", "on");
        // PhaseOptions.v().setPhaseOption("jj.ule", "on");

        Scene scene = Scene.v();

        scene.loadNecessaryClasses(); // Triggers Soot exception

        println("Classes in class list: %s", methods.getClasses().size());

        int methodCount = 0;

        Set<String> escapingClasses = new HashSet<String>();
        
        SootFactory sootFactory = new SootFactory();

        for (JavaType currentClass : methods.getClasses()) {
            
            String sootClassName = sootFactory.getSootClassName(currentClass);

            println("Loading: %s from %s", sootClassName, currentClass.jarFile());

            SootClass sootClass = scene.loadClassAndSupport(sootClassName);
            // Make it an application class as it will be analyzed.
            sootClass.setApplicationClass();

            println("Soot class %s loaded.", sootClass.getName());
            println("Soot class has %s methods.", currentClass.declaredMethods().size());

            for (JavaMethod method : methods.getMethodsOfClass(currentClass)) {

                SootMethod sootMethod = sootFactory.getSootMethod(sootClass, method);

                if (sootMethod != null) {

                    Body body = getSootMethodBody(sootMethod);

                    UnitGraph graph = new ExceptionalUnitGraph(body);

                    println("Method: %s.%s", sootClass.getName(), method.name());
                    UnitGraphContainer visitor = new UnitGraphContainer(graph);

                    ConnectionGraphBuilder builder = new ConnectionGraphBuilder();
                    visitor.accept(builder);
                    ConnectionGraph connectionGraph = builder.connectionGraph();
                    connectionGraph.resolveEscapeState();

                    for (ObjectNode objectNode : connectionGraph.getObjects()) {

                        println("   %s   %s", objectNode.id(), objectNode.getEscapeState().toString());

                        if (objectNode.getEscapeState() == EscapeState.ESCAPE) {

                            this.addEscapingClass(objectNode);
                            escapingClasses.add(objectNode.getObjectType().getClassName());
                        }
                    }
                    methodCount++;
                } else {
                    System.out.println("WARNING: Could not find method " + method.toString());
                }
            }
        }

        if (this.verbose) {
            println("Methods processed: %s", methodCount);
            println("Total number of escaping classes: %s", escapingClasses.size());
            println("Finished Escape Analysis");
        }
    }

    private Body getSootMethodBody(SootMethod sootMethod) {
        if (sootMethod.hasActiveBody()) {
            return sootMethod.getActiveBody();
        } else {
            return sootMethod.retrieveActiveBody();
        }
    }

    private void addEscapingClass(ObjectNode objectNode) {

        // Translate the Soot class name back to the ASM class name.
        String internalName = objectNode.getObjectType().getClassName().replace('.', '/');

        if (!this.escapingClasses.contains(internalName)) {

            // The objectNode may also be an interface.
            JavaType escapingClass = this.allClasses.find(internalName);

            if (escapingClass != null) {
                this.escapingClasses.add(escapingClass);
            }
        }
    }

    private void println(String format, Object... args) {
        if (this.verbose) {
            System.out.format(format + "\n", args);
        }
    }
}