package org.tlsys.twt;

import java.util.HashMap;

public class PomLoader {

    //private static final File mavenRepo = new File(System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository");

    private final HashMap<String, PomClassLoader> loaded = new HashMap<>();

    public PomClassLoader get(String name) {
        return loaded.get(name);
    }

    public void add(PomClassLoader loader) {
        loaded.put(loader.getName(), loader);
    }

/*
    public PomLoader(PomProvider pomProvider) {
        this.pomProvider = pomProvider;
    }

    public PomClassLoader load(String artifacte,String group, String version) throws IOException, XmlPullParserException {
        File pom = pomProvider.getPomFor(group, artifacte, version);
        return load(pom);
        //return load(new File(getFileMaven(artifacte, group, version, mavenRepo)+".pom"));
    }

    public interface PomProvider {
        public File getPomFor(String group, String artifact, String version);
    }

    private final PomProvider pomProvider;

    public PomClassLoader load(File pom) throws IOException, XmlPullParserException {
        pom = new File(pom.toURI().normalize());
        if (!pom.isFile())
            return null;
        if (loaded.containsKey(pom)) {
            return loaded.get(pom);
        }
        PomClassLoader pl = new PomClassLoader(pom, this);
        loaded.put(pom, pl);
        return pl;
    }

    private static String getFileMaven(String artifacte, String group, String version, File localRepo) {
        String arFile = localRepo + File.separator + group.replace('.', File.separatorChar) + File.separator + artifacte + File.separator;
        if (version == null) {
            String[] versions = new File(arFile).list();
            Arrays.sort(versions, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return calcVersion(o1) - calcVersion(o2);
                }
            });
            version = versions[0];
        }
        return arFile + version + File.separator + artifacte + "-" + version;
    }

    private static int calcVersion(String t) {
        String[] tt = t.split("\\.");
        int res = 0;
        for (int i = t.length() - 1; i >= 0; i++) {
            res += Integer.parseInt(tt[i]) * (i+1);
        }
        return res;
    }
    */
}
