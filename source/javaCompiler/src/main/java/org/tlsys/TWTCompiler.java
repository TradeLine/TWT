package org.tlsys;

import org.tlsys.compiler.Compile;
import org.tlsys.compiler.ast.TypeDeclaration;
import org.tlsys.compiler.parser.Parser;
import org.tlsys.compiler.utils.ClassUnit;
import org.tlsys.twt.nodes.TClass;

import java.io.InputStream;

/**
 * Created by Субочев Антон on 20.06.2016.
 */
public class TWTCompiler {
    public static CompileResult compile(InputStream classFile) {
        ClassUnit cu = Compile.getInstance().getOrCreateClassUnit("", classFile);
        Parser p = new Parser(cu);
        TypeDeclaration td = p.parse();
        TWTClassVisiter mv = new TWTClassVisiter();
        td.visit(mv);
        return new CompileResult(mv.getResult(), new TClass[0]);
    }
}
