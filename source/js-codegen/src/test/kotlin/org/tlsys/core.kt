package org.tlsys

import org.tlsys.edge.VarianValue
import org.tlsys.node.Node
import java.util.*

open class Operation: Node()
open class Expression : Operation() {
    private val _usingVariants = HashSet<VarianValue>()

    val usingVariants: Set<VarianValue>
        get() = _usingVariants

    fun ref(v: VarianValue) {
        _usingVariants += v
    }

    private val _usingSteck = HashSet<BaseBlock>()

    val usingSteck: Set<BaseBlock>
        get() = _usingSteck


    fun steck(b: BaseBlock) {
        if (this !in b.steck)
            throw IllegalStateException("When called this method value from stack must was added")
        _usingSteck += b
    }

    fun unsteck(b: BaseBlock) {
        if (this in b.steck)
            throw IllegalStateException("When called this method value from stack must was removed")
        _usingSteck -= b
    }

    fun unref(varianValue: VarianValue) {
        _usingVariants-=varianValue
    }
}