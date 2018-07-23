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


    public void printCallEdgeTable() {
        
        StringBuilder latexTable = new StringBuilder();
        double totalRtaReductionEa = 0;
        double totalRtaReductionMax = 0;
        
        latexTable.append("\\begin{table}\n");
        latexTable.append("\\begin{tabular}{ l | r r r | r r r | r r}\n");
        latexTable.append("\\hline\n");
        latexTable.append("Project & \\multicolumn{3}{c|}{Class count} & \\multicolumn{3}{c|}{Call Edges} & \\multicolumn{2}{c}{Reduction} \\\\ \n");
        latexTable.append(" & Pub. & Pkg. & Conf. & RTA & RTA\\textsubscript{EA} & RTA\\textsubscript{MAX} & RTA\\textsubscript{EA} & RTA\\textsubscript{MAX} \\\\ \n");
        latexTable.append("\\hline\n");
        for(LibraryResult libraryResult : this.libraryResults) {
            libraryResult.addToLatexTable(latexTable);
            
            totalRtaReductionEa += libraryResult.rtaReductionEa();
            totalRtaReductionMax += libraryResult.rtaReductionMax();
        }
        latexTable.append("\\hline\n");
        
        double averageReductionRtaEa = Math.round(totalRtaReductionEa / this.libraryResults.size() * 100.0) / 100.0;
        double averageReductionRtaMax = Math.round(totalRtaReductionMax / this.libraryResults.size() * 100.0) / 100.0;
        latexTable.append("\\multicolumn{7}{l|}{Average} & " + averageReductionRtaEa + "\\% & " + averageReductionRtaMax + "\\% \\\\ \n");
        
        latexTable.append("\\end{tabular}\n");
        latexTable.append("\\caption{\\label{tbl:tableEdges}Reduction of call edges from RTA\\textsubscript{EA} and RTA\\textsubscript{MAX} compared to RTA.}\n");
        latexTable.append("\\end{table}\n");
        
        System.out.println(latexTable.toString());
    }
    
    public void printMonomorphicCallSitesTable() {
        
        StringBuilder latexTable = new StringBuilder();
        
        latexTable.append("\\begin{table}\n");
        latexTable.append("\\begin{tabular}{ l | r r | r r r | r r r}\n");
        latexTable.append("\\hline\n");
        latexTable.append("Project & \\multicolumn{2}{c|}{Call Sites RTA} & \\multicolumn{3}{c|}{Call Sites RTA\\textsubscript{EA}} & \\multicolumn{3}{c}{Call Sites RTA\\textsubscript{MAX}} \\\\ \n");
        latexTable.append(" & Virtual & Mono & Virtual & Mono & New & Virtual & Mono & New \\\\ \n");
        latexTable.append("\\hline\n");
        for(LibraryResult libraryResult : this.libraryResults) {
            libraryResult.addToLatexTable2(latexTable);
        }
        latexTable.append("\\hline\n");
        latexTable.append("\\end{tabular}\n");
        latexTable.append("\\caption{\\label{tbl:monomorphicCallSites}Discovering of new monomorphic virtual call sites in RTA\\textsubscript{EA} and RTA\\textsubscript{MAX} compared to RTA.}\n");
        latexTable.append("\\end{table}\n");
        
        System.out.println(latexTable.toString());
    }
    
    public void printDeadMethodsTable() {
        StringBuilder latexTable = new StringBuilder();
        
        latexTable.append("\\begin{table}\n");
        latexTable.append("\\begin{tabular}{ l | r r r | r r r}\n");
        latexTable.append("\\hline\n");
        latexTable.append("Project & \\multicolumn{3}{c}{Method count} & \\multicolumn{3}{c}{Dead methods} \\\\ \n");
        latexTable.append(" & Total & Entry point & Compiler Gen. & RTA & RTA\\textsubscript{EA} & RTA\\textsubscript{MAX} \\\\ \n");
        latexTable.append("\\hline\n");
        for(LibraryResult libraryResult : this.libraryResults) {
            libraryResult.addToDeadMethodsTable(latexTable);
        }
        latexTable.append("\\hline\n");
        latexTable.append("\\end{tabular}\n");
        latexTable.append("\\caption{\\label{tbl:deadMethods}Discovering of new dead methods in RTA\\textsubscript{EA} and RTA\\textsubscript{MAX} compared to RTA.}\n");
        latexTable.append("\\end{table}\n");
        
        System.out.println(latexTable.toString());        
    }
    
    public void printLatexTable4() {
        
        StringBuilder latexTable = new StringBuilder();
        
        latexTable.append("\\begin{table}\n");
        latexTable.append("\\begin{tabular}{ l | r r r r r r r}\n");
        latexTable.append("\\hline\n");
        latexTable.append("Project & Class Hierachy & CHA & RTA & Escape Analysis & RTA\\textsubscript{EA} & RTA\\textsubscript{MAX} & Total \\\\ \n");
        latexTable.append("\\hline\n");
        for(LibraryResult libraryResult : this.libraryResults) {
            libraryResult.addToLatexTable4(latexTable);
        }
        latexTable.append("\\hline\n");
        latexTable.append("\\end{tabular}\n");
        latexTable.append("\\caption{\\label{tbl:calculationTime}Calculation time in seconds.}\n");
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
}