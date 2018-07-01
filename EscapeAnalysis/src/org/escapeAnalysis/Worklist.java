package org.escapeAnalysis;

import java.util.HashSet;
import java.util.Set;

import org.escapeAnalysis.connectionGraph.Node;


/**
 * Represents a work list in which a work item can only be processed once.
 */
class Worklist {
    private Set<Node> toProcess = new HashSet<Node>();
    private Set<Node> processed = new HashSet<Node>();

    Worklist() {
    
    }
    
    public Node getItem() {
        Node item = this.toProcess.iterator().next();
        this.toProcess.remove(item);
        this.processed.add(item);
        return item;
    }

    public void add(Node item) {
        if (!this.toProcess.contains(item) && !this.processed.contains(item)) {
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
