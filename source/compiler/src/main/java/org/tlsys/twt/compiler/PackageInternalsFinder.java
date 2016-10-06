package org.tlsys.twt.compiler;

import sun.net.www.protocol.file.FileURLConnection;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class PackageInternalsFinder {
    private final ClassLoader classLoader;
    private static final String CLASS_FILE_EXTENSION = ".class";

    private final Map<String, List<JavaFileObject>> cachePackageEntries = new HashMap<>();

    public PackageInternalsFinder(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public List<JavaFileObject> find(String packageName, boolean recursive) throws IOException {
        System.out.println("Search package " + packageName + "...");
        String javaPackageName = packageName.replaceAll("\\.", "/");

        List<JavaFileObject> result = cachePackageEntries.get(javaPackageName);
        if (result != null) {
            System.out.println("Result " + result);
            return result;
        }

        result = new ArrayList<JavaFileObject>();
        cachePackageEntries.put(javaPackageName, result);

        Enumeration<URL> urlEnumeration = classLoader.getResources(javaPackageName);
        while (urlEnumeration.hasMoreElements()) { // one URL for each jar on the classpath that has the given package
            URL packageFolderURL = urlEnumeration.nextElement();
            result.addAll(listUnder(packageName, packageFolderURL, recursive));
        }

        System.out.println("Result " + result);
        return result;
    }

    private Collection<JavaFileObject> listUnder(String packageName, URL packageFolderURL, boolean recursive) {
        File directory = null;
        try {
            System.out.println("URL=" + packageFolderURL);
            System.out.println("URL.file=" + packageFolderURL.getFile());
            System.out.println("URL.ref=" + packageFolderURL.getRef());
            System.out.println("URL.query=" + packageFolderURL.getQuery());

            directory = new File(packageFolderURL.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        System.out.println("directory=" + directory + ", directory.isDirectory()=" + directory.isDirectory() + ", directory.isFile()=" + directory.isFile());
        if (directory.isDirectory()) { // browse local .class files - useful for local execution
            return processDir(packageName, directory, recursive);
        } else { // browse a jar file
            return processJar(packageFolderURL, directory, recursive);
        } // maybe there can be something else for more involved class loaders
    }

    private void addFileObject(String jarUri, String name, String rootEntryName, int rootEnd, List<JavaFileObject> result, boolean recursive) {
        boolean acceptCurrentFolder = recursive || name.indexOf('/', rootEnd) == -1;
        if (acceptCurrentFolder && name.startsWith(rootEntryName) && name.endsWith(CLASS_FILE_EXTENSION)) {
            URI uri = URI.create(jarUri + "!/" + name);
            String binaryName = name.replaceAll("/", ".");
            binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

            result.add(new CustomJavaFileObject(binaryName, uri));
        }
    }

    private List<JavaFileObject> processJar(URL packageFolderURL, File directory, boolean recursive) {

        System.out.print("Load JAR " + packageFolderURL + "...");

        //System.out.println("PROCESS JAR " + packageFolderURL + ", " + packageFolderURL.getClass());
        List<JavaFileObject> result = new ArrayList<JavaFileObject>();

        URLConnection urlConnection = null;
        try {

            urlConnection = packageFolderURL.openConnection();

            if (urlConnection instanceof FileURLConnection) {
                try {
                    File file = new File(packageFolderURL.toURI());

                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            if (!(urlConnection instanceof JarURLConnection)) {
                System.out.println("BAD CONNECTION TYPE!!! " + urlConnection.getClass().getName());
                urlConnection.getInputStream().close();
                return Collections.emptyList();
            }
            System.out.println();
            JarURLConnection jarConnection = (JarURLConnection) urlConnection;

            String jarUri = packageFolderURL.toExternalForm().split("!")[0];
            JarURLConnection jarConn = (JarURLConnection) urlConnection;
            String rootEntryName = jarConn.getEntryName();
            int rootEnd = rootEntryName.length() + 1;
            try (JarFile file = jarConn.getJarFile()) {
                Enumeration<JarEntry> entryEnum = file.entries();
                System.out.println("try read... " + packageFolderURL);
                int g = 0;
                while (entryEnum.hasMoreElements()) {
                    g++;
                    JarEntry jarEntry = entryEnum.nextElement();
                    String name = jarEntry.getName();
                    addFileObject(jarUri, name, rootEntryName, rootEnd, result, recursive);
                }
                System.out.println("Readed! " + g);

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Wasn't able to open " + packageFolderURL + " as a jar file", e);
        } catch (IllegalStateException e) {
            throw new RuntimeException("Can't read URL " + packageFolderURL, e);
        } finally {
            /*
            if (urlConnection != null)
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            */
        }

        return result;
    }

    private List<JavaFileObject> processDir(String packageName, File directory, boolean recursive) {
        List<JavaFileObject> result = new ArrayList<JavaFileObject>();

        File[] childFiles = directory.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isFile() && childFile.getName().endsWith(CLASS_FILE_EXTENSION)) {
                // We only want the .class files.
                String binaryName = packageName + "." + childFile.getName();
                binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

                result.add(new CustomJavaFileObject(binaryName, childFile.toURI()));
            } else if (recursive && childFile.isDirectory()) {
                result.addAll(processDir(packageName + "." + childFile.getName(), childFile, recursive));
            }
        }

        return result;
    }
}
