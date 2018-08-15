package org.callGraphs.cha;

import org.asm.jvm.InvocationType;
import org.asm.jvm.InvokedMethod;
import org.callGraphs.CallGraph;
import org.callGraphs.Worklist;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;

public class ClassHierarchyAnalysis {

    private ClassHierarchy classHierarchy;
    private Worklist worklist;
    private AppliesToSets appliesToSets;
    
    public ClassHierarchyAnalysis(ClassHierarchy classHierachy, JavaMethodSet entryPoints) {
        this.classHierarchy = classHierachy;
        this.worklist = new Worklist(entryPoints);
        this.appliesToSets = new AppliesToSets(this.classHierarchy);
    }
    
    public CallGraph computeCallGraph() {
        
        CallGraph callGraph = new CallGraph();
        
        while (!this.worklist.isEmpty()) {

            JavaMethod currentMethod = this.worklist.getItem();
            
            for (InvokedMethod invokedMethod : currentMethod.invokedMethods()) {

                JavaType declType = this.classHierarchy.getType(invokedMethod.declaredType());

                if(invokedMethod.invocationType() == InvocationType.CONSTRUCTOR) {
                    JavaMethod target = declType.getMethod(invokedMethod.signature());
                    
                    callGraph.addStaticCallSite(currentMethod, target);
                    this.worklist.add(target);
                    
                } else if(invokedMethod.invocationType() == InvocationType.STATIC) {
                    
                    JavaMethod target = declType.findStaticMethod(invokedMethod.signature());
                    
                    callGraph.addStaticCallSite(currentMethod, target);
                    this.worklist.add(target);
                }
                else {
                    
                    if (!declType.id().equals("java/lang/invoke/MethodHandle")) {
                        JavaMethodSet virtualTargets = appliesToSets.appliesTo(declType.coneSet(), invokedMethod.signature());
                        
                        // If the declared type is an abstract class or an interface, the invoked method may not 
                        // have a concrete implementation. If the call-site has no target methods, we omit it from the call graph.
                        if(!virtualTargets.isEmpty()) {
                            callGraph.addVirtualCallSite(currentMethod, virtualTargets);
                            for(JavaMethod virtualTarget : virtualTargets) {
                                this.worklist.add(virtualTarget);
                            }
                        }                    
                    }
                }
            }
        }
        return callGraph;
    }
}
