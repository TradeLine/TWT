package org.tlsys

import org.objectweb.asm.Opcodes
import java.util.*

open class TypeID(val sinature: String) {
    private var ar: ArrayOf? = null
    fun asArray(): ArrayOf {
        if (ar == null)
            ar = ArrayOf(this)
        return ar!!
    }
}

val UNKNOWN_TYPE = TypeID("UNKNOWN");

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

        fun get(signature: Char) = created[signature]

        fun getByOpcode(opcode: Int): Primitive? {
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
                Opcodes.ISUB-> get('I')
                else -> TODO()
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

