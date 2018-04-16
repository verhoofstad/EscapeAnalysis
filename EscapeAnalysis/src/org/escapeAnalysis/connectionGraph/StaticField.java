package org.escapeAnalysis.connectionGraph;

/**
 * Represents a static field (i.e. a global variable).
 */
public class StaticField extends FieldNode {

	public StaticField(String id) {
		super(id);
	}

	@Override
	public String toString() {
		return "Static field (" + this.id() + ")";
	}
}
