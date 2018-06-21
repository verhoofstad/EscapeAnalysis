package org.callGraphs;

import java.util.HashMap;
import java.util.Map;

import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;

/**
 * Represents a call graph.
 *
 */
public class CallGraph {

    private Map<String, CallSiteSet> callSites = new HashMap<String, CallSiteSet>();
    public int newMonoMorphicCallSites = 0;

    public void addStaticCallSite(JavaMethod source, JavaMethod target) {
        this.addCallSite(new CallSite(source, target));
    }

    public void addVirtualCallSite(JavaMethod source, JavaMethodSet virtualTargets) {
        this.addCallSite(new CallSite(source, virtualTargets));
    }

    private void addCallSite(CallSite callSite) {
        String sourceId = callSite.source().id();
        if (!this.callSites.containsKey(sourceId)) {
            this.callSites.put(sourceId, new CallSiteSet(callSite.source()));
        }

        this.callSites.get(sourceId).addCallSite(callSite);
    }

    /**
     * Gets all call sites for a given method.
     * 
     * Remark: A method can have zero or more call sites.
     */
    public CallSiteSet getCallSites(JavaMethod javaMethod) {

        if (this.callSites.containsKey(javaMethod.id())) {
            return this.callSites.get(javaMethod.id());
        } else {
            return new CallSiteSet(javaMethod);
        }
    }

    public int nrOfEdges() {
        int nrOfEdges = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            nrOfEdges += callSiteSet.nrOfEdges();
        }
        return nrOfEdges;
    }

    public int nrOfCallSites() {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            nrOfCallSites += callSiteSet.size();
        }
        return nrOfCallSites;
    }

    public int nrOfVirtualCallSites() {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            nrOfCallSites += callSiteSet.nrOfVirtualCallSites();
        }
        return nrOfCallSites;
    }

    public int nrOfVirtualMonoCallSites() {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            nrOfCallSites += callSiteSet.nrOfVirtualMonoCallSites();
        }
        return nrOfCallSites;
    }

    public int nrOfVirtualEmptyCallSites() {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            nrOfCallSites += callSiteSet.nrOfVirtualEmptyCallSites();
        }
        return nrOfCallSites;
    }

    public int nrOfStaticCallSites() {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            nrOfCallSites += callSiteSet.nrOfStaticCallSites();
        }
        return nrOfCallSites;
    }
}
