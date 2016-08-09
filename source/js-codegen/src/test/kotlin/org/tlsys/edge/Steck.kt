package org.tlsys.edge

import org.tlsys.BaseBlock
import org.tlsys.Expression
import java.util.*

fun <T> List<T>.indexOf(f: (T) -> Boolean): Int {
    val t = listIterator()
    while (t.hasNext()) {
        val i = t.nextIndex()
        if (f(t.next()))
            return i
    }
    return -1
}

fun <T> List<T>.contains(f: (T) -> Boolean): Boolean {

    val t = listIterator()
    while (t.hasNext()) {
        if (f(t.next()))
            return true
    }
    return false
}

fun <T> MutableList<T>.removeIf(f: (T) -> Boolean) {

    val t = listIterator()
    while (t.hasNext()) {
        if (f(t.next()))
            t.remove()
    }
}

class ValueSteck {

    class StackRecord(val value: Expression, val marged: Boolean)

    private val steck = LinkedList<StackRecord>()
    fun push(value: Expression) {
        steck.push(StackRecord(value, false))
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
                    i1.set(StackRecord(marge1(block, i1.next().value, v2.value), true))
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
        g.value.unsteck(block)
        return g.value
    }

    val size: Int get() = steck.size
    operator fun contains(e: Expression) = steck.contains { it.value == e }

    val addedSize: Int get() = steck.count { !it.marged }

    fun remove(e: Expression) {
        val f = steck.indexOf {
            it.value == e
        }
        if (f >= 0) {
            steck.removeAt(f)
            e.unsteck(block)
        }
    }

    fun isEmpty() = steck.isEmpty()

    fun toList() = steck.toList()

    fun getOne(f: (StackRecord) -> Boolean): Expression {
        val g = steck.filter(f)

        if (g.isEmpty())
            throw RuntimeException("No Value")
        if (g.size != 1)
            throw RuntimeException("Same more value")

        return g.first().value
    }

    fun getOne(): Expression {
        if (isEmpty())
            throw RuntimeException("No Value")
        if (size != 1)
            throw RuntimeException("Same more value")

        return steck.first.value
    }
}

class VarValue(val index: Int) : Expression() {
    override fun toString(): String {
        return "V$index"
    }
}

class IntValue(val value: Int) : Expression() {
    override fun toString(): String {
        return "$value"
    }
}

class LdcValue(val value: Any?) : Expression() {
    override fun toString(): String {
        return "\"$value\""
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
        return "[$list]"
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

fun marge1(block: BaseBlock, v1: Expression, v2: Expression): VarianValue {
    if (v1 is VarianValue)
    //if (v1.block == block)
    {
        if (v2 is VarianValue) {
        } else
            v1 += v2
        return v1
    }
    return VarianValue(v1, v2)
}