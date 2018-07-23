package org.results;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.callGraphs.CallGraph;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaTypeSet;
import org.counting.CountResults;
import org.dataSets.Library;

/**
 * Contains the results obtained by the Libraryanalyser.
 */
public class LibraryResult {

    private Library library;

    public int libraryPublicClassCount = 0;
    public int libraryPackagePrivateClassCount = 0;
    public int libraryConfinedClassCount = 0;
    
    public int libraryConcreteMethodCount = 0;
    public int libraryEntryPointMethodCount = 0;
    public int libraryCompilerMethodCount = 0;
    
    public int rtaEdgeCount = 0;
    public int rtaEaEdgeCount = 0;
    public int rtaMaxEdgeCount = 0;
    
    public int rtaCallSiteCount = 0;
    public int rtaVirtualCallSiteCount = 0;
    public int rtaMonomorphicCallSiteCount = 0; // Virtual call sites that resolve to one target.
    public int rtaEaCallSiteCount = 0;
    public int rtaEaVirtualCallSiteCount = 0;
    public int rtaEaMonomorphicCallSiteCount = 0;
    public int rtaEaNewMonomorphicCallSiteCount = 0;
    public int rtaMaxCallSiteCount = 0;
    public int rtaMaxVirtualCallSiteCount = 0;
    public int rtaMaxMonomorphicCallSiteCount = 0;
    public int rtaMaxNewMonomorphicCallSiteCount = 0;
    
    public int rtaDeadMethods = 0;
    public int rtaEaDeadMethods = 0;
    public int rtaMaxDeadMethods = 0;

    public long classHierarchyBuildTime = 0;
    public long chaBuildTime = 0;
    public long rtaBuildTime = 0;
    public long escapeAnalysisTime = 0;
    public long rtaEaTime = 0;
    public long rtaMaxTime = 0;
    public long totalAnalysisTime = 0;
    
    private double rtaReductionEa = 0;
    private double rtaReductionMax = 0;
    
    
    
    public LibraryResult(Library library) {
        
        this.library = library;
    }
    
    public String name() {
        return this.library.name();
    }
    
    public double rtaReductionEa() {
        return this.rtaReductionEa;
    }
    
    public double rtaReductionMax() {
        return this.rtaReductionMax;
    }
    
    private void calculate() {
        this.rtaReductionEa = Math.round((double)(this.rtaEdgeCount - this.rtaEaEdgeCount) / this.rtaEdgeCount * 100 * 100.0) / 100.0;
        this.rtaReductionMax = Math.round((double)(this.rtaEdgeCount - this.rtaMaxEdgeCount) / this.rtaEdgeCount * 100 * 100.0) / 100.0;
    }
    
    
    void addToLatexTable(StringBuilder latexTable) {
        
        this.calculate();
        
        latexTable.append(this.library.name());
        latexTable.append(" & ");
        latexTable.append(this.libraryPublicClassCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryPackagePrivateClassCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryConfinedClassCount);
        latexTable.append(" & ");

        latexTable.append(this.rtaEdgeCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaEaEdgeCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaMaxEdgeCount);
        
        latexTable.append(" & ");
        latexTable.append(this.rtaReductionEa + "\\%");
        latexTable.append(" & ");
        latexTable.append(this.rtaReductionMax + "\\%");
        
        
        latexTable.append("\\\\");
        latexTable.append("\n");
    }
    
    void addToLatexTable2(StringBuilder latexTable) {
        
        latexTable.append(this.library.name());
        latexTable.append(" & ");

        latexTable.append(this.rtaVirtualCallSiteCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaMonomorphicCallSiteCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaEaVirtualCallSiteCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaEaMonomorphicCallSiteCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaEaNewMonomorphicCallSiteCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaMaxVirtualCallSiteCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaMaxMonomorphicCallSiteCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaMaxNewMonomorphicCallSiteCount);
        
        latexTable.append("\\\\");
        latexTable.append("\n");
    }
    
    void addToDeadMethodsTable(StringBuilder latexTable) {
        
        int additionalRtaEa = this.rtaEaDeadMethods - this.rtaDeadMethods;
        int additionalrtaMax = this.rtaMaxDeadMethods - this.rtaDeadMethods;
        
        latexTable.append(this.library.name());
        latexTable.append(" & ");
        
        latexTable.append(this.libraryConcreteMethodCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryEntryPointMethodCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryCompilerMethodCount);
        latexTable.append(" & ");
        
        latexTable.append(this.rtaDeadMethods);
        latexTable.append(" & ");
        latexTable.append(additionalRtaEa == 0 ? "0" : "+" + additionalRtaEa);
        latexTable.append(" & ");
        latexTable.append(additionalrtaMax == 0 ? "0" : "+" + additionalrtaMax);

        latexTable.append("\\\\");
        latexTable.append("\n");
    }
    
    void addToLatexTable4(StringBuilder latexTable) {
        
        latexTable.append(this.library.name());
        latexTable.append(" & ");

        latexTable.append(formatNanoTime(this.classHierarchyBuildTime));
        latexTable.append(" & ");
        latexTable.append(formatNanoTime(this.chaBuildTime));
        latexTable.append(" & ");
        latexTable.append(formatNanoTime(this.rtaBuildTime));
        latexTable.append(" & ");
        latexTable.append(formatNanoTime(this.escapeAnalysisTime));
        latexTable.append(" & ");
        latexTable.append(formatNanoTime(this.rtaEaTime));
        latexTable.append(" & ");
        latexTable.append(formatNanoTime(this.rtaMaxTime));
        latexTable.append(" & ");
        latexTable.append(formatNanoTime(this.totalAnalysisTime));
        
        latexTable.append("\\\\");
        latexTable.append("\n");
    }
    
    private String formatNanoTime(long nanoTime) {
        
        DecimalFormat formatter = new DecimalFormat("#.00");
        return formatter.format((double)nanoTime / 1000 / 1000 / 1000);
    }
    
    private void printToFile(File file, CountResults totalCounts, CountResults libraryCounts, LibraryResult chaCpaResult, JavaMethodSet entryPoints, 
            JavaTypeSet confinedClasses, CallGraph chaGraph, CallGraph rtaGraph, CallGraph rtaGraphEA, CallGraph rtaGraphEAMax) {
        
        List<String> fields = new ArrayList<String>();
        fields.add("" + this.library.id()); 
        fields.add(this.library.organisation());
        fields.add(library.name());
        fields.add(library.revision());
        
        fields.add("" + totalCounts.classCount);
        fields.add("" + totalCounts.packagePrivateClassCount);
        fields.add("" + libraryCounts.classCount);
        fields.add("" + libraryCounts.packagePrivateClassCount);
        
        fields.add("" + confinedClasses.size());

        fields.add("" + entryPoints.size());
        //fields.add("" + chaCpaResult.cpa_entryPoints);
        //fields.add("" + chaCpaResult.old_entryPoints);

        fields.add("" + chaGraph.nrOfEdges());
        fields.add("" + chaGraph.nrOfCallSites());
        fields.add("" + chaGraph.nrOfVirtualCallSites());
        fields.add("" + chaGraph.nrOfStaticCallSites());

        fields.add("" + rtaGraph.nrOfEdges());
        fields.add("" + rtaGraph.nrOfCallSites());
        fields.add("" + rtaGraph.nrOfVirtualCallSites());
        fields.add("" + rtaGraph.nrOfStaticCallSites());

        fields.add("" + rtaGraphEA.nrOfEdges());
        fields.add("" + rtaGraphEA.nrOfCallSites());
        fields.add("" + rtaGraphEA.nrOfVirtualCallSites());
        fields.add("" + rtaGraphEA.nrOfStaticCallSites());
        fields.add("" + rtaGraphEA.nrOfNewMonomorphicCallSites());

        fields.add("" + rtaGraphEAMax.nrOfEdges());
        fields.add("" + rtaGraphEAMax.nrOfCallSites());
        fields.add("" + rtaGraphEAMax.nrOfVirtualCallSites());
        fields.add("" + rtaGraphEAMax.nrOfStaticCallSites());
        fields.add("" + rtaGraphEAMax.nrOfNewMonomorphicCallSites());

        List<String> line = new ArrayList<String>();
        line.add(String.join(";", fields));
        
        try {
            Files.write(file.toPath(), line, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
