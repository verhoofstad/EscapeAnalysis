package org.callGraphs.rta;

import java.util.HashSet;
import java.util.Set;

import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;

class Worklist {
	
	private JavaMethodSet toProcess = new JavaMethodSet();
	private Set<String> processed = new HashSet<String>();
	
	Worklist(JavaMethodSet worklist) {
		this.toProcess = worklist;
	}
	
	public boolean isEmpty() {
		return toProcess.isEmpty();
	}

	public JavaMethod removeItem() {
		JavaMethod item = this.toProcess.getRandom();
		this.toProcess.remove(item.id());
		this.processed.add(item.id());
		return item;
	}
	
	public void add(JavaMethod item) {
		if(!this.toProcess.contains(item.id()) && !this.processed.contains(item.id())) {
			this.toProcess.add(item);
		}
	}
}
