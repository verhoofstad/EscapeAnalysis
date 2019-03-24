package org.results;

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
    
    public int libraryRtaEntryPointCount = 0;
    public int libraryOldEntryPointCount = 0;
    public int libraryOpaEntryPointCount = 0;
    public int libraryCpaEntryPointCount = 0;
    public int libraryReifOldEntryPointCount = 0;
    public int libraryReifOpaEntryPointCount = 0;
    public int libraryReifCpaEntryPointCount = 0;
    
    public int libraryPackagePrivateClassInheritFromObjectCount = 0;
    public int libraryPackagePrivateClassInheritFromOtherCount = 0;
    public int libraryPackagePrivateClassOverridingMethodCount = 0;
    public int libraryPackagePrivateClassOverridingObjectMethodCount = 0;
    
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
    
    private double libraryPublicClassPercentage = 0;
    private double libraryPackagePrivateClassPercentage = 0;
    private double libraryConfinedClassPercentage = 0;
    
    private double rtaReductionEa = 0;
    private double rtaReductionMax = 0;
    
    private double rtaEaNewMonomorphicCallSitePercentage = 0;
    private double rtaMaxNewMonomorphicCallSitePercentage = 0;
    
    private double rtaEaDeadMethodsPercentage = 0;
    private double rtaMaxDeadMethodsPercentage = 0;
    
    public LibraryResult(Library library) {
        
        this.library = library;
    }
    
    public String name() {
        return this.library.name();
    }
    
    public double libraryPublicClassPercentage() {
        return this.libraryPublicClassPercentage;
    }
    
    public double libraryPackagePrivateClassPercentage() {
        return this.libraryPackagePrivateClassPercentage;
    }
    
    public double libraryConfinedClassPercentage() {
        return this.libraryConfinedClassPercentage;
    }
    
    public double rtaReductionEa() {
        return this.rtaReductionEa;
    }
    
    public double rtaReductionMax() {
        return this.rtaReductionMax;
    }
    
    private void calculate() {
        int totalLibraryClassCount = this.libraryPublicClassCount + this.libraryPackagePrivateClassCount;
        this.libraryPublicClassPercentage = (double)this.libraryPublicClassCount / totalLibraryClassCount * 100;
        this.libraryPackagePrivateClassPercentage = (double)this.libraryPackagePrivateClassCount / totalLibraryClassCount * 100;
        this.libraryConfinedClassPercentage = (double)this.libraryConfinedClassCount / totalLibraryClassCount * 100;
        this.rtaReductionEa = (double)(this.rtaEdgeCount - this.rtaEaEdgeCount) / this.rtaEdgeCount * 100;
        this.rtaReductionMax = (double)(this.rtaEdgeCount - this.rtaMaxEdgeCount) / this.rtaEdgeCount * 100;
        
        if (this.rtaEaMonomorphicCallSiteCount != 0) {
            this.rtaEaNewMonomorphicCallSitePercentage = (double)(this.rtaEaNewMonomorphicCallSiteCount / this.rtaEaMonomorphicCallSiteCount) * 100;
        }
        if (this.rtaMaxMonomorphicCallSiteCount != 0) {
            this.rtaMaxNewMonomorphicCallSitePercentage = (double)(this.rtaMaxNewMonomorphicCallSiteCount / this.rtaMaxMonomorphicCallSiteCount) * 100;
        }
        
        this.rtaEaDeadMethodsPercentage = (double)(this.rtaEaDeadMethods - this.rtaDeadMethods) / this.rtaDeadMethods * 100;
        this.rtaMaxDeadMethodsPercentage = (double)(this.rtaMaxDeadMethods - this.rtaDeadMethods) / this.rtaDeadMethods * 100;
    }
    
    
    void addToCallEdgeTable(StringBuilder latexTable) {
        
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
        latexTable.append(roundToTwoDecimals(this.rtaReductionEa) + "\\%");
        latexTable.append(" & ");
        latexTable.append(roundToTwoDecimals(this.rtaReductionMax) + "\\%");
        
        latexTable.append("\\\\");
        latexTable.append("\n");
    }
    
    void addToPackagePrivateClassDistribution(StringBuilder latexTable) {
        
        latexTable.append(this.library.name());
        latexTable.append(" & ");

        latexTable.append(this.libraryPublicClassCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryPackagePrivateClassCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryPackagePrivateClassInheritFromObjectCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryPackagePrivateClassInheritFromOtherCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryPackagePrivateClassOverridingObjectMethodCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryPackagePrivateClassOverridingMethodCount);
        
        latexTable.append("\\\\");
        latexTable.append("\n");       
    }
    
    void addToMonomorphicCallSitesTable(StringBuilder latexTable) {
        
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
        latexTable.append(roundToThreeDecimals(this.rtaEaNewMonomorphicCallSitePercentage) + "\\%");
        latexTable.append(" & ");
        
        latexTable.append(this.rtaMaxVirtualCallSiteCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaMaxMonomorphicCallSiteCount);
        latexTable.append(" & ");
        latexTable.append(this.rtaMaxNewMonomorphicCallSiteCount);
        latexTable.append(" & ");
        latexTable.append(roundToThreeDecimals(this.rtaMaxNewMonomorphicCallSitePercentage) + "\\%");
        
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
        latexTable.append(additionalRtaEa == 0 ? "0\\%" : roundToTwoDecimals(this.rtaEaDeadMethodsPercentage) + "\\%");
        latexTable.append(" & ");
        latexTable.append(additionalrtaMax == 0 ? "0\\%" : roundToTwoDecimals(this.rtaMaxDeadMethodsPercentage) + "\\%");

        latexTable.append("\\\\");
        latexTable.append("\n");
    }
    
    void addToEntryPointTable(StringBuilder latexTable) {
        latexTable.append(this.library.name());
        latexTable.append(" & ");

        latexTable.append(this.libraryRtaEntryPointCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryOldEntryPointCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryReifOldEntryPointCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryOpaEntryPointCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryReifOpaEntryPointCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryCpaEntryPointCount);
        latexTable.append(" & ");
        latexTable.append(this.libraryReifCpaEntryPointCount);
        
        latexTable.append("\\\\");
        latexTable.append("\n");        
    }
    
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    
    private double roundToThreeDecimals(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
    
    /*
    private void printToFile(File file, CountResults totalCounts, CountResults libraryCounts, LibraryResult chaCpaResult, JavaMethodSet entryPoints, 
            JavaTypeSet confinedClasses, CallGraph chaGraph, CallGraph rtaGraph, CallGraph rtaGraphEA, CallGraph rtaGraphEAMax) {
        
        List<String> fields = new ArrayList<String>();
        fields.add("" + this.library.id()); 
        fields.add(this.library.organisation());
        fields.add(library.name());
        fields.add(library.revision());
        
        fields.add("" + totalCounts.all_classCount);
        fields.add("" + totalCounts.all_packageVisibleClassCount);
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
    */
}
