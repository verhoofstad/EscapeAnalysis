package org.escapeAnalysis.connectionGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a non-object; i.e. a reference variable or a static or non-static field.
 */
public abstract class ReferenceNode extends Node {

    protected Map<String, ObjectNode> pointsToNodes;
    protected Map<String, ReferenceNode> deferredNodes;

    public ReferenceNode(String id) {
        this(id, EscapeState.UNRESOLVED);
    }

    public ReferenceNode(String id, EscapeState escapeState) {
        super(id, escapeState);

        pointsToNodes = new HashMap<String, ObjectNode>();
        deferredNodes = new HashMap<String, ReferenceNode>();
    }

    public void addNode(ReferenceNode node) {

        if (node.id().equals(this.id())) {
            System.out.println("WARNING: Node " + this.id() + " gets reference to self!");
        } else {
            this.deferredNodes.put(node.id(), (ReferenceNode) node);
        }
    }

    @Override
    public Set<Node> successorNodes() {
        Set<Node> successorNodes = new HashSet<Node>();
        for(Node pointsToNode : this.pointsToNodes.values()) {
            successorNodes.add(pointsToNode);
        }
        for(Node deferredNode : this.deferredNodes.values()) {
            successorNodes.add(deferredNode);
        }
        return successorNodes;
    }
    
    public ObjectNodeSet pointsTo() {
        return pointsTo(this, new HashSet<String>());
    }

    ObjectNodeSet pointsTo(ReferenceNode currentNode, Set<String> visitedNodes) {

        ObjectNodeSet objectNodes = new ObjectNodeSet();

        objectNodes.addAll(currentNode.pointsToNodes.values());
        visitedNodes.add(currentNode.id());

        for (ReferenceNode node : currentNode.deferredNodes.values()) {
            if (!visitedNodes.contains(node.id())) {

                for (ObjectNode pointsToNode : pointsTo(node, visitedNodes)) {
                    if (!objectNodes.contains(pointsToNode)) {
                        objectNodes.add(pointsToNode);
                    }
                }
            }
        }
        return objectNodes;
    }

    public void pointsTo(ObjectNode objectNode) {
        this.pointsToNodes.put(objectNode.id(), objectNode);
    }

    public boolean pointsToNothing() {
        return this.pointsTo().isEmpty();
    }

    public void byPass(ReferenceNode nodeToByPass) {
        if (this.id().equals(nodeToByPass.id())) {
            // A node cannot bypass its self.
            return;
        }

        if (this.deferredNodes.containsKey(nodeToByPass.id())) {
            this.deferredNodes.remove(nodeToByPass.id());

            for (ObjectNode objectNode : nodeToByPass.pointsToNodes.values()) {
                pointsTo(objectNode);
            }
            for (ReferenceNode nonObjectNode : nodeToByPass.deferredNodes.values()) {
                addNode(nonObjectNode);
            }
        }
    }

    public void prettyPrint(int indent) {

        String indentStr = new String(new char[indent]).replace("\0", "   ");

        System.out.println(indentStr + this.toString() + this.getEscapeState().toString());
        for (ObjectNode pointsTo : pointsToNodes.values()) {
            System.out.println(indentStr + "   " + pointsTo.toString() + this.getEscapeState().toString());
        }
        for (ReferenceNode deferred : deferredNodes.values()) {
            System.out.println(indentStr + "   " + deferred.toString() + this.getEscapeState().toString());
        }
    }
}