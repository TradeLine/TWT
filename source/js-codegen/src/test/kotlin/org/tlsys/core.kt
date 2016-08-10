package org.tlsys

import org.tlsys.edge.VarianValue
import org.tlsys.node.Node
import java.util.*

open class Operation : Node() {
    open fun freeUsingValues() {

    }

    open fun replaceUsingValue(old: Expression, new: Expression) {
    }
}

open class Expression : Operation() {
    private val _usingVariants = HashSet<VarianValue>()
    private val _usingOperation = HashSet<Operation>()


    fun replace(new: Expression) {
        for (o in usingOperation.toList().toTypedArray()) {
            o.replaceUsingValue(this, new)
        }


        for (v in usingVariants) {
            v -= this
            v += new
        }

        for (s in usingSteck) {
            s.steck.replace(this, new)
        }

    }

    val usingVariants: Set<VarianValue>
        get() = _usingVariants

    val usingOperation: Set<Operation>
        get() = _usingOperation

    fun use(e: Operation) {
        _usingOperation += e
    }

    fun unuse(e: Operation) {
        _usingOperation -= e
    }

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
        //if (this in b.steck)
        //throw IllegalStateException("When called this method value from stack must was removed")
        _usingSteck -= b
    }

    fun unref(varianValue: VarianValue) {
        _usingVariants -= varianValue
    }
}