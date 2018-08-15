package org.callGraphs;

import java.util.HashSet;
import java.util.Set;

import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;

/**
 * Represents a work list for Rapid Type Analysis in which a work item can only be processed once.
 */
public class Worklist {

    private JavaMethodSet toProcess = new JavaMethodSet();
    private Set<String> processed = new HashSet<String>();

    public Worklist(JavaMethodSet worklist) {
        this.toProcess = new JavaMethodSet(worklist);
    }

    public JavaMethod getItem() {
        JavaMethod item = this.toProcess.getRandom();
        this.toProcess.remove(item.id());
        this.processed.add(item.id());
        return item;
    }

    public void add(JavaMethod item) {
        if (!this.toProcess.contains(item.id()) && !this.processed.contains(item.id())) {
            this.toProcess.add(item);
        }
    }
    
    public int size() {
        return this.toProcess.size();
    }
    
    public boolean isEmpty() {
        return this.toProcess.isEmpty();
    }
}
