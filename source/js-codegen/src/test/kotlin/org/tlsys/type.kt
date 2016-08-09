package org.tlsys

import java.util.*

open class TypeID(val sinature: String) {
    private var ar:ArrayOf?=null
    fun asArray():ArrayOf {
        if (ar == null)
            ar = ArrayOf(this)
        return ar!!
    }
}

val UNKNOWN_TYPE=TypeID("UNKNOWN");

class ArrayOf(type:TypeID):TypeID(type.sinature)

class Primitive private constructor(val text: String, sinature: String) : TypeID(sinature) {
    companion object {
        private val created = HashMap<Char, Primitive>()

        init {
            created['V'] = Primitive("void", "V")
            created['Z'] = Primitive("boolean", "Z")
            created['I'] = Primitive("byte", "B")
            created['I'] = Primitive("char", "C")
            created['I'] = Primitive("short", "S")
            created['I'] = Primitive("int", "I")
            created['J'] = Primitive("long", "J")
            created['F'] = Primitive("float", "F")
            created['D'] = Primitive("double", "D")
        }

        fun get(signature: Char) = created[signature]
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

