package org.callGraphs;

import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;

/*
 * Represents a single method invocation from a source method to a set of possible targets.
 */
public class CallSite {

	private JavaMethod source;
	private JavaMethodSet targets = new JavaMethodSet();
	private boolean isStatic;
	
	public CallSite(JavaMethod source, JavaMethod nonVirtualTarget) {
		
		if(nonVirtualTarget == null) { throw new Error(); }
		/*
		if(!nonVirtualTarget.isConstructor() && !nonVirtualTarget.isStatic() && !nonVirtualTarget.isPrivate()) {
			throw new Error("Source " + source.toString() + ", target: " + nonVirtualTarget.toString());
		}*/
		
		this.isStatic = true;
		this.source = source;
		this.targets.add(nonVirtualTarget);
	}
	
	public CallSite(JavaMethod source, JavaMethodSet virtualTargets) {
		
		this.isStatic = false;
		this.source = source;
		this.targets.addAll(virtualTargets);
	}
	
	public JavaMethod source() {
		return this.source;
	}
	
	public JavaMethodSet targets() {
		return this.targets;
	}
	
	public boolean isConstructor( ) {
		return this.targets.size() == 1 && this.targets.getRandom().isConstructor();
	}
	
	public JavaClass getInstantiatedClass() {
		if(this.isConstructor()) {
			return (JavaClass)this.targets.getRandom().containedIn();
		} else {
			return null;
		}
	}
	
	public boolean isVirtual() {
		return !this.isStatic;
	}
	
	public boolean isStatic() {
		return this.isStatic;
	}
	
	public boolean isMonomorphic() {
		return this.targets.size() == 1;
	}
	
	public boolean hasNoTargets() {
		return this.targets.isEmpty();
	}
	
	int nrOfEdges() {
		return this.targets.size(); 
	}
}
