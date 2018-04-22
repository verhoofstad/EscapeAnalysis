package org.asm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    public JarFileSet(File location) {
        findJarFiles(location);
    }

    public JarFileSet(String location) {
        findJarFiles(new File(location));
    }

    public JarFileSet(List<String> locations) {

        for (String location : locations) {
            findJarFiles(new File(location));
        }
    }

    public JarFileSet(Set<JarFile> jarFiles) {
        this.jarFiles.addAll(jarFiles);
    }

    public void add(File jarFile) {
        try {
            this.jarFiles.add(new JarFile(jarFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void add(JarFile jarFile) {
        this.jarFiles.add(jarFile);
    }

    public int size() {
        return this.jarFiles.size();
    }

    public void accept(JarFileSetVisitor visitor) {

        for (JarFile jarFile : this.jarFiles) {

            visitor.visitJarFile(jarFile);

            try {
                jarFile.accept(visitor);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                try {
                    this.jarFiles.add(new JarFile(fileOrFolder));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
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

        StringBuilder classPath = new StringBuilder();

        for (JarFile jarFile : this.jarFiles) {
            classPath.append(jarFile.getAbsolutePath() + ";");
        }
        return classPath.toString();
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
