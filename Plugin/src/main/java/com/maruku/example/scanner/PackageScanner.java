package com.maruku.example.scanner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by marc on 26/11/15.
 */
public final class PackageScanner {

    private final static Logger LOG = Logger.getLogger(PackageScanner.class.getName());
    private final static char DOT = '.';
    private final static char SLASH = '/';
    private final static String CLASS_SUFFIX = ".class";
    private final static String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the given '%s' package exists?";
    public static final String TARGET = "target/";

    public enum TYPE {
        TEST,
        MAIN
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            String packageName = args[0];
            List<Class<?>> classes = find(packageName, TYPE.MAIN);
            for (Class<?> cls : classes) {
                LOG.info(cls.getName());
            }
        }
    }

    public static List<Class<?>> find(final String scannedPackage, TYPE type) {
        final ClassLoader classLoader = PackageScanner.class.getClassLoader();
        final String scannedPath = scannedPackage.replace(DOT, SLASH);
        final Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(scannedPath);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage), e);
        }
        final List<Class<?>> classes = new LinkedList<Class<?>>();
        List<String> paths = new ArrayList<String>();
        while (resources.hasMoreElements()) {
            String path = resources.nextElement().getPath();
            path = path.substring(0, path.indexOf(TARGET) + TARGET.length());
            path = path + (type == TYPE.TEST ? "test-classes" : "classes") + SLASH;
            if (!paths.contains(path)) {
                paths.add(path);
            }
        }
        for (String path : paths) {
            File file = new File(path);
            System.out.println("PATH " + file.getAbsolutePath());
            classes.addAll(find(file, scannedPackage));
        }
        return classes;
    }

    private static List<Class<?>> find(final File file, final String scannedPackage) {
        final List<Class<?>> classes = new LinkedList<Class<?>>();
        if (file.isDirectory()) {
            for (File nestedFile : file.listFiles()) {
                classes.addAll(find(nestedFile, scannedPackage));
            }
            //File names with the $1, $2 holds the anonymous inner classes, we are not interested on them.
        } else if (file.getName().endsWith(CLASS_SUFFIX) && !file.getName().contains("$")) {

            final int beginIndex = 0;
            final int endIndex = file.getName().length() - CLASS_SUFFIX.length();
            final String className = file.getName().substring(beginIndex, endIndex);
            try {
                final String resource = scannedPackage + DOT + className;
                classes.add(Class.forName(resource));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return classes;
    }

}
