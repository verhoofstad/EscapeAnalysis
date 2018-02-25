package org.callGraphs.cha;

import java.util.HashMap;
import java.util.Map;

import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaType;
import org.classHierarchy.tree.JavaTypeSet;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;

public class AppliesToSets {

	Map<String, JavaMethodSet> appliesToSet;
	
	public AppliesToSets(ClassHierarchy classHierarchy) {
		
		this.appliesToSet = new HashMap<String, JavaMethodSet>();
				
		for(JavaType javaClass : classHierarchy.getClasses()) {
			
			for(JavaMethod declaredMethod : javaClass.declaredMethods()) {
				
				if(!this.appliesToSet.containsKey(declaredMethod.signature())) {
					
					this.appliesToSet.put(declaredMethod.signature(), new JavaMethodSet());
				}
				this.appliesToSet.get(declaredMethod.signature()).add(declaredMethod);
			}
		}
		
		for(JavaType javaInterface : classHierarchy.getInterfaces()) {
			
			for(JavaMethod declaredMethod : javaInterface.declaredMethods()) {
				
				if(!declaredMethod.isAbstract()) {
					if(!this.appliesToSet.containsKey(declaredMethod.signature())) {
						
						this.appliesToSet.put(declaredMethod.signature(), new JavaMethodSet());
					}
					this.appliesToSet.get(declaredMethod.signature()).add(declaredMethod);
				}
			}
		}
	}
	
	public JavaMethodSet appliesTo(JavaTypeSet coneSet, String name, String desc) {
		
		String signature = JavaMethod.toSignature(name, desc);
		
		JavaMethodSet methods = new JavaMethodSet();
		
		if(this.appliesToSet.containsKey(signature)) {
			
			for(JavaMethod method : this.appliesToSet.get(signature)) {
				
				if(method.appliesTo().overlapsWith(coneSet)) {
					methods.add(method);
				}
			}
		}
		return methods;
	}
}
