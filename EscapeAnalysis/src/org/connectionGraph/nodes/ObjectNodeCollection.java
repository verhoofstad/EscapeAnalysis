package org.connectionGraph.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a collection of object nodes.
 */
public class ObjectNodeCollection implements Iterable<ObjectNode> {

	private final ArrayList<ObjectNode> nodes;

	public ObjectNodeCollection() {
		nodes = new ArrayList<ObjectNode>();
	}

	public void add(ObjectNode node) {
		nodes.add(node);
	}

	public void addAll(ObjectNodeCollection other) {
		nodes.addAll(other.nodes);
	}
	
	public void addAll(Collection<ObjectNode> other) {
		for(ObjectNode node : other) {
			nodes.add(node);
		}
	}

	/*
	public void remove(ObjectNode node) {
		nodes.remove(node);
	}*/
	
	public int size() {
		return this.nodes.size();
	}
	
	
	@Override
	public Iterator<ObjectNode> iterator() {
		return nodes.iterator();
	}
}
