package org;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.asm.ConvertOpcode;
import org.asm.JarFile;
import org.connectionGraph.EscapeJarVisitor;
import org.instructionCounting.JarInstructionCounter;
import org.io.JarFinder;
import org.methodFinding.JarFileMethodFinder;
import org.packagePrivateClasses.JavaClassList;
import org.packagePrivateClasses.PackagePrivateClassCollector;
import org.soot.BytecodeConverter;

public class Main {

	
	public static void main(String[] args) {
	
		
		BytecodeConverter conv = new BytecodeConverter();
		
		conv.test();
	
		
	}
		
	public static void asmAnalysis() {
		
		System.out.println("Escape Analysis v1.0");
	
        int publicClassCount = 0;
        int packagePrivateClassCount = 0;
        int publicEnumCount = 0;
        int packagePrivateEnumCount = 0;
        int publicInterfaceCount = 0;
        int packagePrivateInterfaceCount = 0;
		
		String path1 = "C:\\CallGraphData\\JavaJDK\\java-8-openjdk-amd64";
        String path2 = "C:\\CallGraphData\\TestProject.jar";
        String path3 = "C:\\CallGraphData\\PaperProject.jar";
		String path4 = "C:\\CallGraphData\\JavaJDK\\java-8-openjdk-amd64\\jre\\lib\\rt.jar";
        
        String path = path4;
        
        // Initialize the list of package-private classes.
        JavaClassList packagePrivateClasses = new JavaClassList();
        
        /*
        System.out.print("Find package-private classes...");
		for(File jarFilePath : new JarFinder(path)) {

			JarFile jarFile;
			try {

				jarFile = new JarFile(jarFilePath);
			
				PackagePrivateClassCollector classCollector = new PackagePrivateClassCollector();
				
				jarFile.accept(classCollector);
		
				packagePrivateClasses.addAll(classCollector.getClassList());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Ok");
		packagePrivateClassCount += packagePrivateClasses.size();
		*/
		
        Map<Integer, Integer> opcodeCount = new HashMap<Integer, Integer>();
        Map<String, Integer> patternCount = new HashMap<String, Integer>();
        
        System.out.print("Find method in which they are instantiated...\n");
		for(File jarFilePath : new JarFinder(path)) {

			JarFile jarFile;
			try {

				jarFile = new JarFile(jarFilePath);
			
				System.out.println("Visit JAR: " + jarFilePath);
				
				EscapeJarVisitor methodFinder = new EscapeJarVisitor(jarFilePath, patternCount);
				jarFile.accept(methodFinder);

				//JarInstructionCounter instructionCounter = new JarInstructionCounter(opcodeCount);
				//jarFile.accept(instructionCounter);
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Ok");		

		TreeMap<Integer, Integer> sortedOpcodeCount = new TreeMap<Integer, Integer>(opcodeCount);
		int i = 1;
		
		for(Map.Entry<Integer, Integer> entry : sortedOpcodeCount.entrySet()) 
		{
			System.out.format("Opcode %s:   %s (%s)\n", ConvertOpcode.toString(entry.getKey()), entry.getValue(), i);
			i++;
		}
		
		
		System.out.format("Patterns: different: %s\n", patternCount.keySet().size());
		int totalCount = 0;
		for(int count : patternCount.values()) {
			totalCount += count;
		}
		System.out.format("Patterns: total:     %s\n", totalCount);
		
		System.out.format("%s methods found.\n", packagePrivateClasses.methodCount());
		/*
		System.out.format("Public class count              : %s\n", publicClassCount);
		System.out.format("Package-private class count     : %s\n", packagePrivateClassCount);
		System.out.format("Public enum count               : %s\n", publicEnumCount);
		System.out.format("Package-private enum count      : %s\n", packagePrivateEnumCount);
		System.out.format("Public interface count          : %s\n", publicInterfaceCount);
		System.out.format("Package-private interface count : %s\n", packagePrivateInterfaceCount);
		
		System.out.format("Total class count               : %s\n", ClassBrowser.totalClasses);
		*/
		
        System.out.println();
		
	}
}
