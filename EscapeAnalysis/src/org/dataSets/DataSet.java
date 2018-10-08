package org.dataSets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.Environment;
import org.asm.JarFile;
import org.asm.JarFileSet;

public class DataSet implements Iterable<Library> {

    private List<Library> libraries = new ArrayList<Library>();

    private void addLibrary(Library library) {
        this.libraries.add(library);
    }

    private void addLibrary(int id, String organisation, String name, String revision, String cpFile,
            String[] libFiles) {

        JarFile cpJarFile = new JarFile(Environment.rootFolder + cpFile);

        List<String> libJarFiles = new ArrayList<String>();

        for (String libFile : libFiles) {
            libJarFiles.add(Environment.rootFolder + libFile);
        }

        this.libraries.add(new Library(id, organisation, name, revision, cpJarFile, new JarFileSet(libJarFiles)));
    }

    public Library get(int id) {
        for (Library library : this.libraries) {
            if (library.id() == id) {
                return library;
            }
        }
        throw new Error("Library not found.");
    }

    @Override
    public Iterator<Library> iterator() {
        return this.libraries.iterator();
    }

    public int size() {
        return this.libraries.size();
    }

    public static DataSet getDevelopmentSet() {
        DataSet dataSet = new DataSet();

        dataSet.addLibrary(0, "org", "test-dataset", "1.0", "TestProject.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib", });

        return dataSet;
    }

    /**
     * Returns a data set containing those libraries for which a complete and valid
     * class hierarchy can be build (i.e. which do not have a missing dependency).
     */
    public static DataSet getCorrectSet() {

        DataSet completeSet = DataSet.getCompleteSet();
        DataSet correctSet = new DataSet();

        Integer[] correctIds = { 1, 2, 3, 4, 5, 6, 8, 11, 17, 18, 19, 21, 26, 27, 30, 33, 35, 37, 41, 47, 48, 49, 51,
                52, 60, 71, 72, 86, 88, 91, 95, 97 };
        List<Integer> correctLibraries = new ArrayList<Integer>(Arrays.asList(correctIds));

        for (Library library : completeSet) {
            if (correctLibraries.contains(library.id())) {
                correctSet.addLibrary(library);
            }
        }
        return correctSet;
    }
    
    public static DataSet getTestSet() {
        DataSet completeSet = DataSet.getCompleteSet();
        DataSet correctSet = new DataSet();

        Integer[] correctIds = { 2, 4, 6 };
        List<Integer> correctLibraries = new ArrayList<Integer>(Arrays.asList(correctIds));

        for (Library library : completeSet) {
            if (correctLibraries.contains(library.id())) {
                correctSet.addLibrary(library);
            }
        }
        return correctSet;        
    }
    
    public static DataSet getApplicationSet() {
        
        DataSet applicationSet = new DataSet();

        applicationSet.addLibrary(0, "org.soot", "org.soot", "x.x", 
                "Applications\\soot\\sootclasses-trunk-jar-with-dependencies.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        applicationSet.addLibrary(1, "smallsql", "smallsql", "0.21", 
                "Applications\\smallsql\\smallsql0.21_lib\\smallsql.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        applicationSet.addLibrary(2, "hsqldb", "sqltool", "2.4.1", 
                "Applications\\hsql\\hsqldb-2.4.1\\hsqldb\\lib\\sqltool.jar",
                new String[] {
                        "Applications\\hsql\\hsqldb-2.4.1\\hsqldb\\lib\\hsqldb.jar",
                        "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" 
                });
        applicationSet.addLibrary(3, "org.jwork", "jPort", "1.8", 
                "Applications\\jPort\\jPort-1.8\\jPort\\jport.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
                // http://jwork.org/jport/
        applicationSet.addLibrary(4, "org.jedit", "jEdit", "5.5.0", 
                "Applications\\jEdit\\jEdit-5.5.0\\jedit.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        applicationSet.addLibrary(5, "jpc", "JPC", "1.0", 
                "Applications\\jpc\\JPCApplication.jar",
                new String[] {
                        "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        applicationSet.addLibrary(6, "jlGui", "jlGui", "3.0", 
                "Applications\\jlgui\\jlgui3.0\\jlgui3.0.jar",
                new String[] {
                        "Applications\\jlgui\\jlgui3.0\\lib\\jl1.0.jar",
                        "Applications\\jlgui\\jlgui3.0\\lib\\mp3spi1.9.4.jar", 
                        "Applications\\jlgui\\jlgui3.0\\lib\\jorbis-0.0.15.jar",
                        "Applications\\jlgui\\jlgui3.0\\lib\\jogg-0.0.7.jar",
                        "Applications\\jlgui\\jlgui3.0\\lib\\vorbisspi1.0.2.jar",
                        "Applications\\jlgui\\jlgui3.0\\lib\\tritonus_share.jar",
                        "Applications\\jlgui\\jlgui3.0\\lib\\jspeex0.9.7.jar",
                        "Applications\\jlgui\\jlgui3.0\\lib\\basicplayer3.0.jar",
                        "Applications\\jlgui\\jlgui3.0\\lib\\kj_dsp1.1.jar",
                        "Applications\\jlgui\\jlgui3.0\\lib\\commons-logging-api.jar",
                        "Applications\\jlgui\\jlgui3.0\\lib\\jmactritonusspi1.74.jar",
                        "Applications\\jlgui\\jlgui3.0\\lib\\jflac-1.2.jar",
                        "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        return applicationSet;
    }
    
    public static DataSet getUnmodifiedSet() {

        DataSet completeSet = DataSet.getCompleteSet();
        DataSet correctSet = new DataSet();

        Integer[] correctIds = { 1, 2, 3, 6, 8, 11, 17, 18, 19, 21, 26, 27, 30, 33, 35, 37, 41, 47, 48, 49, 51,
                52, 60, 71, 72, 86, 88, 91, 95, 97 };
        List<Integer> correctLibraries = new ArrayList<Integer>(Arrays.asList(correctIds));

        for (Library library : completeSet) {
            if (correctLibraries.contains(library.id())) {
                correctSet.addLibrary(library);
            }
        }
        return correctSet;
    }

    public static DataSet getCompleteSet() {

        DataSet dataSet = new DataSet();

        dataSet.addLibrary(0, "org.scala-lang", "scala-compiler", "2.10.4",
                "Libraries\\org.scala-lang\\scala-compiler\\jars\\scala-compiler-2.10.4.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.scala-lang\\scala-library\\jars\\scala-library-2.10.4.jar",
                        "Libraries\\org.scala-lang\\scala-reflect\\jars\\scala-reflect-2.10.4.jar",
                        // Added
                        "Libraries\\org.scala-lang\\jline\\jars\\jline-2.10.4.jar",
                        "Libraries\\org.apache.ant\\ant\\jars\\ant-1.9.6.jar",
                        "Libraries\\org.apache.ant\\ant-launcher\\jars\\ant-launcher-1.9.6.jar" });
        dataSet.addLibrary(1, "org.scala-lang", "scala-library", "2.10.4",
                "Libraries\\org.scala-lang\\scala-library\\jars\\scala-library-2.10.4.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(2, "junit", "junit", "4.12", "Libraries\\junit\\junit\\jars\\junit-4.12.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.hamcrest\\hamcrest-core\\jars\\hamcrest-core-1.3.jar" });
        dataSet.addLibrary(3, "org.scala-lang", "scala-library", "2.12.0-M3",
                "Libraries\\org.scala-lang\\scala-library\\jars\\scala-library-2.12.0-M3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(4, "org.slf4j", "slf4j-api", "1.7.12",
                "Libraries\\org.slf4j\\slf4j-api\\jars\\slf4j-api-1.7.12.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        // Added
                        "Libraries\\org.slf4j\\slf4j-log4j12\\jars\\slf4j-log4j12-1.7.12.jar",
                        "Libraries\\log4j/log4j\\bundles\\log4j-1.2.17.jar",
                        "Libraries\\javax.mail\\mail\\jars\\mail-1.4.7.jar",
                        "Libraries\\geronimo-jms_1.1_spec-1.1.1.jar" });
        dataSet.addLibrary(5, "log4j", "log4j", "1.2.17", "Libraries\\log4j\\log4j\\bundles\\log4j-1.2.17.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        // Added
                        "Libraries\\javax.mail\\mail\\jars\\mail-1.4.7.jar",
                        "Libraries\\geronimo-jms_1.1_spec-1.1.1.jar" });
        dataSet.addLibrary(6, "com.google.guava", "guava", "19.0-rc2",
                "Libraries\\com.google.guava\\guava\\bundles\\guava-19.0-rc2.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(7, "ch.qos.logback", "logback-classic", "1.1.3",
                "Libraries\\ch.qos.logback\\logback-classic\\jars\\logback-classic-1.1.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        // Added
                        "Libraries\\ch.qos.logback\\logback-core\\jars\\logback-core-1.1.3.jar",
                        "Libraries\\javax.mail\\mail\\jars\\mail-1.4.7.jar",
                        "Libraries\\javax.servlet\\javax.servlet-api\\jars\\javax.servlet-api-3.1.0.jar",
                        "Libraries\\org.codehaus.groovy\\groovy-all\\jars\\groovy-all-2.4.5.jar",
                        "Libraries\\org.slf4j\\slf4j-api\\jars\\slf4j-api-1.7.12.jar" });
        dataSet.addLibrary(8, "commons-io", "commons-io", "2.4",
                "Libraries\\commons-io\\commons-io\\jars\\commons-io-2.4.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(9, "org.slf4j", "slf4j-log4j12", "1.7.12",
                "Libraries\\org.slf4j\\slf4j-log4j12\\jars\\slf4j-log4j12-1.7.12.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.slf4j\\slf4j-api\\jars\\slf4j-api-1.7.12.jar",
                        "Libraries\\log4j\\log4j\\bundles\\log4j-1.2.17.jar",
                        // Added
                        "Libraries\\javax.mail\\mail\\jars\\mail-1.4.7.jar", });
        dataSet.addLibrary(10, "org.mockito", "mockito-all", "1.10.19",
                "Libraries\\org.mockito\\mockito-all\\jars\\mockito-all-1.10.19.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        // Added
                        "Libraries\\junit\\junit\\jars\\junit-4.12.jar", });
        dataSet.addLibrary(11, "org.apache.commons", "commons-lang3", "3.4",
                "Libraries\\org.apache.commons\\commons-lang3\\jars\\commons-lang3-3.4.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                // Added
                });
        dataSet.addLibrary(12, "commons-logging", "commons-logging", "1.2",
                "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(13, "org.testng", "testng", "6.9.9", "Libraries\\org.testng\\testng\\jars\\testng-6.9.9.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\com.beust\\jcommander\\jars\\jcommander-1.48.jar",
                        "Libraries\\com.google.inject\\guice\\jars\\guice-4.0.jar",
                        "Libraries\\javax.inject\\javax.inject\\jars\\javax.inject-1.jar",
                        "Libraries\\aopalliance\\aopalliance\\jars\\aopalliance-1.0.jar",
                        "Libraries\\org.yaml\\snakeyaml\\bundles\\snakeyaml-1.15.jar",
                        "Libraries\\org.beanshell\\bsh\\jars\\bsh-2.0b4.jar" });
        dataSet.addLibrary(14, "org.apache.maven", "maven-plugin-api", "3.3.3",
                "Libraries\\org.apache.maven\\maven-plugin-api\\jars\\maven-plugin-api-3.3.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.apache.maven\\maven-model\\jars\\maven-model-3.3.3.jar",
                        "Libraries\\org.apache.maven\\maven-artifact\\jars\\maven-artifact-3.3.3.jar",
                        "Libraries\\org.eclipse.sisu\\org.eclipse.sisu.plexus\\eclipse-plugins\\org.eclipse.sisu.plexus-0.3.0.jar",
                        "Libraries\\org.eclipse.sisu\\org.eclipse.sisu.inject\\eclipse-plugins\\org.eclipse.sisu.inject-0.3.0.jar",
                        "Libraries\\org.codehaus.plexus\\plexus-component-annotations\\jars\\plexus-component-annotations-1.5.5.jar",
                        "Libraries\\org.codehaus.plexus\\plexus-classworlds\\bundles\\plexus-classworlds-2.5.2.jar" });
        dataSet.addLibrary(15, "org.springframework", "spring-context", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-context\\jars\\spring-context-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.springframework\\spring-aop\\jars\\spring-aop-4.2.2.RELEASE.jar",
                        "Libraries\\aopalliance\\aopalliance\\jars\\aopalliance-1.0.jar",
                        "Libraries\\org.springframework\\spring-beans\\jars\\spring-beans-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar",
                        "Libraries\\org.springframework\\spring-expression\\jars\\spring-expression-4.2.2.RELEASE.jar" });
        dataSet.addLibrary(16, "org.apache.httpcomponents", "httpclient", "4.5.1",
                "Libraries\\org.apache.httpcomponents\\httpclient\\jars\\httpclient-4.5.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar" });
        dataSet.addLibrary(17, "org.osgi", "org.osgi.core", "6.0.0",
                "Libraries\\org.osgi\\org.osgi.core\\jars\\org.osgi.core-6.0.0.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(18, "joda-time", "joda-time", "2.9",
                "Libraries\\joda-time\\joda-time\\jars\\joda-time-2.9.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(19, "javax.servlet", "javax.servlet-api", "3.1.0",
                "Libraries\\javax.servlet\\javax.servlet-api\\jars\\javax.servlet-api-3.1.0.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(20, "com.fasterxml.jackson.core", "jackson-databind", "2.6.3",
                "Libraries\\com.fasterxml.jackson.core\\jackson-databind\\bundles\\jackson-databind-2.6.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\com.fasterxml.jackson.core\\jackson-core\\bundles\\jackson-core-2.6.3.jar" });
        dataSet.addLibrary(21, "commons-codec", "commons-codec", "1.10",
                "Libraries\\commons-codec\\commons-codec\\jars\\commons-codec-1.10.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(22, "org.springframework", "spring-test", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-test\\jars\\spring-test-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar" });
        dataSet.addLibrary(23, "org.springframework", "spring-core", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar" });
        dataSet.addLibrary(24, "org.easymock", "easymock", "3.4",
                "Libraries\\org.easymock\\easymock\\jars\\easymock-3.4.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.objenesis\\objenesis\\jars\\objenesis-2.2.jar" });
        dataSet.addLibrary(25, "org.scalatest", "scalatest_2.10", "3.0.0-M11",
                "Libraries\\org.scalatest\\scalatest_2.10\\bundles\\scalatest_2.10-3.0.0-M11.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.scalactic\\scalactic_2.10\\bundles\\scalactic_2.10-3.0.0-M11.jar",
                        "Libraries\\org.scala-lang\\scala-reflect\\jars\\scala-reflect-2.10.5.jar" });
        dataSet.addLibrary(26, "commons-collections", "commons-collections", "3.2.1",
                "Libraries\\commons-collections\\commons-collections\\jars\\commons-collections-3.2.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(27, "org.codehaus.plexus", "plexus-utils", "3.0.22",
                "Libraries\\org.codehaus.plexus\\plexus-utils\\jars\\plexus-utils-3.0.22.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(28, "com.h2database", "h2", "1.4.190", "Libraries\\com.h2database\\h2\\jars\\h2-1.4.190.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(29, "org.apache.maven", "maven-project", "2.2.1",
                "Libraries\\org.apache.maven\\maven-project\\jars\\maven-project-2.2.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.apache.maven\\maven-profile\\jars\\maven-profile-2.2.1.jar",
                        "Libraries\\org.apache.maven\\maven-artifact-manager\\jars\\maven-artifact-manager-2.2.1.jar",
                        "Libraries\\org.apache.maven.wagon\\wagon-provider-api\\jars\\wagon-provider-api-1.0-beta-6.jar",
                        "Libraries\\backport-util-concurrent\\backport-util-concurrent\\jars\\backport-util-concurrent-3.1.jar",
                        "Libraries\\org.apache.maven\\maven-plugin-registry\\jars\\maven-plugin-registry-2.2.1.jar" });
        dataSet.addLibrary(30, "com.google.code.gson", "gson", "2.4",
                "Libraries\\com.google.code.gson\\gson\\jars\\gson-2.4.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(31, "org.osgi", "org.osgi.compendium", "5.0.0",
                "Libraries\\org.osgi\\org.osgi.compendium\\jars\\org.osgi.compendium-5.0.0.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(32, "org.springframework", "spring-beans", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-beans\\jars\\spring-beans-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar" });
        dataSet.addLibrary(33, "com.google.code.findbugs", "jsr305", "3.0.1",
                "Libraries\\com.google.code.findbugs\\jsr305\\jars\\jsr305-3.0.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(34, "org.codehaus.jackson", "jackson-mapper-asl", "1.9.13",
                "Libraries\\org.codehaus.jackson\\jackson-mapper-asl\\jars\\jackson-mapper-asl-1.9.13.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.codehaus.jackson\\jackson-core-asl\\jars\\jackson-core-asl-1.9.13.jar" });
        dataSet.addLibrary(35, "org.hamcrest", "hamcrest-library", "1.3",
                "Libraries\\org.hamcrest\\hamcrest-library\\jars\\hamcrest-library-1.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.hamcrest\\hamcrest-core\\jars\\hamcrest-core-1.3.jar" });
        dataSet.addLibrary(36, "org.springframework", "spring-web", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-web\\jars\\spring-web-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.springframework\\spring-aop\\jars\\spring-aop-4.2.2.RELEASE.jar",
                        "Libraries\\aopalliance\\aopalliance\\jars\\aopalliance-1.0.jar",
                        "Libraries\\org.springframework\\spring-beans\\jars\\spring-beans-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar",
                        "Libraries\\org.springframework\\spring-context\\jars\\spring-context-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-expression\\jars\\spring-expression-4.2.2.RELEASE.jar" });
        dataSet.addLibrary(37, "com.fasterxml.jackson.core", "jackson-core", "2.6.3",
                "Libraries\\com.fasterxml.jackson.core\\jackson-core\\bundles\\jackson-core-2.6.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(38, "com.google.inject", "guice", "4.0",
                "Libraries\\com.google.inject\\guice\\jars\\guice-4.0.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\javax.inject\\javax.inject\\jars\\javax.inject-1.jar",
                        "Libraries\\aopalliance\\aopalliance\\jars\\aopalliance-1.0.jar" });
        dataSet.addLibrary(39, "org.codehaus.groovy", "groovy-all", "2.4.5",
                "Libraries\\org.codehaus.groovy\\groovy-all\\jars\\groovy-all-2.4.5.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(40, "org.apache.maven", "maven-core", "3.3.3",
                "Libraries\\org.apache.maven\\maven-core\\jars\\maven-core-3.3.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.apache.maven\\maven-model\\jars\\maven-model-3.3.3.jar",
                        "Libraries\\org.apache.maven\\maven-settings\\jars\\maven-settings-3.3.3.jar",
                        "Libraries\\org.apache.maven\\maven-settings-builder\\jars\\maven-settings-builder-3.3.3.jar",
                        "Libraries\\org.apache.maven\\maven-builder-support\\jars\\maven-builder-support-3.3.3.jar",
                        "Libraries\\org.codehaus.plexus\\plexus-interpolation\\jars\\plexus-interpolation-1.21.jar",
                        "Libraries\\org.codehaus.plexus\\plexus-component-annotations\\jars\\plexus-component-annotations-1.5.5.jar",
                        "Libraries\\org.sonatype.plexus\\plexus-sec-dispatcher\\jars\\plexus-sec-dispatcher-1.3.jar",
                        "Libraries\\org.apache.maven\\maven-repository-metadata\\jars\\maven-repository-metadata-3.3.3.jar",
                        "Libraries\\org.apache.maven\\maven-artifact\\jars\\maven-artifact-3.3.3.jar",
                        "Libraries\\org.apache.maven\\maven-plugin-api\\jars\\maven-plugin-api-3.3.3.jar",
                        "Libraries\\org.eclipse.sisu\\org.eclipse.sisu.plexus\\eclipse-plugins\\org.eclipse.sisu.plexus-0.3.0.jar",
                        "Libraries\\org.eclipse.sisu\\org.eclipse.sisu.inject\\eclipse-plugins\\org.eclipse.sisu.inject-0.3.0.jar",
                        "Libraries\\org.codehaus.plexus\\plexus-classworlds\\bundles\\plexus-classworlds-2.5.2.jar",
                        "Libraries\\org.apache.maven\\maven-model-builder\\jars\\maven-model-builder-3.3.3.jar",
                        "Libraries\\org.apache.maven\\maven-aether-provider\\jars\\maven-aether-provider-3.3.3.jar",
                        "Libraries\\org.eclipse.aether\\aether-api\\jars\\aether-api-1.0.2.v20150114.jar",
                        "Libraries\\org.eclipse.aether\\aether-spi\\jars\\aether-spi-1.0.2.v20150114.jar",
                        "Libraries\\org.eclipse.aether\\aether-util\\jars\\aether-util-1.0.2.v20150114.jar",
                        "Libraries\\org.eclipse.aether\\aether-impl\\jars\\aether-impl-1.0.2.v20150114.jar" });
        dataSet.addLibrary(41, "org.hamcrest", "hamcrest-core", "1.3",
                "Libraries\\org.hamcrest\\hamcrest-core\\jars\\hamcrest-core-1.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(42, "commons-beanutils", "commons-beanutils", "1.9.2",
                "Libraries\\commons-beanutils\\commons-beanutils\\jars\\commons-beanutils-1.9.2.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\commons-collections\\commons-collections\\jars\\commons-collections-3.2.1.jar" });
        dataSet.addLibrary(43, "ch.qos.logback", "logback-core", "1.1.3",
                "Libraries\\ch.qos.logback\\logback-core\\jars\\logback-core-1.1.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(44, "com.google.gwt", "gwt-user", "2.7.0",
                "Libraries\\com.google.gwt\\gwt-user\\jars\\gwt-user-2.7.0.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(45, "mysql", "mysql-connector-java", "5.1.37",
                "Libraries\\mysql\\mysql-connector-java\\jars\\mysql-connector-java-5.1.37.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(46, "org.springframework", "spring-webmvc", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-webmvc\\jars\\spring-webmvc-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.springframework\\spring-beans\\jars\\spring-beans-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar",
                        "Libraries\\org.springframework\\spring-context\\jars\\spring-context-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-aop\\jars\\spring-aop-4.2.2.RELEASE.jar",
                        "Libraries\\aopalliance\\aopalliance\\jars\\aopalliance-1.0.jar",
                        "Libraries\\org.springframework\\spring-expression\\jars\\spring-expression-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-web\\jars\\spring-web-4.2.2.RELEASE.jar" });
        dataSet.addLibrary(47, "com.fasterxml.jackson.core", "jackson-annotations", "2.6.3",
                "Libraries\\com.fasterxml.jackson.core\\jackson-annotations\\bundles\\jackson-annotations-2.6.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(48, "javax.enterprise", "cdi-api", "2.0-EDR1",
                "Libraries\\javax.enterprise\\cdi-api\\jars\\cdi-api-2.0-EDR1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\javax.el\\javax.el-api\\jars\\javax.el-api-3.0.0.jar",
                        "Libraries\\javax.interceptor\\javax.interceptor-api\\jars\\javax.interceptor-api-1.2.jar",
                        "Libraries\\javax.inject\\javax.inject\\jars\\javax.inject-1.jar" });
        dataSet.addLibrary(49, "commons-cli", "commons-cli", "1.3.1",
                "Libraries\\commons-cli\\commons-cli\\jars\\commons-cli-1.3.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(50, "javax.mail", "mail", "1.4.7", "Libraries\\javax.mail\\mail\\jars\\mail-1.4.7.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\javax.activation\\activation\\jars\\activation-1.1.jar" });
        dataSet.addLibrary(51, "org.assertj", "assertj-core", "3.2.0",
                "Libraries\\org.assertj\\assertj-core\\bundles\\assertj-core-3.2.0.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\junit\\junit\\jars\\junit-4.12.jar",
                        "Libraries\\org.hamcrest\\hamcrest-core\\jars\\hamcrest-core-1.3.jar" });
        dataSet.addLibrary(52, "org.apache.ant", "ant", "1.9.6", "Libraries\\org.apache.ant\\ant\\jars\\ant-1.9.6.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.apache.ant\\ant-launcher\\jars\\ant-launcher-1.9.6.jar" });
        dataSet.addLibrary(53, "xerces", "xercesImpl", "2.11.0",
                "Libraries\\xerces\\xercesImpl\\jars\\xercesImpl-2.11.0.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(54, "org.hibernate", "hibernate-core", "5.0.3.Final",
                "Libraries\\org.hibernate\\hibernate-core\\jars\\hibernate-core-5.0.3.Final.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.jboss.logging\\jboss-logging\\jars\\jboss-logging-3.3.0.Final.jar",
                        "Libraries\\org.hibernate.javax.persistence\\hibernate-jpa-2.1-api\\jars\\hibernate-jpa-2.1-api-1.0.0.Final.jar",
                        "Libraries\\antlr\\antlr\\jars\\antlr-2.7.7.jar",
                        "Libraries\\org.jboss\\jandex\\bundles\\jandex-2.0.0.CR1.jar",
                        "Libraries\\org.apache.geronimo.specs\\geronimo-jta_1.1_spec\\jars\\geronimo-jta_1.1_spec-1.1.1.jar",
                        "Libraries\\dom4j\\dom4j\\jars\\dom4j-1.6.1.jar",
                        "Libraries\\org.hibernate.common\\hibernate-commons-annotations\\jars\\hibernate-commons-annotations-5.0.0.Final.jar" });
        dataSet.addLibrary(55, "org.apache.maven", "maven-artifact", "3.3.3",
                "Libraries\\org.apache.maven\\maven-artifact\\jars\\maven-artifact-3.3.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(56, "org.apache.derby", "derby", "10.12.1.1",
                "Libraries\\org.apache.derby\\derby\\jars\\derby-10.12.1.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(57, "org.hsqldb", "hsqldb", "2.3.3", "Libraries\\org.hsqldb\\hsqldb\\jars\\hsqldb-2.3.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(58, "org.hibernate", "hibernate-entitymanager", "5.0.3.Final",
                "Libraries\\org.hibernate\\hibernate-entitymanager\\jars\\hibernate-entitymanager-5.0.3.Final.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.jboss.logging\\jboss-logging\\jars\\jboss-logging-3.3.0.Final.jar",
                        "Libraries\\org.hibernate\\hibernate-core\\jars\\hibernate-core-5.0.3.Final.jar",
                        "Libraries\\org.hibernate.javax.persistence\\hibernate-jpa-2.1-api\\jars\\hibernate-jpa-2.1-api-1.0.0.Final.jar",
                        "Libraries\\antlr\\antlr\\jars\\antlr-2.7.7.jar",
                        "Libraries\\org.jboss\\jandex\\bundles\\jandex-2.0.0.CR1.jar",
                        "Libraries\\org.apache.geronimo.specs\\geronimo-jta_1.1_spec\\jars\\geronimo-jta_1.1_spec-1.1.1.jar",
                        "Libraries\\dom4j\\dom4j\\jars\\dom4j-1.6.1.jar",
                        "Libraries\\org.hibernate.common\\hibernate-commons-annotations\\jars\\hibernate-commons-annotations-5.0.0.Final.jar" });
        dataSet.addLibrary(59, "org.freemarker", "freemarker", "2.3.23",
                "Libraries\\org.freemarker\\freemarker\\jars\\freemarker-2.3.23.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(60, "javax.validation", "validation-api", "1.1.0.Final",
                "Libraries\\javax.validation\\validation-api\\jars\\validation-api-1.1.0.Final.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(61, "cglib", "cglib-nodep", "3.2.0",
                "Libraries\\cglib\\cglib-nodep\\jars\\cglib-nodep-3.2.0.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(62, "org.springframework", "spring-jdbc", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-jdbc\\jars\\spring-jdbc-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.springframework\\spring-beans\\jars\\spring-beans-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar",
                        "Libraries\\org.springframework\\spring-tx\\jars\\spring-tx-4.2.2.RELEASE.jar" });
        dataSet.addLibrary(63, "com.sun.xml.bind", "jaxb-impl", "2.2.11",
                "Libraries\\com.sun.xml.bind\\jaxb-impl\\jars\\jaxb-impl-2.2.11.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(64, "org.hibernate", "hibernate-validator", "5.2.2.Final",
                "Libraries\\org.hibernate\\hibernate-validator\\jars\\hibernate-validator-5.2.2.Final.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\javax.validation\\validation-api\\jars\\validation-api-1.1.0.Final.jar",
                        "Libraries\\com.fasterxml\\classmate\\bundles\\classmate-1.1.0.jar" });
        dataSet.addLibrary(65, "com.thoughtworks.xstream", "xstream", "1.4.8",
                "Libraries\\com.thoughtworks.xstream\\xstream\\jars\\xstream-1.4.8.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\xmlpull\\xmlpull\\jars\\xmlpull-1.1.3.1.jar",
                        "Libraries\\xpp3\\xpp3_min\\jars\\xpp3_min-1.1.4c.jar" });
        dataSet.addLibrary(66, "org.springframework", "spring-aop", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-aop\\jars\\spring-aop-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\aopalliance\\aopalliance\\jars\\aopalliance-1.0.jar",
                        "Libraries\\org.springframework\\spring-beans\\jars\\spring-beans-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar" });
        dataSet.addLibrary(67, "org.jboss.logging", "jboss-logging", "3.3.0.Final",
                "Libraries\\org.jboss.logging\\jboss-logging\\jars\\jboss-logging-3.3.0.Final.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(68, "org.mortbay.jetty", "jetty", "6.1.26",
                "Libraries\\org.mortbay.jetty\\jetty\\jars\\jetty-6.1.26.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.mortbay.jetty\\jetty-util\\jars\\jetty-util-6.1.26.jar",
                        "Libraries\\org.mortbay.jetty\\servlet-api\\jars\\servlet-api-2.5-20081211.jar" });
        dataSet.addLibrary(69, "commons-dbcp", "commons-dbcp", "1.4",
                "Libraries\\commons-dbcp\\commons-dbcp\\jars\\commons-dbcp-1.4.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\commons-pool\\commons-pool\\jars\\commons-pool-1.5.4.jar" });
        dataSet.addLibrary(70, "commons-fileupload", "commons-fileupload", "1.3.1",
                "Libraries\\commons-fileupload\\commons-fileupload\\jars\\commons-fileupload-1.3.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(71, "org.json", "json", "20150729", "Libraries\\org.json\\json\\jars\\json-20150729.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(72, "org.easytesting", "fest-assert", "1.4",
                "Libraries\\org.easytesting\\fest-assert\\jars\\fest-assert-1.4.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.easytesting\\fest-util\\jars\\fest-util-1.1.6.jar" });
        dataSet.addLibrary(73, "org.springframework", "spring-tx", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-tx\\jars\\spring-tx-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.springframework\\spring-beans\\jars\\spring-beans-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar" });
        dataSet.addLibrary(74, "org.eclipse.jetty", "jetty-servlet", "9.3.5.v20151012",
                "Libraries\\org.eclipse.jetty\\jetty-servlet\\jars\\jetty-servlet-9.3.5.v20151012.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.eclipse.jetty\\jetty-security\\jars\\jetty-security-9.3.5.v20151012.jar",
                        "Libraries\\org.eclipse.jetty\\jetty-server\\jars\\jetty-server-9.3.5.v20151012.jar",
                        "Libraries\\javax.servlet\\javax.servlet-api\\jars\\javax.servlet-api-3.1.0.jar",
                        "Libraries\\org.eclipse.jetty\\jetty-http\\jars\\jetty-http-9.3.5.v20151012.jar",
                        "Libraries\\org.eclipse.jetty\\jetty-util\\jars\\jetty-util-9.3.5.v20151012.jar",
                        "Libraries\\org.eclipse.jetty\\jetty-io\\jars\\jetty-io-9.3.5.v20151012.jar" });
        dataSet.addLibrary(75, "org.scoverage", "scalac-scoverage-plugin_2.11", "1.1.1",
                "Libraries\\org.scoverage\\scalac-scoverage-plugin_2.11\\jars\\scalac-scoverage-plugin_2.11-1.1.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.scala-lang.modules\\scala-xml_2.11\\bundles\\scala-xml_2.11-1.0.1.jar" });
        dataSet.addLibrary(76, "org.apache.maven", "maven-model", "3.3.3",
                "Libraries\\org.apache.maven\\maven-model\\jars\\maven-model-3.3.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(77, "org.spockframework", "spock-core", "1.0-groovy-2.4",
                "Libraries\\org.spockframework\\spock-core\\jars\\spock-core-1.0-groovy-2.4.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\junit\\junit\\jars\\junit-4.12.jar",
                        "Libraries\\org.hamcrest\\hamcrest-core\\jars\\hamcrest-core-1.3.jar" });
        dataSet.addLibrary(78, "org.projectlombok", "lombok", "1.16.6",
                "Libraries\\org.projectlombok\\lombok\\jars\\lombok-1.16.6.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(79, "cglib", "cglib", "3.2.0", "Libraries\\cglib\\cglib\\jars\\cglib-3.2.0.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.ow2.asm\\asm\\jars\\asm-5.0.3.jar" });
        dataSet.addLibrary(80, "org.springframework", "spring-context-support", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-context-support\\jars\\spring-context-support-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.springframework\\spring-beans\\jars\\spring-beans-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar",
                        "Libraries\\org.springframework\\spring-context\\jars\\spring-context-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-aop\\jars\\spring-aop-4.2.2.RELEASE.jar",
                        "Libraries\\aopalliance\\aopalliance\\jars\\aopalliance-1.0.jar",
                        "Libraries\\org.springframework\\spring-expression\\jars\\spring-expression-4.2.2.RELEASE.jar" });
        dataSet.addLibrary(81, "dom4j", "dom4j", "1.6.1", "Libraries\\dom4j\\dom4j\\jars\\dom4j-1.6.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(82, "org.springframework", "spring-orm", "4.2.2.RELEASE",
                "Libraries\\org.springframework\\spring-orm\\jars\\spring-orm-4.2.2.RELEASE.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.springframework\\spring-beans\\jars\\spring-beans-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-core\\jars\\spring-core-4.2.2.RELEASE.jar",
                        "Libraries\\commons-logging\\commons-logging\\jars\\commons-logging-1.2.jar",
                        "Libraries\\org.springframework\\spring-jdbc\\jars\\spring-jdbc-4.2.2.RELEASE.jar",
                        "Libraries\\org.springframework\\spring-tx\\jars\\spring-tx-4.2.2.RELEASE.jar" });
        dataSet.addLibrary(83, "org.scalacheck", "scalacheck_2.10", "1.12.5",
                "Libraries\\org.scalacheck\\scalacheck_2.10\\jars\\scalacheck_2.10-1.12.5.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.scala-sbt\\test-interface\\jars\\test-interface-1.0.jar" });
        dataSet.addLibrary(84, "org.powermock", "powermock-module-junit4", "1.6.3",
                "Libraries\\org.powermock\\powermock-module-junit4\\jars\\powermock-module-junit4-1.6.3.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\junit\\junit\\jars\\junit-4.12.jar",
                        "Libraries\\org.hamcrest\\hamcrest-core\\jars\\hamcrest-core-1.3.jar",
                        "Libraries\\org.powermock\\powermock-module-junit4-common\\jars\\powermock-module-junit4-common-1.6.3.jar",
                        "Libraries\\org.powermock\\powermock-core\\jars\\powermock-core-1.6.3.jar",
                        "Libraries\\org.powermock\\powermock-reflect\\jars\\powermock-reflect-1.6.3.jar",
                        "Libraries\\org.javassist\\javassist\\bundles\\javassist-3.20.0-GA.jar" });
        dataSet.addLibrary(85, "org.apache.velocity", "velocity", "1.7",
                "Libraries\\org.apache.velocity\\velocity\\jars\\velocity-1.7.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\commons-collections\\commons-collections\\jars\\commons-collections-3.2.1.jar" });
        dataSet.addLibrary(86, "org.apache.httpcomponents", "httpcore", "4.4.4",
                "Libraries\\org.apache.httpcomponents\\httpcore\\jars\\httpcore-4.4.4.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(87, "commons-configuration", "commons-configuration", "1.10",
                "Libraries\\commons-configuration\\commons-configuration\\jars\\commons-configuration-1.10.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\commons-lang\\commons-lang\\jars\\commons-lang-2.6.jar" });
        dataSet.addLibrary(88, "asm", "asm", "3.3.1", "Libraries\\asm\\asm\\jars\\asm-3.3.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(89, "org.apache.felix", "org.apache.felix.scr.annotations", "1.9.12",
                "Libraries\\org.apache.felix\\org.apache.felix.scr.annotations\\jars\\org.apache.felix.scr.annotations-1.9.12.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(90, "org.easymock", "easymockclassextension", "3.2",
                "Libraries\\org.easymock\\easymockclassextension\\jars\\easymockclassextension-3.2.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib", 
                        // Added
                        //"Libraries\\org.easymock\\easymock\\jars\\easymock-3.4.jar",
                        "Libraries\\org.objenesis\\objenesis\\jars\\objenesis-2.2.jar"
                });
        dataSet.addLibrary(91, "org.aspectj", "aspectjrt", "1.8.7",
                "Libraries\\org.aspectj\\aspectjrt\\jars\\aspectjrt-1.8.7.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(92, "org.apache.camel", "camel-core", "2.16.0",
                "Libraries\\org.apache.camel\\camel-core\\bundles\\camel-core-2.16.0.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\com.sun.xml.bind\\jaxb-core\\jars\\jaxb-core-2.2.11.jar",
                        "Libraries\\com.sun.xml.bind\\jaxb-impl\\jars\\jaxb-impl-2.2.11.jar" });
        dataSet.addLibrary(93, "xmlunit", "xmlunit", "1.6", "Libraries\\xmlunit\\xmlunit\\jars\\xmlunit-1.6.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(94, "org.javassist", "javassist", "3.20.0-GA",
                "Libraries\\org.javassist\\javassist\\bundles\\javassist-3.20.0-GA.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(95, "com.google.protobuf", "protobuf-java", "2.6.1",
                "Libraries\\com.google.protobuf\\protobuf-java\\bundles\\protobuf-java-2.6.1.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(96, "org.codehaus.plexus", "plexus-container-default", "1.6",
                "Libraries\\org.codehaus.plexus\\plexus-container-default\\jars\\plexus-container-default-1.6.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\org.apache.xbean\\xbean-reflect\\bundles\\xbean-reflect-3.7.jar",
                        "Libraries\\com.google.collections\\google-collections\\jars\\google-collections-1.0.jar" });
        dataSet.addLibrary(97, "org.hibernate.javax.persistence", "hibernate-jpa-2.0-api", "1.0.1.Final",
                "Libraries\\org.hibernate.javax.persistence\\hibernate-jpa-2.0-api\\jars\\hibernate-jpa-2.0-api-1.0.1.Final.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });
        dataSet.addLibrary(98, "org.reflections", "reflections", "0.9.10",
                "Libraries\\org.reflections\\reflections\\jars\\reflections-0.9.10.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib",
                        "Libraries\\com.google.code.findbugs\\annotations\\jars\\annotations-2.0.1.jar" });
        dataSet.addLibrary(99, "com.sun.jersey", "jersey-server", "1.19",
                "Libraries\\com.sun.jersey\\jersey-server\\jars\\jersey-server-1.19.jar",
                new String[] { "JavaJDK\\java-8-openjdk-amd64\\jre\\lib" });

        return dataSet;
    }
}
