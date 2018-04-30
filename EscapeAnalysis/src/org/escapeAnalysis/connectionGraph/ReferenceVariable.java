package org.escapeAnalysis.connectionGraph;

/**
 * Represents a local reference variable.
 */
public class ReferenceVariable extends ReferenceNode {

    public ReferenceVariable(String id) {
        this(id, EscapeState.UNRESOLVED);
    }

    public ReferenceVariable(String id, EscapeState escapeState) {
        super(id, escapeState);
    }

    @Override
    public String toString() {
        return "Reference variable (" + this.id() + ")";
    }
}
