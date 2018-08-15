package org.callGraphs.rta;

import org.callGraphs.CallGraph;
import org.callGraphs.CallSite;
import org.callGraphs.CallSiteSet;
import org.callGraphs.Worklist;
import org.classHierarchy.JavaClass;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;

/**
 * Performs Rapid Type Analysis over a previously computed CHA call graph.
 */
public class RapidTypeAnalysis {

    private CallGraph chaGraph;
    private CallGraph rtaGraph;

    private Worklist worklist;
    private JavaTypeSet liveClasses;

    private JavaTypeSet confinedClasses;

    private boolean verbose = false;
    private boolean analyseWithConfinedClasses = false;
    
    public RapidTypeAnalysis(CallGraph chaGraph) {

        this.chaGraph = chaGraph;
        this.rtaGraph = new CallGraph();
        
        this.confinedClasses = new JavaTypeSet();
    }

    public void setLibraryAnalysis(JavaTypeSet exportedClasses, JavaMethodSet exportedMethods) {

        this.liveClasses = exportedClasses;
        this.worklist = new Worklist(exportedMethods);

        println("Initializing RTA for library analysis with %s exported classes (live) and %s exported methods (worklist).",
                exportedClasses.size(), exportedMethods.size());
    }

    public void setConfinedClasses(JavaTypeSet confinedClasses) {
        this.confinedClasses = confinedClasses;
        this.analyseWithConfinedClasses = true;
    }

    public CallGraph buildGraph() {

        while (!this.worklist.isEmpty()) {

            JavaMethod currentMethod = this.worklist.getItem();

            CallSiteSet callSites = this.chaGraph.getCallSites(currentMethod);

            // Find all instantiated classes in this method.
            for (CallSite callSite : callSites) {
                if (callSite.isConstructor()) {
                    JavaClass instantiatedClass = callSite.getInstantiatedClass();
                    
                    addToLiveClasses(instantiatedClass);
                }
            }
            
            for (CallSite callSite : callSites) {
                if (callSite.isStatic()) {

                    JavaMethod staticTarget = callSite.targets().getRandom();

                    this.rtaGraph.addStaticCallSite(currentMethod, staticTarget);
                    this.worklist.add(staticTarget);
                } else {
                    JavaMethodSet rtaVirtualTargets = new JavaMethodSet();
                    int classicRtaEdgeCount = 0;
                    
                    // For each virtual target determined by Class Hierarchy Analysis...
                    for (JavaMethod virtualTarget : callSite.targets()) {
                        JavaType targetType = virtualTarget.containedIn();

                        // ...Rapid Type Analysis only adds those which belong to instantiated classes.
                        if (this.liveClasses.contains(targetType.id())) {

                            // If we analyze with confined classes, some extra checks are needed.
                            if(this.analyseWithConfinedClasses) {
                                if (!this.isCrossPackageMethodInvocation(callSite.source(), virtualTarget)
                                        || !this.confinedClasses.contains(targetType)) {

                                    rtaVirtualTargets.add(virtualTarget);
                                    this.worklist.add(virtualTarget);
                                } else {
                                    classicRtaEdgeCount++;
                                }
                            } else {
                                rtaVirtualTargets.add(virtualTarget);
                                this.worklist.add(virtualTarget);
                            }
                        }
                    }
                    if (rtaVirtualTargets.size() > 0) {
                        this.rtaGraph.addVirtualCallSite(currentMethod, rtaVirtualTargets);
                    }
                    if(this.analyseWithConfinedClasses 
                       && rtaVirtualTargets.size() == 1 && classicRtaEdgeCount > 1) {
                        // In this case RTA combined with confined classes has discovered a new call site that can
                        // be statically resolved.
                        this.rtaGraph.addMonomorphicCallSite(currentMethod, rtaVirtualTargets);
                    }
                }
            }
        }
        return this.rtaGraph;
    }

    private void addToLiveClasses(JavaClass javaClass) {
        if (!this.liveClasses.contains(javaClass)) {
            this.liveClasses.add(javaClass);
        }
        if (javaClass.hasSuperClass()) {
            addToLiveClasses(javaClass.superClass());
        }
    }
    
    private boolean isCrossPackageMethodInvocation(JavaMethod source, JavaMethod target) {

        JavaType sourceType = source.containedIn();
        JavaType targetType = target.containedIn();

        return !sourceType.packagePath().equals(targetType.packagePath());
    }

    private void println(String format, Object... args) {
        if (this.verbose) {
            System.out.format(format + "\n", args);
        }
    }
}
