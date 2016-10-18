package org.tlsys.twt.compil;

import org.tlsys.lex.declare.VClass;

import java.io.*;
import java.util.Optional;

public interface Compiller {
    void add(String name, String data);
    default void add(String name, InputStream stream) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[512];

        while ((len = stream.read(buffer)) != -1) {
            b.write(buffer, 0, len);
        }

        add(name, new String(b.toByteArray()));
    }
    default void add(File file) throws IOException {
        try (FileInputStream f = new FileInputStream(file)) {
            add(file.getAbsolutePath(), f);
        }
    }

    Optional<VClass> getClass(String name);
}
