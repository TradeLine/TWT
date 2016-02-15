package org.tlsys.twt.generate;

import java.util.ArrayList;

public class TWTGenerationPluginExtension {
    private ArrayList<GenerationTarget> targets = new ArrayList<>();

    public void target(GenerationTarget target) {
        targets.add(target);
    }

    public GenerationTarget target() {
        GenerationTarget gr = new GenerationTarget();
        target(gr);
        return gr;
    }

    public ArrayList<GenerationTarget> getTargets() {
        return targets;
    }
}
