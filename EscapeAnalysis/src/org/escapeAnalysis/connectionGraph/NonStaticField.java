package org.escapeAnalysis.connectionGraph;

/**
 * Represents an instance field of an object.
 */
public class NonStaticField extends FieldNode {

    public NonStaticField(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return "Non-static field (" + this.id() + ")";
    }
}
