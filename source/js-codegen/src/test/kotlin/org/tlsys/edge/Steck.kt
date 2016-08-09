package org.tlsys.edge

import org.tlsys.BaseBlock
import org.tlsys.Expression
import java.util.*

class ValueSteck {
    private val steck = LinkedList<Expression>()
    fun push(value: Expression) {
        steck.push(value)
        value.steck(block)
        println("PUSH $value")
    }

    val block: BaseBlock

    fun marge(vararg other: ValueSteck) {
        for (l in other) {
            println("MARGE STECK ${block.ID} with ${l.block.ID}")
            val i1 = steck.listIterator()
            val i2 = l.steck.listIterator()

            while (i2.hasNext()) {
                val v2 = i2.next()
                if (i1.hasNext()) {
                    i1.set(marge(block, i1.next(), v2))
                } else {
                    i1.add(v2)
                }
            }
        }
    }

    constructor(baseBlock: BaseBlock) {
        this.block = baseBlock
    }


    fun pop(): Expression {
        val g = steck.pop()
        g.unsteck(block)
        return g
    }

    val size: Int get() = steck.size
    operator fun contains(e: Expression) = e in steck

    fun remove(e: Expression) {
        if (steck.remove(e))
            e.unsteck(block)
    }

    fun isEmpty() = steck.isEmpty()

    fun toList() = steck.toList()

    fun getOne(): Expression {
        if (isEmpty())
            throw RuntimeException("No Value")
        if (size != 1)
            throw RuntimeException("Same more value")

        return steck.first
    }
}

class VarValue(val index: Int) : Expression() {
    override fun toString(): String {
        return "V$index"
    }
}

class IntValue(val value: Int) : Expression() {
    override fun toString(): String {
        return "($value from ${block?.ID})"
    }
}

class LdcValue(val value: Any?) : Expression() {
    override fun toString(): String {
        return "LdcValue(value=$value)"
    }
}

class VarianValue(vararg values: Expression) : Expression() {
    private val _list = ArrayList<Expression>()
    val list: List<Expression>
        get() = _list


    operator fun contains(e: Expression) = e in _list

    init {
        for (v in values)
            this += v
    }

    override fun toString(): String {
        return "VarianValue(list=$list)"
    }

    operator fun plusAssign(v: Expression) {
        _list += v
        v.ref(this)
    }

    operator fun minusAssign(left: Expression) {
        _list -= left
        left.unref(this)
    }

}

fun marge(block: BaseBlock, v1: Expression, v2: Expression): VarianValue {
    if (v1 is VarianValue)
        //if (v1.block == block)
        {
            if (v2 is VarianValue) {
            }else
            v1 += v2
            return v1
        }
    return VarianValue(v1, v2)
}