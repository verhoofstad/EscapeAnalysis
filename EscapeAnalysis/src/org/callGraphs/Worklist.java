package org.callGraphs;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;

/**
 * Represents a work list for Rapid Type Analysis in which a work item can only be processed once.
 */
public class Worklist {

    private Queue<JavaMethod> toProcess = new LinkedList<JavaMethod>();
    private Set<String> processed = new HashSet<String>();

    public Worklist(JavaMethodSet worklist) {
        this.toProcess =  new LinkedList<JavaMethod>();
        for(JavaMethod javaMethod : worklist) {
            this.toProcess.add(javaMethod);
            this.processed.add(javaMethod.id());
        }
    }

    public JavaMethod getItem() {
        JavaMethod item = this.toProcess.remove();
        this.processed.add(item.id());
        return item;
    }

    public void add(JavaMethod item) {
        if (!this.processed.contains(item.id())) {
            this.toProcess.add(item);
            this.processed.add(item.id());
        }
    }
    
    public int size() {
        return this.toProcess.size();
    }
    
    public boolean isEmpty() {
        return this.toProcess.isEmpty();
    }
}
