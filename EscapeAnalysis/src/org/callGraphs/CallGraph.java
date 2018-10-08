package org.callGraphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.asm.JarFile;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;

/**
 * Represents a call graph.
 */
public class CallGraph {

    private Map<String, CallSiteSet> callSites = new HashMap<String, CallSiteSet>();
    
    private List<CallSite> newMonomorphicCallSites = new ArrayList<CallSite>();
    private JavaMethodSet invokedMethods = new JavaMethodSet();

    public void addStaticCallSite(JavaMethod source, JavaMethod target) {
        if (source == null) { throw new IllegalArgumentException("Parameter 'source' should not be null."); }
        if (target == null) { throw new IllegalArgumentException("Parameter 'target' should not be null."); }

        CallSite staticCallSite = new CallSite(source, target);
        
        this.addCallSite(staticCallSite);
        this.addInvokedMethods(staticCallSite);
    }

    public void addVirtualCallSite(JavaMethod source, JavaMethodSet virtualTargets) {
        if (source == null) { throw new IllegalArgumentException("Parameter 'source' should not be null."); }
        if (virtualTargets == null) { throw new IllegalArgumentException("Parameter 'virtualTargets' should not be null."); }

        CallSite virtualCallSite = new CallSite(source, virtualTargets);
        
        this.addCallSite(virtualCallSite);
        this.addInvokedMethods(virtualCallSite);
    }

    public void addMonomorphicCallSite(JavaMethod source, JavaMethodSet virtualTargets) {
        if (source == null) { throw new IllegalArgumentException("Parameter 'source' should not be null."); }
        if (virtualTargets == null) { throw new IllegalArgumentException("Parameter 'virtualTargets' should not be null."); }
        if (virtualTargets.size() != 1) { throw new IllegalArgumentException("Parameter 'virtualTargets' should contain exactly one target method."); }
        
        CallSite monomorphicCallSite = new CallSite(source, virtualTargets);
        
        this.newMonomorphicCallSites.add(monomorphicCallSite);
        this.addInvokedMethods(monomorphicCallSite);
    }
    
    private void addCallSite(CallSite callSite) {
        String sourceId = callSite.source().id();
        if (!this.callSites.containsKey(sourceId)) {
            this.callSites.put(sourceId, new CallSiteSet(callSite.source()));
        }

        this.callSites.get(sourceId).addCallSite(callSite);
    }
    
    private void addInvokedMethods(CallSite callSite) {
        for(JavaMethod invokedMethod : callSite.targets()) {
            if(!this.invokedMethods.contains(invokedMethod)) {
                this.invokedMethods.add(invokedMethod);
            }
        }        
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

    /**
     * Gets the total number of edges in the call graph.
     */
    public int nrOfEdges() {
        int nrOfEdges = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            nrOfEdges += callSiteSet.nrOfEdges();
        }
        return nrOfEdges;
    }

    /**
     * Gets the number of edges that originated from a method in a given JAR-file.
     */
    public int nrOfEdges(JarFile fromSource) {
        int nrOfEdges = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            if(callSiteSet.source().jarFile().equals(fromSource)) {
                nrOfEdges += callSiteSet.nrOfEdges();
            }
        }
        return nrOfEdges;
    }

    /**
     * Gets the total number of call sites in the call graph.
     */
    public int nrOfCallSites() {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            nrOfCallSites += callSiteSet.size();
        }
        return nrOfCallSites;
    }

    /**
     * Gets the number of call sites in a given JAR-file.
     */
    public int nrOfCallSites(JarFile fromSource) {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            if(callSiteSet.source().jarFile().equals(fromSource)) {
                nrOfCallSites += callSiteSet.size();
            }
        }
        return nrOfCallSites;
    }

    /**
     * Gets the total number of virtual call sites in the call graph.
     */
    public int nrOfVirtualCallSites() {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            nrOfCallSites += callSiteSet.nrOfVirtualCallSites();
        }
        return nrOfCallSites;
    }

    /**
     * Gets the number of virtual call sites in a given JAR-file.
     */
    public int nrOfVirtualCallSites(JarFile fromSource) {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            if(callSiteSet.source().jarFile().equals(fromSource)) {
                nrOfCallSites += callSiteSet.nrOfVirtualCallSites();
            }
        }
        return nrOfCallSites;
    }
    
    /**
     * Gets the total number of virtual call sites in the call graph which resolve to one target method.
     */
    public int nrOfMonomorphicCallSites() {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            nrOfCallSites += callSiteSet.nrOfMonomorphicCallSites();
        }
        return nrOfCallSites;
    }

    /**
     * Gets the number of virtual call sites in a given JAR-file which resolve to one target method.
     */
    public int nrOfMonomorphicCallSites(JarFile fromSource) {
        int nrOfCallSites = 0;
        for (CallSiteSet callSiteSet : this.callSites.values()) {
            if(callSiteSet.source().jarFile().equals(fromSource)) {
                nrOfCallSites += callSiteSet.nrOfMonomorphicCallSites();
            }
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
    
    public int nrOfNewMonomorphicCallSites() {
        return this.newMonomorphicCallSites.size();
    }
    
    public int nrOfNewMonomorphicCallSites(JarFile fromSource) {
        int nrOfNewMonomorphicCallSites = 0;
        for(CallSite callSite : this.newMonomorphicCallSites) {
            if(callSite.source().jarFile().equals(fromSource)) {
                nrOfNewMonomorphicCallSites += 1;
            }
        }
        return nrOfNewMonomorphicCallSites;
    }
    

    /**
     * Returns the set of methods that have never been invoked.
     * Because the call graph never contains edges to abstract methods, only concrete methods are considered.
     * @param classHierarchy The class hierarchy that contains all methods.
     * @param entryPoints The set of entry point methods. These will never be considered dead methods.
     * @param jarFile 
     * @return The set of methods that have never been invoked.
     */
    public JavaMethodSet getDeadMethods(ClassHierarchy classHierarchy, JavaMethodSet entryPoints, JarFile jarFile) {
        
        JavaMethodSet methodsInJarFile = classHierarchy.getMethods(jarFile);
        JavaMethodSet compilerGenerated = classHierarchy.getCompilerGeneratedMethods(jarFile);
        
        return methodsInJarFile.difference(this.invokedMethods).difference(entryPoints).difference(compilerGenerated);
    }
}
