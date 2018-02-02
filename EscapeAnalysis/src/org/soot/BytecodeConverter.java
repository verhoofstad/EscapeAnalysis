package org.soot;

import java.io.IOException;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.asm.JarFileList;
import org.tree.JavaClass;
import org.tree.JavaClassList;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class BytecodeConverter {

	
	public void test(JavaClass classHierarchy, JarFileList jarFiles) {
	
		Options.v().set_soot_classpath(jarFiles.getClassPath());

		Scene scene = Scene.v();


		JavaClassList allClasses = classHierarchy.getClasses();

		System.out.format("Classes in class list: %s\n", allClasses.size());
		
		for(JavaClass currentClass : allClasses) {
			
			if(currentClass.methodCount() > 0) {

				System.out.format("Loading: %s  -  %s\n", currentClass.name(), currentClass.sootName());
				
				SootClass sootClass = scene.loadClassAndSupport(currentClass.sootName());
				
				//System.out.format("Has outer class: %s\n", sootClass.hasOuterClass() ? "yes" : "no");
				
				Collection<SootMethod> methods = sootClass.getMethods();
				
				for(SootMethod method : methods) {

					UnitGraph graph = new ExceptionalUnitGraph(method.getActiveBody());
					
					
					//System.out.format("Method: %s\n", method.getName());
				}
			}
		}
	}
}
