package org.escapeAnalysis.connectionGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import soot.RefType;

/**
 * Represents an object.
 */
public class ObjectNode extends Node {

    private RefType objectType;

    // Edges to both the static and non-static field nodes;
    private Map<String, FieldNode> fieldNodes;

    public ObjectNode(String id, RefType objectType) {
        super(id);

        this.fieldNodes = new HashMap<String, FieldNode>();
        this.objectType = objectType;
    }

    public Collection<FieldNode> getFieldNodes() {
        return this.fieldNodes.values();
    }

    public RefType getObjectType() {
        return this.objectType;
    }

    public void addNode(FieldNode node) {
        this.fieldNodes.put(node.id(), node);
    }

    public void prettyPrint(int indent) {

        String indentStr = new String(new char[indent]).replace("\0", "   ");

        System.out.println(indentStr + this.toString() + " " + this.getEscapeState().toString());
        for (FieldNode fieldNode : this.fieldNodes.values()) {
            System.out.println(indentStr + "   " + fieldNode.toString() + this.getEscapeState().toString());

            fieldNode.prettyPrint(indent + 2);
        }
    }

    /**
     * Gets a the field node with a specified id -or- null if the current object
     * does not contain a field node with the specified id.
     * 
     * @param id
     * @return
     */
    public FieldNode getFieldNode(String id) {
        return this.fieldNodes.get(id);
    }

    @Override
    public void setEscape(EscapeState escapeState) {
        super.setEscape(escapeState);

        for (FieldNode fieldNode : this.fieldNodes.values()) {
            fieldNode.setEscape(escapeState);
        }
    }

    @Override
    public String toString() {
        return "Object (" + this.id() + ")";
    }
}
