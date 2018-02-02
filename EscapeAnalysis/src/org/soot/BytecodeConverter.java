package org.soot;

import java.io.IOException;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

public class BytecodeConverter {

	
	public void test() {
	
		//JarFile file = new java.util.jar.JarFile()
		
		Options.v().set_soot_classpath("C:\\CallGraphData\\JavaJDK\\java-8-openjdk-amd64\\jre\\lib\\rt.jar");
		
		
	
		Scene scene = Scene.v();
		
		SootClass sootClass = scene.loadClassAndSupport("java.net.CookiePolicy");
		
		
		System.out.format("Has outer class: %s\n", sootClass.hasOuterClass() ? "yes" : "no");
		
		Collection<SootMethod> methods = sootClass.getMethods();
		
		for(SootMethod method : methods) {
			
			
			System.out.format("Method: %s\n", method.getName());
		}
		
	}
	
}
