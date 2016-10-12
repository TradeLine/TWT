package ggg

import org.junit.Test
import org.objectweb.asm.*
import org.tlsys.BBB
import org.tlsys.ClassRef
import org.tlsys.Program
import org.tlsys.ThisRef
import org.tlsys.node.SReader
import java.util.*

class Start {

    @Test
    fun start() {
        val cr = ClassReader(BBB::class.java.name)
        val v = ClassV()
        cr.accept(v, 0)
    }
}

class ClassV : ClassVisitor(Opcodes.ASM5) {
    val program = ArrayList<Program>()
    lateinit var type: ClassRef

    override fun visitAttribute(p0: Attribute?) {
        super.visitAttribute(p0)
    }

    override fun visitInnerClass(p0: String?, p1: String?, p2: String?, p3: Int) {
        super.visitInnerClass(p0, p1, p2, p3)
    }

    override fun visitSource(p0: String?, p1: String?) {
        super.visitSource(p0, p1)
    }

    override fun visitOuterClass(p0: String?, p1: String?, p2: String?) {
        super.visitOuterClass(p0, p1, p2)
    }

    override fun visit(p0: Int, p1: Int, classSignature: String, p3: String?, superClassSignature: String?, p5: Array<out String>?) {
        type = ClassRef.get(classSignature)
    }

    override fun visitField(p0: Int, p1: String?, p2: String?, p3: String?, p4: Any?): FieldVisitor {
        return super.visitField(p0, p1, p2, p3, p4)
    }

    override fun visitEnd() {
        super.visitEnd()
    }

    override fun visitAnnotation(p0: String?, p1: Boolean): AnnotationVisitor? {
        return super.visitAnnotation(p0, p1)
    }

    override fun visitTypeAnnotation(p0: Int, p1: TypePath?, p2: String?, p3: Boolean): AnnotationVisitor {
        return super.visitTypeAnnotation(p0, p1, p2, p3)
    }

    override fun visitMethod(access: Int, name: String?, desc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor? {

        val method = JMethod()
        val m = MethodParser(method)

        val staticMethod = access and Opcodes.ACC_STATIC != 0

        val g = SReader.parse(desc)

        if (!staticMethod) {
            val g = method.createArg(0, type)
            method.entryBlock += SetVar(g.first(InitValue(type)))
        }

        for (i in 0..g.params.size - 1) {
            val v = method.createArg(i + if (staticMethod) 0 else 1, g.params[i])
            method.entryBlock += SetVar(v.first(InitValue(g.params[i])))
        }

        return m
    }
}