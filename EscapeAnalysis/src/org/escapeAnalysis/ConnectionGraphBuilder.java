package org.escapeAnalysis;

import java.util.List;

import org.escapeAnalysis.connectionGraph.*;

import soot.RefType;

/**
 * Performs an intra procedural analysis on a single method to create its
 * connection graph.
 */
public class ConnectionGraphBuilder extends EscapeStatementVisitor {

    public enum AnalysisType {
        FlowSensitive, FlowInsensitive;
    }

    private AnalysisType analysisType;
    private Boolean verbose = false;

    private ConnectionGraph connectionGraph;

    public ConnectionGraphBuilder() {

        this.analysisType = AnalysisType.FlowSensitive;
        this.connectionGraph = new ConnectionGraph();
    }

    public ConnectionGraph connectionGraph() {
        return this.connectionGraph;
    }

    /*
     * Visit the assignment of 'this' to a local variable.
     */
    @Override
    public void visitThis(String localName) {
        printDebug("visitThis %s", localName);
        this.connectionGraph.addThisVariable(localName);
    }

    /*
     * Visit the assignment of a reference parameter to a local variable.
     */
    @Override
    public void visitParameter(String localName) {
        printDebug("visitParameter %s", localName);
        this.connectionGraph.addParameterVariable(localName);
    }

    /*
     * (1) Visit an assignment of the form of p = new T()
     */
    @Override
    public void visitNew(RefType objectType, String localName) {
        printDebug("visitNew %s = new %s()", localName, objectType.getClassName());

        String objectid = objectType.getClassName() + " [" + localName + "]";

        // Create a new object node
        ObjectNode objectNode = new ObjectNode(objectid, objectType);

        connectionGraph.addObjectNode(objectNode);

        ReferenceVariable target = connectionGraph.ensureLocal(localName);

        // For flow-sensitive analysis, we first apply the ByPass(p) function.
        if (this.analysisType == AnalysisType.FlowSensitive) {
            this.connectionGraph.byPass(target);
        }

        // Add a points-to edge p -> O
        target.pointsTo(objectNode);
    }

    /*
     * (2) Visit an assignment of the form of p = q
     */
    @Override
    public void visitAssignment(String localNameLeft, String localNameRight) {
        printDebug("visitAssignment %s = %s", localNameLeft, localNameRight);

        ReferenceVariable target = connectionGraph.ensureLocal(localNameLeft);
        ReferenceVariable source = connectionGraph.ensureLocal(localNameRight);

        // For flow-sensitive analysis, we first apply the ByPass(p) function.
        if (this.analysisType == AnalysisType.FlowSensitive) {
            this.connectionGraph.byPass(target);
        }

        // Add a deferred edge p -> q
        target.addNode(source);
    }

    /*
     * (3a) Visit a field assignment of the form of p.f = q (non-static)
     */
    @Override
    public void visitNonStaticFieldAssignment(String localNameLeft, String fieldName, String localNameRight) {

        printDebug("visitNonStaticFieldAssignment %s.%s = %s", localNameLeft, fieldName, localNameRight);

        ReferenceVariable target = this.connectionGraph.ensureLocal(localNameLeft);
        ReferenceVariable sourceNode = this.connectionGraph.ensureLocal(localNameRight);

        if (target.pointsToNothing()) {

            // Create object phantom node Oph.
            PhantomObjectNode phantomObject = new PhantomObjectNode();

            // Add a points-to edge p -> Oph
            target.pointsTo(phantomObject);

            this.connectionGraph.addPhantomObjectNode(phantomObject);
            printDebug("   created phantom node for %s.", localNameLeft);
        }

        ObjectNodeSet objectNodes = target.pointsTo();

        for (ObjectNode objectNode : objectNodes) {
            FieldNode fieldNode = objectNode.getFieldNode(fieldName);

            // Lazily add a field node if it doesn't exist yet.
            if (fieldNode == null) {

                fieldNode = new NonStaticField(fieldName);

                // Add a field edge
                objectNode.addNode(fieldNode);
                printDebug("   created field node '%s' for %s.", fieldName, objectNode.id());
            }

            // Add a deferred edge v -> q
            fieldNode.addNode(sourceNode);
        }
    }

    /*
     * (3b) Visit a static field assignment of the form of P.f = q (static)
     */
    @Override
    public void visitStaticFieldAssignment(RefType objectType, String fieldName, String localNameRight) {

        printDebug("visitStaticFieldAssignment %s.%s = %s", objectType.getClassName(), fieldName, localNameRight);

        ReferenceVariable sourceNode = this.connectionGraph.ensureLocal(localNameRight);
        ObjectNodeSet objectNodes = this.connectionGraph.getObjectsOfType(objectType);

        if (objectNodes.isEmpty()) {

            // Create object phantom node.
            PhantomObjectNode phantomObject = new PhantomObjectNode();

            this.connectionGraph.addPhantomObjectNode(phantomObject);
            printDebug("   created phantom node for %s.", objectType.getClassName());
        }

        objectNodes = this.connectionGraph.getObjectsOfType(objectType);

        for (ObjectNode objectNode : objectNodes) {
            FieldNode fieldNode = objectNode.getFieldNode(fieldName);

            // Lazily add a field node if it doesn't exist yet.
            if (fieldNode == null) {

                fieldNode = new StaticField(fieldName);
                // Add a static field edge
                objectNode.addNode(fieldNode);
            }

            // Add a deferred edge v -> q
            fieldNode.addNode(sourceNode);

            // An assignment to a static field of a class is a special case of this transfer
            // function: all the objects pointed to by q of the right-hand side will become
            // GlobalEscape.
            for (ObjectNode sourceObjectNode : sourceNode.pointsTo()) {
                sourceObjectNode.setEscape(EscapeState.ESCAPE);
            }
        }
    }

    /*
     * (4a) Visit an assignment of the form of p = q.f (non-static)
     */
    @Override
    public void visitAssignment(String localNameLeft, String localNameRight, String fieldName) {

        printDebug("visitAssignment %s = %s.%s", localNameLeft, localNameRight, fieldName);

        ReferenceVariable target = connectionGraph.ensureLocal(localNameLeft);
        ReferenceVariable source = connectionGraph.ensureLocal(localNameRight);

        if (source.pointsToNothing()) {

            // Create object phantom node.
            PhantomObjectNode phantomObject = new PhantomObjectNode();

            // Add a points-to edge q -> Oph
            source.pointsTo(phantomObject);

            this.connectionGraph.addPhantomObjectNode(phantomObject);
            printDebug("   created phantom node for %s.", localNameRight);
        }

        // For flow-sensitive analysis, we first apply the ByPass(p) function.
        if (this.analysisType == AnalysisType.FlowSensitive) {
            this.connectionGraph.byPass(target);
        }

        ObjectNodeSet objectNodes = source.pointsTo();

        for (ObjectNode objectNode : objectNodes) {
            FieldNode fieldNode = objectNode.getFieldNode(fieldName);

            // Lazily add a field node 'v' if it doesn't exist yet.
            if (fieldNode == null) {

                fieldNode = new NonStaticField(fieldName, objectNode.getEscapeState());

                // Add a field edge
                objectNode.addNode(fieldNode);
                printDebug("   created field node '%s' for %s.", fieldName, objectNode.id());
            }

            // Add a deferred edge v -> q
            target.addNode(fieldNode);
        }
    }

    /*
     * (4b) Visit an assignment in the form of p = Q.f (static)
     */
    @Override
    public void visitAssignment(String localNameLeft, RefType objectType, String fieldName) {

        printDebug("visitStaticFieldAssignment %s = %s.%s", localNameLeft, objectType.getClassName(), fieldName);

        ReferenceVariable target = this.connectionGraph.ensureLocal(localNameLeft);
        ObjectNodeSet objectNodes = this.connectionGraph.getObjectsOfType(objectType);

        if (objectNodes.isEmpty()) {

            // Create object phantom node.
            PhantomObjectNode phantomObject = new PhantomObjectNode();

            this.connectionGraph.addPhantomObjectNode(phantomObject);
            printDebug("   created phantom node for %s.", objectType.getClassName());
        }

        // For flow-sensitive analysis, we first apply the ByPass(p) function.
        if (this.analysisType == AnalysisType.FlowSensitive) {
            this.connectionGraph.byPass(target);
        }

        objectNodes = this.connectionGraph.getObjectsOfType(objectType);

        for (ObjectNode objectNode : objectNodes) {
            FieldNode fieldNode = objectNode.getFieldNode(fieldName);

            // Lazily add a field node 'v' if it doesn't exist yet.
            if (fieldNode == null) {

                fieldNode = new StaticField(fieldName);
                // Add a static field edge
                objectNode.addNode(fieldNode);
            }

            // Add a deferred edge p -> v
            target.addNode(fieldNode);
        }
    }

    @Override
    public void visitReturn(String localName) {

        printDebug("visitReturn %s", localName);

        ReferenceVariable node = this.connectionGraph.ensureLocal(localName);

        node.setEscape(EscapeState.ESCAPE);
    }

    /*
     * Visit the invocation of a method.
     */
    public void visitMethodInvoke(List<String> localArguments) {
        printDebug("visitMethodInvoke");
        for (String localName : localArguments) {
            ReferenceVariable node = this.connectionGraph.ensureLocal(localName);

            node.setEscape(EscapeState.ESCAPE);
        }
    }

    private void printDebug(String format, Object... args) {
        if (this.verbose) {
            System.out.format("   " + format, args);
            System.out.println();
        }
    }
}
