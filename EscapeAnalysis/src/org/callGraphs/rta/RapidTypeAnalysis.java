package org.callGraphs.rta;

import org.callGraphs.CallGraph;
import org.callGraphs.CallSite;
import org.callGraphs.CallSiteSet;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;
import org.classHierarchy.tree.JavaTypeSet;

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

    public RapidTypeAnalysis(CallGraph chaGraph) {

        this.chaGraph = chaGraph;
        this.rtaGraph = new CallGraph();
        this.confinedClasses = new JavaTypeSet();
    }

    public CallGraph callGraph() {
        return this.rtaGraph;
    }

    public void setLibraryAnalysis(JavaTypeSet exportedClasses, JavaMethodSet exportedMethods) {

        this.liveClasses = exportedClasses;
        this.worklist = new Worklist(exportedMethods);

        println("Initializing RTA for library analysis with %s exported classes (live) and %s exported methods (worklist).",
                exportedClasses.size(), exportedMethods.size());
    }

    public void setConfinedClasses(JavaTypeSet confinedClasses) {
        this.confinedClasses = confinedClasses;
    }

    public void analyse() {

        while (!this.worklist.isEmpty()) {

            JavaMethod currentMethod = this.worklist.getItem();

            CallSiteSet callSites = this.chaGraph.getCallSites(currentMethod);

            for (CallSite callSite : callSites) {
                // Find all instantiated classes in this method.
                if (callSite.isConstructor()) {
                    JavaClass instantiatedClass = callSite.getInstantiatedClass();
                    if (!this.liveClasses.contains(instantiatedClass)) {
                        this.liveClasses.add(instantiatedClass);
                    }
                }

                if (callSite.isStatic()) {

                    JavaMethod staticTarget = callSite.targets().getRandom();

                    this.rtaGraph.addStaticCallSite(currentMethod, staticTarget);
                    this.worklist.add(staticTarget);
                } else {
                    JavaMethodSet rtaVirtualTargets = new JavaMethodSet();

                    for (JavaMethod virtualTarget : callSite.targets()) {
                        JavaType targetType = virtualTarget.containedIn();

                        if (this.liveClasses.contains(targetType.id())) {

                            if (!this.isCrossPackageMethodInvocation(callSite.source(), virtualTarget)
                                    || !this.confinedClasses.contains(targetType)) {

                                rtaVirtualTargets.add(virtualTarget);
                                this.worklist.add(virtualTarget);
                            }
                        }
                    }
                    if (rtaVirtualTargets.size() > 0) {
                        this.rtaGraph.addVirtualCallSite(currentMethod, rtaVirtualTargets);
                    }
                }
            }
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
