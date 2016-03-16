package org.tlsys.twt;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.tlsys.lex.declare.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;

public class PomClassLoader extends URLClassLoader {

    private static final Logger LOG = Logger.getLogger(PomClassLoader.class.getName());
    /*
        public PomClassLoader(URL url) {
            super(new URL[]{url}, null);
        }
        */
    public final ArrayList<PomClassLoader> parents = new ArrayList<>();
    //private final PomLoader pomLoader;
    private File jarFile;
    private boolean haveJSLib = true;
    private VClassLoader jsClassLoader;
    private String name;

    /*
    public PomClassLoader(File file, PomLoader pomLoader) throws IOException, XmlPullParserException {
        super(new URL[]{}, null);
        this.pomLoader = pomLoader;
        if (file.getName().endsWith(".xml") || file.getName().endsWith(".pom"))
            setJarFile(file);
        else {
            haveJSLib = false;
            addURL(file.toURI().toURL());
        }
    }
*/
    public PomClassLoader(ArtifactRepository localRepo, DependencyNode node, File file, String name, PomLoader pomLoader) {
        super(new URL[]{}, null);
        haveJSLib = false;
        jarFile = file;
        try {
            addURL(jarFile.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        this.name = name;
        pomLoader.add(this);

        includeDependency(localRepo, pomLoader, node.getChildren());
        getJSClassLoader();
    }

    public PomClassLoader(ArtifactRepository localRepo, DependencyNode node, PomLoader pomLoader) {
        super(new URL[]{}, null);

        if (node.getArtifact().getFile() == null) {
            String s = localRepo.getBasedir()+File.separator+localRepo.pathOf(node.getArtifact());
            node.getArtifact().setFile(new File(s));
        }

        jarFile = node.getArtifact().getFile();
        if (jarFile == null)
            System.out.println("123");
        try {
            addURL(jarFile.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        name = node.getArtifact().getGroupId() + "-" + node.getArtifact().getArtifactId() + "-" + node.getArtifact().getVersion();
        pomLoader.add(this);

        includeDependency(localRepo, pomLoader, node.getChildren());
        getJSClassLoader();
    }

    private void includeDependency(ArtifactRepository localRepo, PomLoader pomLoader, Collection<DependencyNode> nodes) {
        for (DependencyNode dn : nodes) {
            if ("test".equals(dn.getArtifact().getScope()))
                continue;
            String n = dn.getArtifact().getGroupId() + "-" + dn.getArtifact().getArtifactId() + "-" + dn.getArtifact().getVersion();
            PomClassLoader pcl = pomLoader.get(n);
            if (pcl == null) {
                pcl = new PomClassLoader(localRepo, dn, pomLoader);
            }
            parents.add(pcl);
        }
    }

    public String getName() {
        return name;
    }
/*
    public PomClassLoader(MavenProject project, File jarFile, PomLoader pomLoader) throws IOException, XmlPullParserException {
        super(new URL[]{}, null);

        setMavenProject(project, jarFile);

        this.pomLoader = pomLoader;
    }
    */

    /*
    public PomClassLoader(String artifacte, String group, String version, File localRepo) throws IOException, XmlPullParserException {
        super(new URL[]{}, null);
        setJarFile(new File(getFileMaven(artifacte, group, version, localRepo) + ".pom"));
    }
    */


    /*
    private void setMavenProject(MavenProject project, File file) throws IOException, XmlPullParserException {
        name = project.getArtifactId() + "-" + project.getGroupId() + "-" + project.getVersion();
        jarFile = file;
        if (jarFile.getName().equals("pom.xml")) {
            if (project.getPackaging() != null && project.getPackaging().equals("war")) {
                jarFile = new File(jarFile.getParent() + File.separator + "target" + File.separator + project.getBuild().getFinalName());
            } else {
                jarFile = new File(jarFile.getParent() + File.separator + "target" + File.separator + "classes");
            }
        } else {
            jarFile = new File(jarFile.getParent() + File.separator + project.getArtifactId() + "-" + project.getVersion() + ".jar");
        }

        for (Dependency d : project.getDependencies()) {
            if ("system".equals(d.getScope())) {
                String path = d.getSystemPath();
                Pattern p = Pattern.compile("\\$\\{[a-z,A-Z,\\.]+\\}");
                do {
                    Matcher m = p.matcher(path);
                    if (!m.find())
                        break;
                    String b = m.group().substring(2,m.group().length()-1);
                    path = m.replaceFirst(System.getProperty(b).replace("\\","\\\\"));
                } while (true);
                PomClassLoader pcl = pomLoader.load(new File(path));
                parents.add(pcl);
            } else {
                PomClassLoader pcl = pomLoader.load(d.getArtifactId(), d.getGroupId(), d.getVersion());
                parents.add(pcl);
            }
        }


        addURL(jarFile.toURI().toURL());
        this.jarFile = jarFile;
    }
    */

/*
    private void setJarFile(File jarFile) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model mod = reader.read(new FileReader(jarFile), true);
        MavenProject project = new MavenProject(mod);

        setMavenProject(project, jarFile);
    }
    */

    public void setJsClassLoader(VClassLoader jsClassLoader) {
        this.jsClassLoader = Objects.requireNonNull(jsClassLoader);
        haveJSLib = true;
    }

    public File getJarFile() {
        return jarFile;
    }



    @Override
    public String toString() {
        return "PomClassLoader{" +
                "jarFile=" + jarFile +
                '}';
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        name = name.trim();
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {

        }
        try {
            Class cl = super.loadClass(name);
            LOG.info("---loaded " + name + " from " + this.name);
            return cl;
        } catch (ClassNotFoundException e) {
            LOG.info("---Class " + name + " not found in " + this.name);
        }
        for (ClassLoader cl : parents) {
            try {
                return cl.loadClass(name);
            } catch (ClassNotFoundException e) {
                //
            }
        }
        return getSystemClassLoader().loadClass(name);
    }


    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        final HashSet<URL> urls = new HashSet<>();
        for (ClassLoader cl : parents) {
            urls.addAll(Collections.list(cl.getResources(name)));
        }
        urls.addAll(Collections.list(super.getResources(name)));
        urls.addAll(Collections.list(getSystemClassLoader().getResources(name)));
        return Collections.enumeration(urls);
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        final HashSet<URL> urls = new HashSet<>();
        for (PomClassLoader cl : parents) {
            urls.addAll(Collections.list(cl.findResources(name)));
        }
        urls.addAll(Collections.list(super.findResources(name)));
        return Collections.enumeration(urls);
    }
}
