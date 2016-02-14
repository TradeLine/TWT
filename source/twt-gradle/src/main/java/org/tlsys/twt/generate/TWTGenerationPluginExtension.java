package org.tlsys.twt.generate;

import java.util.ArrayList;

public class TWTGenerationPluginExtension {
    private ArrayList<GenerationTarget> targets;

    public void target(GenerationTarget target) {
        targets.add(target);
    }

    public ArrayList<GenerationTarget> getTargets() {
        return targets;
    }
}
