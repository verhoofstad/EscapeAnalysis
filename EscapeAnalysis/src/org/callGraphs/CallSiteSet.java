package org.callGraphs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.classHierarchy.tree.JavaMethod;

/*
 * Represents the set of all call sites in a single method.
 */
public class CallSiteSet implements Iterable<CallSite> {

    private JavaMethod source;
    private List<CallSite> callSites;

    public CallSiteSet(JavaMethod source) {
        this.source = source;
        this.callSites = new ArrayList<CallSite>();
    }

    public JavaMethod source() {
        return this.source;
    }

    public void addCallSite(CallSite callSite) {

        if (callSite.source().id().equals(this.source.id())) {
            this.callSites.add(callSite);
        } else {
            throw new Error();
        }
    }

    public int nrOfEdges() {
        int nrOfEdges = 0;
        for (CallSite callSite : this.callSites) {
            nrOfEdges += callSite.nrOfEdges();
        }
        return nrOfEdges;
    }

    public int nrOfVirtualCallSites() {
        int nrOfCallSites = 0;
        for (CallSite callSite : this.callSites) {
            if (callSite.isVirtual()) {
                nrOfCallSites += 1;
            }
        }
        return nrOfCallSites;
    }

    public int nrOfVirtualEmptyCallSites() {
        int nrOfCallSites = 0;
        for (CallSite callSite : this.callSites) {
            if (callSite.isVirtual() && callSite.hasNoTargets()) {
                nrOfCallSites += 1;
            }
        }
        return nrOfCallSites;
    }

    public int nrOfVirtualMonoCallSites() {
        int nrOfCallSites = 0;
        for (CallSite callSite : this.callSites) {
            if (callSite.isVirtual() && callSite.isMonomorphic()) {
                nrOfCallSites += 1;
            }
        }
        return nrOfCallSites;
    }

    public int nrOfStaticCallSites() {
        int nrOfEdges = 0;
        for (CallSite callSite : this.callSites) {
            if (callSite.isStatic()) {
                nrOfEdges += 1;
            }
        }
        return nrOfEdges;
    }

    public int size() {
        return this.callSites.size();
    }

    @Override
    public Iterator<CallSite> iterator() {
        return this.callSites.iterator();
    }
}
