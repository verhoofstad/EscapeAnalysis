package org.callGraphs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.classHierarchy.tree.JavaMethod;

/**
 * Represents the set of all call sites in a single method.
 */
public class CallSiteSet implements Iterable<CallSite> {

    private JavaMethod source;
    private List<CallSite> callSites;
    
    private int nrOfEdges = 0;
    private int nrOfVirtualCallSites = 0;
    private int nrOfMonomorphicCallSites = 0;
    private int nrOfStaticCallSites = 0;
    private int nrOfEmptyCallSites = 0;

    public CallSiteSet(JavaMethod source) {
        this.source = source;
        this.callSites = new ArrayList<CallSite>();
    }

    public JavaMethod source() {
        return this.source;
    }

    public void addCallSite(CallSite callSite) {

        if(!callSite.source().id().equals(this.source.id())) { 
            throw new IllegalArgumentException("Parameter 'callSite' has not the same source as the current call site set."); 
        }
        
        this.callSites.add(callSite);
        this.nrOfEdges += callSite.nrOfEdges();
        this.nrOfVirtualCallSites += callSite.isVirtual() ? 1 : 0;
        this.nrOfMonomorphicCallSites += callSite.isVirtual() && callSite.isMonomorphic() ? 1 : 0;
        this.nrOfStaticCallSites += callSite.isStatic() ? 1 : 0;
        this.nrOfEmptyCallSites += callSite.hasNoTargets() ? 1 : 0;
    }

    public int nrOfEdges() {
        return this.nrOfEdges;
    }

    public int nrOfVirtualCallSites() {
        return this.nrOfVirtualCallSites;
    }

    public int nrOfMonomorphicCallSites() {
        return this.nrOfMonomorphicCallSites;
    }

    public int nrOfStaticCallSites() {
        return this.nrOfStaticCallSites;
    }

    public int nrOfVirtualEmptyCallSites() {
        return this.nrOfEmptyCallSites;
    }
    
    public int size() {
        return this.callSites.size();
    }

    @Override
    public Iterator<CallSite> iterator() {
        return this.callSites.iterator();
    }
}
