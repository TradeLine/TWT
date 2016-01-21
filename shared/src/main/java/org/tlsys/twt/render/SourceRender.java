package org.tlsys.twt.render;

import com.sun.source.tree.CompilationUnitTree;
import org.tlsys.twt.ClassAliaseProvider;
import org.tlsys.twt.desc.ClassDesc;

import java.io.OutputStream;

public interface SourceRender {
    public ClassDesc[] render(ClassAliaseProvider classAliaseProvider, ClassLoader appClassLoader, CompilationUnitTree[] files) throws Throwable;
    public void render(ClassAliaseProvider classAliaseProvider, ClassLoader appClassLoader, ClassDesc[] classes, OutputStream stream) throws Throwable;
}
