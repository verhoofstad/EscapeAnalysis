package org.soot;

import java.util.Dictionary;

import org.asm.JarFileSet;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;

import soot.Body;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class BytecodeConverter {

	
	public void test(JavaMethodSet methods, JarFileSet jarFiles) {
	
		Options.v().set_soot_classpath(jarFiles.getClassPath());

        Options.v().set_verbose(false);
        Options.v().set_keep_line_number(true);
        Options.v().set_app(true);
        //Options.v().set_src_prec(Options.src_prec_class);
        //Options.v().set_prepend_classpath(true);
        //Options.v().set_whole_program(true);

    	Options.v().set_process_dir(methods.jarFiles().toStringList());
        
        PhaseOptions.v().setPhaseOption("bb", "off");
        //PhaseOptions.v().setPhaseOption("tag.ln", "on");
        //PhaseOptions.v().setPhaseOption("jj.a", "on");
        //PhaseOptions.v().setPhaseOption("jj.ule", "on");

		Scene scene = Scene.v();
		
		scene.loadNecessaryClasses();
		
		System.out.format("Classes in class list: %s\n", methods.getClasses().size());
		
		int methodCount = 0;
		int activeBody = 0;
		int noActiveBody = 0;
		
		for(JavaType currentClass : methods.getClasses()) {
			
			System.out.format("Loading: %s from %s\n", currentClass.sootName(), currentClass.jarFile());
			
			SootClass sootClass = scene.loadClassAndSupport(currentClass.sootName());
			// Make it an application class as it will be analyzed.
			sootClass.setApplicationClass();

			
			System.out.format("Soot class %s loaded.\n", sootClass.getName());
			System.out.format("Soot class has %s methods.\n", currentClass.declaredMethods().size());
			
			for(JavaMethod method : methods.getMethodsOfClass(currentClass)) {
				
				SootMethod sootMethod = sootClass.getMethod(method.sootName(), method.sootParameters(), method.sootReturnType());

				if(sootMethod != null) {
					
					if(sootMethod.hasActiveBody()) {
						
						Body body = sootMethod.getActiveBody();
						
						activeBody++;
					} else {
						
						Body body = sootMethod.retrieveActiveBody();
						
						UnitGraph graph = new ExceptionalUnitGraph(body);

						System.out.format("Method: %s\n", method.sootName());
						UnitGraphContainer visitor = new UnitGraphContainer(graph);

						ConnectionGraphBuilder builder = new ConnectionGraphBuilder();
						
						visitor.accept(builder);
						
						
						noActiveBody++;
					}
					
					methodCount++;
				}
				else {
					System.out.println("Could not find method!!!");
				}
			}
		}
		
		System.out.format("Methods processed: %s\n", methodCount);
		System.out.format("Active body: %s\n", activeBody);
		System.out.format("No active body: %s\n", noActiveBody);
		System.out.println("Finished");
	}
}