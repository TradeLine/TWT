package org.tlsys

import org.tlsys.compiler.Compile
import org.tlsys.compiler.ast.TypeDeclaration
import org.tlsys.compiler.parser.Parser
import org.tlsys.compiler.utils.ClassUnit
import org.tlsys.twt.nodes.TClass

import java.io.InputStream

/**
 * Created by Субочев Антон on 20.06.2016.
 */
object TWTCompiler {
    fun compile(classFile: InputStream): CompileResult {
        val cu = Compile.getInstance().getOrCreateClassUnit("", classFile)
        val p = Parser(cu)
        val td = p.parse()
        val mv = TWTClassVisiter()
        td.visit(mv)
        return CompileResult(mv.result, arrayOfNulls<TClass>(0))
    }
}
