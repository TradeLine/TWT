package org.tlsys.twt

import org.objectweb.asm.Opcodes
import org.objectweb.asm.signature.SignatureReader
import org.objectweb.asm.signature.SignatureVisitor
import java.util.*

open class TypeID(val sinature: String) {
    private var ar: ArrayOf? = null
    fun asArray(): ArrayOf {
        if (ar == null)
            ar = ArrayOf(this)
        return ar!!
    }
}

val UNKNOWN_TYPE = TypeID("UNKNOWN")

class ArrayOf(type: TypeID) : TypeID(type.sinature)

class Primitive private constructor(val text: String, sinature: String) : TypeID(sinature) {
    companion object {
        private val created = HashMap<Char, Primitive>()

        init {
            created['V'] = Primitive("void", "V")
            created['Z'] = Primitive("boolean", "Z")
            created['B'] = Primitive("byte", "B")
            created['C'] = Primitive("char", "C")
            created['S'] = Primitive("short", "S")
            created['I'] = Primitive("int", "I")
            created['J'] = Primitive("long", "J")
            created['F'] = Primitive("float", "F")
            created['D'] = Primitive("double", "D")
        }

        fun get(signature: Char) = created[signature]!!

        fun getByOpcode(opcode: Int): Primitive {
            return when (opcode) {
                Opcodes.IADD,
                Opcodes.IMUL,
                Opcodes.IDIV,
                Opcodes.ICONST_M1,
                Opcodes.ICONST_0,
                Opcodes.ICONST_1,
                Opcodes.ICONST_2,
                Opcodes.ICONST_3,
                Opcodes.ICONST_4,
                Opcodes.ICONST_5,
                Opcodes.F2I,
                Opcodes.D2I,
                Opcodes.L2I,
                Opcodes.ISUB -> get('I')

                Opcodes.FADD,
                Opcodes.FMUL,
                Opcodes.FDIV,
                Opcodes.FCONST_0,
                Opcodes.FCONST_1,
                Opcodes.FCONST_2,
                Opcodes.D2F,
                Opcodes.I2F,
                Opcodes.L2F -> get('F')

                Opcodes.F2D,
                Opcodes.I2D,
                Opcodes.L2D,
                Opcodes.DADD,
                Opcodes.DMUL,
                Opcodes.DDIV,
                Opcodes.DSUB,
                Opcodes.DCONST_0,
                Opcodes.DCONST_1 -> get('D')

                else -> throw IllegalArgumentException("Unknown opcode $opcode. Can't get value type")
            }
        }
    }
}

class ClassRef private constructor(sinature: String) : TypeID(sinature) {
    companion object {
        private val created = HashMap<String, ClassRef>()

        fun get(signature: String): ClassRef {
            val g = created[signature]
            if (g != null)
                return g
            val c = ClassRef(signature)
            created.put(signature, c)
            return c
        }
    }
}


class SReader : SignatureVisitor {
    constructor() : super(Opcodes.ASM5) {
    }

    companion object {
        fun parse(signature: String): SReader {
            val r = SReader()
            SignatureReader(signature).accept(r)
            return r
        }
    }

    val params = ArrayList<TypeID>()
    var ret: TypeID = UNKNOWN_TYPE

    override fun visitReturnType(): SignatureVisitor {
        return object : SignatureVisitor(Opcodes.ASM5) {
            var arr: Int = 0
            override fun visitArrayType(): SignatureVisitor {
                arr++
                return super.visitArrayType()
            }

            override fun visitClassType(name: String?) {
                var t: TypeID = ClassRef.get(name!!)
                while (arr > 0) {
                    arr--
                    t = t.asArray()
                }
                ret = t
            }

            override fun visitBaseType(descriptor: Char) {
                var t: TypeID? = Primitive.get(descriptor)
                if (t == null)
                    throw RuntimeException("Type $descriptor not found")
                while (arr > 0) {
                    arr--
                    t = t!!.asArray()
                }
                ret = t!!
            }
        }
    }

    override fun visitParameterType(): SignatureVisitor {
        val g = params.size
        params.add(UNKNOWN_TYPE)
        return object : SignatureVisitor(Opcodes.ASM5) {
            var arr: Int = 0
            override fun visitArrayType(): SignatureVisitor {
                arr++
                return super.visitArrayType()
            }

            override fun visitClassType(name: String?) {
                var t: TypeID = ClassRef.get(name!!)
                while (arr > 0) {
                    arr--
                    t = t.asArray()
                }
                params[g] = t
            }

            override fun visitBaseType(descriptor: Char) {
                var t: TypeID? = Primitive.get(descriptor)
                if (t == null)
                    throw RuntimeException("Type $descriptor not found")
                while (arr > 0) {
                    arr--
                    t = t!!.asArray()
                }
                params[g] = t!!
            }
        }
    }
}
