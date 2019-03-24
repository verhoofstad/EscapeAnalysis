package org.results;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.Environment;

public class LibraryResultSet {
    List<LibraryResult> libraryResults = new ArrayList<LibraryResult>();

    public void add(LibraryResult libraryResult) {
        this.libraryResults.add(libraryResult);
    }


    public void printCallEdgeTable(String label) {
        
        StringBuilder latexTable = new StringBuilder();
        
        double totalPublicClassPercentage = 0;
        double totalPackagePrivateClassPercentage = 0;
        double totalConfinedClassPercentage = 0;
        double totalRtaReductionEa = 0;
        double totalRtaReductionMax = 0;
        
        latexTable.append("\\begin{table}\n");
        latexTable.append("\\begin{tabular}{ l | r r r | r r r | r r}\n");
        latexTable.append("\\hline\n");
        latexTable.append("Project & \\multicolumn{3}{c|}{Class count} & \\multicolumn{3}{c|}{Call Edges} & \\multicolumn{2}{c}{Reduction} \\\\ \n");
        latexTable.append(" & Pub. & Pkg. & Conf. & RTA & CCA\\textsubscript{EA} & CCA\\textsubscript{MAX} & CCA\\textsubscript{EA} & CCA\\textsubscript{MAX} \\\\ \n");
        latexTable.append("\\hline\n");
        for(LibraryResult libraryResult : this.libraryResults) {
            libraryResult.addToCallEdgeTable(latexTable);
            
            totalPublicClassPercentage += libraryResult.libraryPublicClassPercentage();
            totalPackagePrivateClassPercentage += libraryResult.libraryPackagePrivateClassPercentage();
            totalConfinedClassPercentage += libraryResult.libraryConfinedClassPercentage();
            totalRtaReductionEa += libraryResult.rtaReductionEa();
            totalRtaReductionMax += libraryResult.rtaReductionMax();
        }
        latexTable.append("\\hline\n");
        
        int nrOfLibraries = this.libraryResults.size();
        double averagePublicClassCount = roundToTwoDecimals(totalPublicClassPercentage / nrOfLibraries);
        double averagePackagePrivateClassCount = roundToTwoDecimals(totalPackagePrivateClassPercentage / nrOfLibraries);
        double averageConfinedClassCount = roundToTwoDecimals(totalConfinedClassPercentage / nrOfLibraries);
        double averageReductionRtaEa = roundToTwoDecimals(totalRtaReductionEa / nrOfLibraries);
        double averageReductionRtaMax = roundToTwoDecimals(totalRtaReductionMax / nrOfLibraries);
        
        latexTable.append("Average & " + averagePublicClassCount + "\\% & " + averagePackagePrivateClassCount + "\\% & ");
        latexTable.append(averageConfinedClassCount + "\\% & & & & " + averageReductionRtaEa + "\\% & " + averageReductionRtaMax + "\\% \\\\ \n");
        
        latexTable.append("\\end{tabular}\n");
        latexTable.append("\\caption{\\label{tbl:" + label + "}Reduction of call edges from CCA\\textsubscript{EA} and CCA\\textsubscript{MAX} compared to RTA.}\n");
        latexTable.append("\\end{table}\n");
        
        System.out.println(latexTable.toString());
    }
    
    public void printPackagePrivateClassDistribution(String label) {
        
        StringBuilder latexTable = new StringBuilder();
        
        latexTable.append("\\begin{table}\n");
        latexTable.append("\\begin{tabular}{ l | r r | r r | r r }\n");
        latexTable.append("\\hline\n");
        latexTable.append("Project & \\multicolumn{2}{c|}{Classes} & \\multicolumn{2}{c|}{Extending} & \\multicolumn{2}{c}{Overriding Methods} \\\\ \n");
        latexTable.append(" & Pub. & Pkg. & Object & Other & Object & Other \\\\ \n");
        latexTable.append("\\hline\n");
        for(LibraryResult libraryResult : this.libraryResults) {
            libraryResult.addToPackagePrivateClassDistribution(latexTable);
        }
        latexTable.append("\\hline\n");
        latexTable.append("\\end{tabular}\n");
        latexTable.append("\\caption{\\label{tbl:" + label + "}Discovering of new monomorphic virtual call sites in CCA\\textsubscript{EA} and CCA\\textsubscript{MAX} compared to RTA.}\n");
        latexTable.append("\\end{table}\n");
        
        System.out.println(latexTable.toString());        
    }
    
    public void printMonomorphicCallSitesTable(String label) {
        
        StringBuilder latexTable = new StringBuilder();
        
        latexTable.append("\\begin{table}\n");
        latexTable.append("\\begin{tabular}{ l | r r | r r r r | r r r r }\n");
        latexTable.append("\\hline\n");
        latexTable.append("Project & \\multicolumn{2}{c|}{Call Sites RTA} & \\multicolumn{4}{c|}{Call Sites CCA\\textsubscript{EA}} & \\multicolumn{4}{c}{Call Sites CCA\\textsubscript{MAX}} \\\\ \n");
        latexTable.append(" & Virtual & Mono & Virtual & Mono & New & \\% & Virtual & Mono & New & \\% \\\\ \n");
        latexTable.append("\\hline\n");
        for(LibraryResult libraryResult : this.libraryResults) {
            libraryResult.addToMonomorphicCallSitesTable(latexTable);
        }
        latexTable.append("\\hline\n");
        latexTable.append("\\end{tabular}\n");
        latexTable.append("\\caption{\\label{tbl:" + label + "}Discovering of new monomorphic virtual call sites in CCA\\textsubscript{EA} and CCA\\textsubscript{MAX} compared to RTA.}\n");
        latexTable.append("\\end{table}\n");
        
        System.out.println(latexTable.toString());
    }
    
    public void printDeadMethodsTable(String label) {
        StringBuilder latexTable = new StringBuilder();
        
        latexTable.append("\\begin{table}\n");
        latexTable.append("\\begin{tabular}{ l | r r r | r r r}\n");
        latexTable.append("\\hline\n");
        latexTable.append("Project & \\multicolumn{3}{c|}{Method count} & \\multicolumn{3}{c}{Dead methods} \\\\ \n");
        latexTable.append(" & Total & Entry point & Compiler Gen. & RTA & CCA\\textsubscript{EA} & CCA\\textsubscript{MAX} \\\\ \n");
        latexTable.append("\\hline\n");
        for(LibraryResult libraryResult : this.libraryResults) {
            libraryResult.addToDeadMethodsTable(latexTable);
        }
        latexTable.append("\\hline\n");
        latexTable.append("\\end{tabular}\n");
        latexTable.append("\\caption{\\label{tbl:" + label + "}Discovering of new dead methods in CCA\\textsubscript{EA} and CCA\\textsubscript{MAX} compared to RTA.}\n");
        latexTable.append("\\end{table}\n");
        
        System.out.println(latexTable.toString());        
    }
    
    public void printEntryPointTable(String label) {
        
        StringBuilder latexTable = new StringBuilder();
        
        latexTable.append("\\begin{table}\n");
        latexTable.append("\\begin{tabular}{ l | r | r r | r r | r r }\n");
        latexTable.append("\\hline\n");
        latexTable.append("Project & RTA & \\multicolumn{2}{c|}{Old} & \\multicolumn{2}{c|}{OPA} & \\multicolumn{2}{c}{CPA} \\\\ \n");
        latexTable.append(" & & Calc. & Reif. & Calc. & Reif. & Calc. & Reif. \\\\ \n");
        latexTable.append("\\hline\n");
        for(LibraryResult libraryResult : this.libraryResults) {
            libraryResult.addToEntryPointTable(latexTable);
        }
        latexTable.append("\\hline\n");
        latexTable.append("\\end{tabular}\n");
        latexTable.append("\\caption{\\label{tbl:" + label + "}Entry point calculation.}\n");
        latexTable.append("\\end{table}\n");
        
        System.out.println(latexTable.toString());
    }
    
    public File createResultsFile() {
        
        List<String> headers = new ArrayList<String>();
        headers.add("id");
        headers.add("organisation");
        headers.add("name");
        headers.add("revision");
        
        headers.add("all_classCount");
        headers.add("all_packageVisibleClassCount");
        headers.add("libraries_classCount");
        headers.add("libraries_packageVisibleClassCount");
        
        headers.add("confinedClassCount");

        headers.add("rta_entryPoints");
        headers.add("chaCpa_entryPoints");
        headers.add("old_entryPoints");

        headers.add("cha_edgeCount");
        headers.add("cha_callSiteCount");
        headers.add("cha_virtualCallSiteCount");
        headers.add("cha_staticCallSiteCount");

        headers.add("rta_edgeCount");
        headers.add("rta_callSiteCount");
        headers.add("rta_virtualCallSiteCount");
        headers.add("rta_staticCallSiteCount");

        headers.add("rtaEA_edgeCount");
        headers.add("rtaEA_callSiteCount");
        headers.add("rtaEA_virtualCallSiteCount");
        headers.add("rtaEA_staticCallSiteCount");
        headers.add("rtaEA_newMonoMorphicCallSiteCount");

        headers.add("rtaEAMax_edgeCount");
        headers.add("rtaEAMax_callSiteCount");
        headers.add("rtaEAMax_virtualCallSiteCount");
        headers.add("rtaEAMax_staticCallSiteCount");
        headers.add("rtaEAMax_newMonoMorphicCallSiteCount");

        List<String> header = new ArrayList<String>();
        header.add(String.join(";", headers));

        File resultsFile = new File(Environment.resultFile);
        try {
            Files.write(resultsFile.toPath(), header, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return resultsFile;
    }
    
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
