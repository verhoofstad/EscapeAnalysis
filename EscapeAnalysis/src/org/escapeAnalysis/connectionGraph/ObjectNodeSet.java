package org.escapeAnalysis.connectionGraph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a collection of object nodes.
 */
public class ObjectNodeSet implements Iterable<ObjectNode> {

	private final Map<String, ObjectNode> nodes;

	public ObjectNodeSet() {
		this.nodes = new HashMap<String, ObjectNode>();
	}
	
	public void add(ObjectNode node) {
		if(!this.nodes.containsKey(node.id())) {
			this.nodes.put(node.id(), node);
		} else {
			//System.out.println("Node already added: " + node.id());
		}
	}
		
	public void addAll(Iterable<ObjectNode> other) {
		for(ObjectNode node : other) {
			add(node);
		}
	}
	
	public boolean contains(String id) {
		return this.nodes.containsKey(id);
	}
	
	public boolean contains(ObjectNode item) {
		return this.nodes.containsKey(item.id());
	}
	
	public int size() {
		return this.nodes.size();
	}
	
	public boolean isEmpty() {
		return this.nodes.isEmpty();
	}
	
	@Override
	public Iterator<ObjectNode> iterator() {
		return this.nodes.values().iterator();
	}
}
