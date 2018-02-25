package org.dataSets;

public class LibraryResult {

	public String organisation;
	public String name;
	public String revision;
	public int dependencies; 
	public int all_classFileCount;
	public int all_classCount;
	public int all_interfaceCount;
	public int all_publicClassCount;
	public int all_packageVisibleClassCount;	
	public int all_publicInterfaceCount;
	public int all_packageVisibleInterfaceCount;
	public int project_classFileCount; 
	public int project_classCount;
	public int project_interfaceCount;
	public int project_publicClassCount;
	public int project_packageVisibleClassCount;
	public int project_publicInterfaceCount;
	public int project_packageVisibleInterfaceCount;
	public int libraries_classFileCount;
	public int libraries_classCount;
	public int libraries_interfaceCount;
	public int libraries_publicClassCount;
	public int libraries_packageVisibleClassCount;
	public int libraries_publicInterfaceCount;
	public int libraries_packageVisibleInterfaceCount;
	public int all_methodCount;
	public int all_publicMethods;
	public int all_protectedMethods;
	public int all_packagePrivateMethods;
	public int all_privateMethods;
	public int project_methodCount;
	public int project_publicMethods;
	public int project_protectedMethods;
	public int project_packagePrivateMethods;
	public int project_privateMethods;
	public int libraries_methodCount;
	public int libraries_publicMethods;
	public int libraries_protectedMethods;
	public int libraries_packagePrivateMethods;
	public int libraries_privateMethods;
	public int old_entryPoints;
	public int old_callEdgesCount;
	public double old_EntryPointCalculationTime;
	public double old_callGraphBuildTime;
	public String old_Memory;
	public int opa_entryPoints;
	public int opa_callEdgesCount;
	public int opa_callBySignatureEdgesCount;
	public double opa_EntryPointCalculationTime;
	public double opa_callGraphBuildTime;
	public String opa_Memory;
	public int cpa_entryPoints;
	public int cpa_callEdgesCount;
	public int cpa_callBySignatureEdgesCount;
	public double cpa_EntryPointCalculationTime;
	public double cpa_callGraphBuildTime;
	public String cpa_Memory;
	
	public LibraryResult(Library library) {
		this.organisation = library.organisation();
		this.name = library.name();
		this.revision = library.revision();
	}
	
	public LibraryResult(String[] parts) {
		
		this.organisation = parts[0];
		this.name = parts[1];
		this.revision = parts[2];
		
		this.dependencies = Integer.parseInt(parts[3]);
		this.all_classFileCount = Integer.parseInt(parts[4]);
		this.all_classCount = Integer.parseInt(parts[5]);
		// Looks like concat of all_publicInterfaceCount and all_packageVisibleInterfaceCount instead of sum.
		this.all_interfaceCount = Integer.parseInt(parts[9]) + Integer.parseInt(parts[10]);
		this.all_publicClassCount = Integer.parseInt(parts[7]);
		this.all_packageVisibleClassCount = Integer.parseInt(parts[8]);
		this.all_publicInterfaceCount = Integer.parseInt(parts[9]);
		this.all_packageVisibleInterfaceCount = Integer.parseInt(parts[10]);
		this.project_classFileCount = Integer.parseInt(parts[11]);
		this.project_classCount = Integer.parseInt(parts[12]);
		this.project_interfaceCount = Integer.parseInt(parts[13]);			// Looks like concat of project_publicInterfaceCount and project_packageVisibleInterfaceCount instead of sum.
		this.project_publicClassCount = Integer.parseInt(parts[14]);
		this.project_packageVisibleClassCount = Integer.parseInt(parts[15]);
		this.project_publicInterfaceCount = Integer.parseInt(parts[16]);
		this.project_packageVisibleInterfaceCount = Integer.parseInt(parts[17]);
		this.libraries_classFileCount = Integer.parseInt(parts[18]);
		this.libraries_classCount = Integer.parseInt(parts[19]);
		this.libraries_interfaceCount = Integer.parseInt(parts[20]);
		this.libraries_publicClassCount = Integer.parseInt(parts[21]);
		this.libraries_packageVisibleClassCount = Integer.parseInt(parts[22]);
		this.libraries_publicInterfaceCount = Integer.parseInt(parts[23]);
		this.libraries_packageVisibleInterfaceCount = Integer.parseInt(parts[24]);
		this.all_methodCount = Integer.parseInt(parts[25]);
		this.all_publicMethods = Integer.parseInt(parts[26]);
		this.all_protectedMethods = Integer.parseInt(parts[27]);
		this.all_packagePrivateMethods = Integer.parseInt(parts[28]);
		this.all_privateMethods = Integer.parseInt(parts[29]);
		this.project_methodCount = Integer.parseInt(parts[30]);
		this.project_publicMethods = Integer.parseInt(parts[31]);
		this.project_protectedMethods = Integer.parseInt(parts[32]);
		this.project_packagePrivateMethods = Integer.parseInt(parts[33]);
		this.project_privateMethods = Integer.parseInt(parts[34]);
		this.libraries_methodCount = Integer.parseInt(parts[35]);
		this.libraries_publicMethods = Integer.parseInt(parts[36]);
		this.libraries_protectedMethods = Integer.parseInt(parts[37]);
		this.libraries_packagePrivateMethods = Integer.parseInt(parts[38]);
		this.libraries_privateMethods = Integer.parseInt(parts[39]);
		this.old_entryPoints = Integer.parseInt(parts[40]);
		this.old_callEdgesCount = Integer.parseInt(parts[41]);
		this.old_EntryPointCalculationTime = Double.parseDouble(parts[42]);
		this.old_callGraphBuildTime = Double.parseDouble(parts[43]);
		this.old_Memory = parts[44];
		this.opa_entryPoints = Integer.parseInt(parts[45]);
		this.opa_callEdgesCount = Integer.parseInt(parts[46]);
		this.opa_callBySignatureEdgesCount = Integer.parseInt(parts[47]);
		this.opa_EntryPointCalculationTime = Double.parseDouble(parts[48]);
		this.opa_callGraphBuildTime = Double.parseDouble(parts[49]);
		this.opa_Memory = parts[50];
		this.cpa_entryPoints = Integer.parseInt(parts[51]);
		this.cpa_callEdgesCount = Integer.parseInt(parts[52]);
		this.cpa_callBySignatureEdgesCount = Integer.parseInt(parts[53]);
		this.cpa_EntryPointCalculationTime = Double.parseDouble(parts[54]);
		this.cpa_callGraphBuildTime = Double.parseDouble(parts[55]);
		this.cpa_Memory = parts[56];
	}
}