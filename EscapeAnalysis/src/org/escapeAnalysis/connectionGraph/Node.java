package org.escapeAnalysis.connectionGraph;

import java.util.Set;

/**
 * Represents a connection graph node.
 */
public abstract class Node {

    private final String id;
    private EscapeState escapeState;

    public Node(String id) {
        this.id = id;
        this.escapeState = EscapeState.UNRESOLVED;
    }

    public Node(String id, EscapeState escapeState) {
        this.id = id;
        this.escapeState = escapeState;
    }

    public String id() {
        return this.id;
    }
    
    public abstract Set<Node> successorNodes();

    public EscapeState getEscapeState() {
        return this.escapeState;
    }

    public void setEscape(EscapeState escapeState) {
        this.escapeState = escapeState;
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Node && this.id.equals(((Node) obj).id());
    }
}