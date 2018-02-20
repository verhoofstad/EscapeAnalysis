package org.callGraphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.classHierarchy.tree.JavaMethod;

public class CallGraph {

	private Map<String, CallSiteSet> callSites = new HashMap<String, CallSiteSet>();
	

	public void addStaticCallSite(JavaMethod source, JavaMethod target) {		
		this.addCallSite(new CallSite(source, target));
	}
	
	public void addVirtualCallSite(JavaMethod source, List<JavaMethod> virtualTargets) {	
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

	public int nrOfVirtualEdges() {
		int nrOfEdges = 0;
		for(CallSiteSet callSiteSet : this.callSites.values()) {
			nrOfEdges += callSiteSet.nrOfVirtualEdges();
		}
		return nrOfEdges;
	}

	public int nrOfVirtualMonoEdges() {
		int nrOfEdges = 0;
		for(CallSiteSet callSiteSet : this.callSites.values()) {
			nrOfEdges += callSiteSet.nrOfVirtualMonoEdges();
		}
		return nrOfEdges;
	}

	public int nrOfStaticEdges() {
		int nrOfEdges = 0;
		for(CallSiteSet callSiteSet : this.callSites.values()) {
			nrOfEdges += callSiteSet.nrOfStaticEdges();
		}
		return nrOfEdges;
	}

	public void printReport() {

		System.out.println("Call graph");
		System.out.println("----------");
		System.out.format("Total number of edges:       %s\n", nrOfEdges());
		System.out.format("Number of virtual edges:     %s\n", nrOfVirtualEdges());
		System.out.format("   of which are monomorphic: %s\n", nrOfVirtualMonoEdges());
		System.out.format("Number of static edges:      %s\n", nrOfStaticEdges());
	}
}
