package org.callGraphs;

import java.util.HashMap;
import java.util.Map;

import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;

public class CallGraph {

	private Map<String, CallSiteSet> callSites = new HashMap<String, CallSiteSet>();
	

	public void addStaticCallSite(JavaMethod source, JavaMethod target) {		
		this.addCallSite(new CallSite(source, target));
	}
	
	public void addVirtualCallSite(JavaMethod source, JavaMethodSet virtualTargets) {	
		this.addCallSite(new CallSite(source, virtualTargets));
	}
	
	public void addCallSite(CallSite callSite) {
		String sourceId = callSite.source().id();
		if(!this.callSites.containsKey(sourceId)) {
			this.callSites.put(sourceId, new CallSiteSet(callSite.source()));
		}
		
		this.callSites.get(sourceId).addCallSite(callSite);
	}
	
	/*
	 * Gets the call site information for a given method.
	 * 
	 * A method can have zero or more call sites.
	 */
	public CallSiteSet getCallSite(JavaMethod javaMethod) {
	
		if(this.callSites.containsKey(javaMethod.id())) {
			return this.callSites.get(javaMethod.id());
		} else {
			return new CallSiteSet(javaMethod);
		}
	}
	
	
	public int nrOfEdges() {
		int nrOfEdges = 0;
		for(CallSiteSet callSiteSet : this.callSites.values()) {
			nrOfEdges += callSiteSet.nrOfEdges();
		}
		return nrOfEdges;
	}
	
	public int nrOfCallSites() {
		int nrOfCallSites = 0;
		for(CallSiteSet callSiteSet : this.callSites.values()) {
			nrOfCallSites += callSiteSet.size();
		}
		return nrOfCallSites;
	}

	public int nrOfVirtualCallSites() {
		int nrOfCallSites = 0;
		for(CallSiteSet callSiteSet : this.callSites.values()) {
			nrOfCallSites += callSiteSet.nrOfVirtualCallSites();
		}
		return nrOfCallSites;
	}

	public int nrOfVirtualMonoCallSites() {
		int nrOfCallSites = 0;
		for(CallSiteSet callSiteSet : this.callSites.values()) {
			nrOfCallSites += callSiteSet.nrOfVirtualMonoCallSites();
		}
		return nrOfCallSites;
	}

	public int nrOfStaticCallSites() {
		int nrOfCallSites = 0;
		for(CallSiteSet callSiteSet : this.callSites.values()) {
			nrOfCallSites += callSiteSet.nrOfStaticCallSites();
		}
		return nrOfCallSites;
	}

	public void printReport() {

		System.out.println("Call graph");
		System.out.println("----------");
		System.out.format("Total number of edges:       %s\n", nrOfEdges());
		System.out.format("Total number of call sites:  %s\n", nrOfCallSites());
		System.out.format(" - Virtual call sites:       %s\n", nrOfVirtualCallSites());
		System.out.format(" - Monomorphic call sites:   %s\n", nrOfVirtualMonoCallSites());
		System.out.format(" - Static call sites:        %s\n", nrOfStaticCallSites());
		
		
	}
}
