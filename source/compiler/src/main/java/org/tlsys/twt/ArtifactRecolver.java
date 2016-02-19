package org.tlsys.twt;

import java.io.File;
import java.util.*;

public class ArtifactRecolver {
    private Set<File> pathes = new HashSet<>();
    public void addPath(File file) {
        pathes.add(file);
    }

    public ArtifactRecord getArtifacte(String name, String group, String version) throws ArtifactNotFoundException {
        for (File f : pathes) {
            Optional<ArtifactRecord> ar = find(f, name, group, version);
            if (ar.isPresent())
                return ar.get();
        }

        throw new ArtifactNotFoundException(name, group, version);
    }

    private static Optional<ArtifactRecord> find(File repo, String name, String group, String version) {
        File ar = new File(repo + File.separator + group.replace('.',File.separatorChar)+File.separator+name);
        if (!ar.isDirectory())
            return Optional.empty();
        File[] versions = ar.listFiles(pathname -> pathname.isDirectory());
        Optional<String> ver = selectVersion(versions, version);
        if (!ver.isPresent())
            return Optional.empty();
        version = ver.get();
        ar = new File(ar + File.separator + version);
        String prifix = ar+File.separator+name+"-"+version;
        return Optional.of(new ArtifactRecord(new File(prifix+".jar"), new File(prifix+".pom")));
    }

    private static Optional<String> selectVersion(File[] all, String version) {
        List<File> list =  Arrays.asList(all);
        Optional<File> f = list.stream().filter(e->e.getName().equals(version)).findFirst();
        if (f.isPresent()) {
            return Optional.of(f.get().getName());
        }
        return Optional.empty();
    }
}
