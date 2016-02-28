package org.tlsys.twt;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class PomFile {
    private PomFile() {
    }

    public static Set<Dependency> getDependecy(File file) throws IOException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try(FileInputStream fin = new FileInputStream(file)) {
            Model model = reader.read(fin);
            Set<Dependency> out = new HashSet<>();

            for (org.apache.maven.model.Dependency d : model.getDependencies()) {
                Scope scope = Scope.fromString(d.getScope());
                Dependency dd;
                if (scope == Scope.SYSTEM) {
                    dd = new JarDependency(scope, new File(d.getSystemPath()));
                } else {
                    dd = new ArtifactDependency(scope, d.getArtifactId(), d.getGroupId(), d.getVersion());
                }

                out.add(dd);
            }

            return out;
        } catch (org.codehaus.plexus.util.xml.pull.XmlPullParserException e) {
            throw new IOException(e);
        }
    }

    public static class Dependency {
        private final Scope scope;

        public Dependency(Scope scope) {
            this.scope = scope;
        }

        public Scope getScope() {
            return scope;
        }
    }

    public static class JarDependency extends Dependency {
        private final File jar;

        public JarDependency(Scope scope, File jar) {
            super(scope);
            this.jar = jar;
        }

        public File getJar() {
            return jar;
        }

        @Override
        public String toString() {
            return "JarDependency{" + "jar=" + jar + '}';
        }
        
        
    }

    public static class ArtifactDependency extends Dependency {
        private final String name;
        private final String group;
        private final String version;

        public ArtifactDependency(Scope scope, String name, String group, String version) {
            super(scope);
            this.name = name;
            this.group = group;
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public String getGroup() {
            return group;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "ArtifactDependency{" + "name=" + name + ", group=" + group + ", version=" + version + '}';
        }
        
        
    }

    public enum Scope {
        JAR,COMPILE,RUNTIME,SYSTEM,TEST,PROVIDED;

        public static Scope fromString(String scope) {
            if (scope == null || scope.isEmpty() || scope.equals("compile"))
                return COMPILE;
            if (scope.equals("jar"))
                return JAR;
            if (scope.equals("runtime"))
                return RUNTIME;
            if (scope.equals("system"))
                return SYSTEM;
            if (scope.equals("test"))
                return TEST;
            if (scope.equals("provided"))
                return PROVIDED;


            throw new IllegalArgumentException("Unknown scope \"" + scope + "\"");
        }
    }
}
