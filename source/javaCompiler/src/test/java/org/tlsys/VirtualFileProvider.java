package org.tlsys;

import org.tlsys.twt.SourceProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

public class VirtualFileProvider implements SourceProvider {

    private final VDir root = new VDir(null);

    public VDir getRoot() {
        return root;
    }

    @Override
    public Optional<InputStream> getFile(String name) {
        String[] list = name.split("/");
        VEntity e = root;

        for (String s : list) {
            if (e instanceof VDir) {
                Optional<VEntity> ee = ((VDir) e).get(s);
                if (!ee.isPresent())
                    return Optional.empty();
                e = ee.get();
            }
        }
        if (e instanceof VFile) {
            byte[] data = ((VFile) e).data;
            return Optional.of(new ByteArrayInputStream(data));
        }
        return Optional.empty();
    }

    public class VEntity {
        private final String name;

        public VEntity(String name) {
            this.name = name;
        }
    }

    public class VFile extends VEntity {
        private final byte[] data;

        public VFile(String name, byte[] data) {
            super(name);
            this.data = data;
        }
    }

    public class VDir extends VEntity {
        private final ArrayList<VEntity> list = new ArrayList<>();

        public VDir(String name) {
            super(name);
        }

        public Optional<VEntity> get(String name) {
            for (VEntity e : list) {
                if (e.name.equals(name))
                    return Optional.of(e);
            }
            return Optional.empty();
        }

        public VDir dir(String name) {
            Optional<VEntity> o = list.stream().filter(e -> e instanceof VDir && e.name.equals(name)).findAny();
            if (o.isPresent())
                return (VDir) o.get();
            VDir d = new VDir(name);
            list.add(d);
            return d;
        }

        public VFile file(String name, byte[] data) {
            VFile d = new VFile(name, data);
            list.add(d);
            return d;
        }

    }
}
