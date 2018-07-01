package org.asm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Represents a set of JAR-files.
 */
public class JarFileSet implements Iterable<JarFile> {

    private Set<JarFile> jarFiles = new HashSet<JarFile>();

    public JarFileSet(String location) {
        if (location == null) { throw new IllegalArgumentException("Parameter 'location' should not be null."); }

        findJarFiles(new File(location));
    }

    public JarFileSet(List<String> locations) {
        if (locations == null) { throw new IllegalArgumentException("Parameter 'locations' should not be null."); }

        for (String location : locations) {
            findJarFiles(new File(location));
        }
    }

    public JarFileSet(Set<JarFile> jarFiles) {
        if (jarFiles == null) { throw new IllegalArgumentException("Parameter 'jarFiles' should not be null."); }

        this.jarFiles.addAll(jarFiles);
    }

    public void add(JarFile jarFile) {
        if (jarFile == null) { throw new IllegalArgumentException("Parameter 'jarFile' should not be null."); }

        this.jarFiles.add(jarFile);
    }

    public int size() {
        return this.jarFiles.size();
    }

    public void accept(JarFileSetVisitor visitor) {

        for (JarFile jarFile : this.jarFiles) {
            visitor.visitJarFile(jarFile);
            jarFile.accept(visitor);
        }
        visitor.visitEnd();
    }

    private void findJarFiles(final File fileOrFolder) {

        if (fileOrFolder.isDirectory()) {
            for (final File fileEntry : fileOrFolder.listFiles()) {
                findJarFiles(fileEntry);
            }
        } else {
            if (getFileExtension(fileOrFolder).equalsIgnoreCase("jar")) {
                this.jarFiles.add(new JarFile(fileOrFolder));
            }
        }
    }

    private static String getFileExtension(final File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public String getSootClassPath() {
        return String.join(";", toSootStringList());
    }

    public List<String> toSootStringList() {
        List<String> jarFiles = new ArrayList<String>();
        for (JarFile jarFile : this.jarFiles) {
            jarFiles.add(jarFile.getAbsolutePath());
        }
        return jarFiles;
    }

    @Override
    public Iterator<JarFile> iterator() {
        return jarFiles.iterator();
    }
}
