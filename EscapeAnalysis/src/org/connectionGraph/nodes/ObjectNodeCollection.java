package org.connectionGraph.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a collection of object nodes.
 */
public class ObjectNodeCollection implements Iterable<ObjectNode> {

	private final List<ObjectNode> nodes;

	public ObjectNodeCollection() {
		this.nodes = new ArrayList<ObjectNode>();
	}

	public void add(ObjectNode node) {
		this.nodes.add(node);
	}

	public void addAll(ObjectNodeCollection other) {
		this.nodes.addAll(other.nodes);
	}
	
	public void addAll(Collection<ObjectNode> other) {
		for(ObjectNode node : other) {
			this.nodes.add(node);
		}
	}
	
	public int size() {
		return this.nodes.size();
	}
	
	@Override
	public Iterator<ObjectNode> iterator() {
		return this.nodes.iterator();
	}
}
