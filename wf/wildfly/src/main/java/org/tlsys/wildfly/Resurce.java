package org.tlsys.wildfly;

/**
 * Created by caffeine on 15.02.2016.
 */
public class Resurce {
    private String name;
    byte[] hash;
    boolean enabled;
    private String runtimeName;

    public Resurce(String name, byte[] hash, boolean enabled, String runtimeName) {
        if (hash != null && hash.length != 20)
            throw new IllegalArgumentException("Hash length must be 20");
        this.name = name;
        this.hash = hash;
        this.enabled = enabled;
        this.runtimeName = runtimeName;
    }

    public Resurce() {
        this(null, null, false, null);
    }

    public String getName() {
        return name;
    }

    public byte[] getHash() {
        return hash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getRuntimeName() {
        return runtimeName;
    }

    @Override
    public String toString() {
        return "Resurce{" + "name=" + name + ", hash=" + hash + ", enabled=" + enabled + ", runtimeName=" + runtimeName + '}';
    }
}
