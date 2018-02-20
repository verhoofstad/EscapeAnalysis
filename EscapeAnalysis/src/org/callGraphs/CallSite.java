package org.callGraphs;

import java.util.ArrayList;
import java.util.List;

import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaMethod;

/*
 * Represents a single method invocation from a source method to a set of possible targets.
 */
public class CallSite {

	private JavaMethod source;
	private List<JavaMethod> targets = new ArrayList<JavaMethod>();
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
	
	public CallSite(JavaMethod source, List<JavaMethod> virtualTargets) {
		if(virtualTargets.size() < 1) {
			throw new Error();
		}
		
		this.isStatic = false;
		this.source = source;
		this.targets.addAll(virtualTargets);
	}
	
	public JavaMethod source() {
		return this.source;
	}
	
	public List<JavaMethod> targets() {
		return this.targets;
	}
	
	public boolean isConstructor( ) {
		return this.targets.size() == 1 && this.targets.get(0).isConstructor();
	}
	
	public JavaClass getInstantiatedClass() {
		if(this.isConstructor()) {
			return (JavaClass)this.targets.get(0).containedIn();
		} else {
			return null;
		}
	}
	
	public boolean isVirtual() {
		
		if(this.targets.isEmpty()) {
			System.out.println("Is leeg");
		}
		
		if(this.targets.get(0) == null) {
			System.out.println("Is leeg 2");
		}
		
		return !this.targets.get(0).isConstructor()
			&& !this.targets.get(0).isStatic();
	}
	
	public boolean isStatic() {
		return this.isStatic;
	}
	
	public boolean isMonomorphic() {
		return this.targets.size() == 1;
	}
	
	int nrOfEdges() {
		return this.targets.size(); 
	}
}
