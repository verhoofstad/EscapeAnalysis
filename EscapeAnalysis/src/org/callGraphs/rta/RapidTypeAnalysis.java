package org.callGraphs.rta;

import java.util.ArrayList;
import java.util.List;

import org.callGraphs.CallGraph;
import org.callGraphs.CallSite;
import org.callGraphs.CallSiteSet;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaClassSet;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;

public class RapidTypeAnalysis {

	private CallGraph chaGraph;
	private CallGraph rtaGraph;
	
	private Worklist worklist;
	private JavaClassSet liveClasses;
	
	public RapidTypeAnalysis(CallGraph chaGraph) {
		
		this.chaGraph = chaGraph;
		this.rtaGraph = new CallGraph();
	}
	
	public CallGraph callGraph() {
		return this.rtaGraph;
	}
	
	public void setLibraryAnalysis(JavaClassSet exportedClasses, JavaMethodSet exportedMethods) {
		
		this.liveClasses = exportedClasses;
		this.worklist = new Worklist(exportedMethods);
	}
	
	public void analyse() {
		
		while(!this.worklist.isEmpty()) {
			
			JavaMethod currentMethod = this.worklist.removeItem();
			
			CallSiteSet callSites = this.chaGraph.getCallSite(currentMethod);
			
			for(CallSite callSite : callSites) {
				if(callSite.isConstructor()) {
					JavaClass instantiatedClass = callSite.getInstantiatedClass();
					if(!this.liveClasses.contains(instantiatedClass.id())) {
						this.liveClasses.add(instantiatedClass);
					}
				}
				
				if(callSite.isStatic()) {
					
					JavaMethod staticTarget = callSite.targets().get(0);
					
					this.rtaGraph.addStaticCallSite(currentMethod, staticTarget);
					this.worklist.add(staticTarget);
				} else {
					List<JavaMethod> rtaVirtualTargets = new ArrayList<JavaMethod>();
					
					for(JavaMethod virtualTarget : callSite.targets()) {
						JavaClass virtualTargetClass = (JavaClass)virtualTarget.containedIn();
						
						if(this.liveClasses.contains(virtualTargetClass.id())) {
							rtaVirtualTargets.add(virtualTarget);
							this.worklist.add(virtualTarget);
						}
					}
					if(rtaVirtualTargets.size() > 0) {
						this.rtaGraph.addVirtualCallSite(currentMethod, rtaVirtualTargets);
					}
				}
			}
 		}
	}
 }
