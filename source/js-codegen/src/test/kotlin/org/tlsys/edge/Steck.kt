package org.tlsys.edge

import org.tlsys.BaseBlock
import org.tlsys.Expression
import org.tlsys.Var
import org.tlsys.node.PhiFunction
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
    fun iterator() = steck.listIterator()

    class StackRecord(val value: Expression, val marged: Boolean)

    private val steck = LinkedList<StackRecord>()
    fun push(value: Expression) {
        steck.push(StackRecord(value, false))
        value.steck(block)
        println("PUSH $value")

        steck.listIterator()
    }

    val block: BaseBlock

    fun convertToVar(list: Array<Var>) {
        val it = iterator()
        val itList = list.iterator()
        while (it.hasNext()) {
            val g = it.next()
            val t = itList.next()
            g.value.unsteck(block)
            val s = t.set(g.value, block)
            val get = s.item.get()
            it.set(StackRecord(get, g.marged))
            get.steck(block)
            block += s
        }
    }

    fun convertToVar(): Array<Var> {
        val it = iterator()
        val values = ArrayList<Var>()
        while (it.hasNext()) {
            val g = it.next()
            val t = block.program.createTempVar()
            val s = t.set(g.value, block)
            val get = s.item.get()
            g.value.unsteck(block)

            it.set(StackRecord(get, g.marged))
            get.steck(block)
            block += s
            values += t
        }
        return values.toTypedArray()
    }

    companion object {

        fun getMarged(vararg list: ValueSteck): Array<PhiFunction> {
            if (list.size < 2)
                throw RuntimeException("Bad stack marge! Bad size! ${list.size}")
            val l = list[0].size
            list.forEach {
                if (it.size != l)
                    throw RuntimeException("Difirent stack!")
            }

            val itList = ArrayList<MutableListIterator<StackRecord>>()

            for (g in list)
                itList += g.iterator()

            return Array(l){
                val ll = ArrayList<Var.VarVariantValue>(l)
                for (g in itList) {
                    ll+=(g.next().value as Var.GetVar).item
                }
                PhiFunction(ll)
            }
        }

        fun marge(vararg other: ValueSteck): Array<Var.GetVar> {
            if (other.isEmpty())
                return arrayOf()

            val g = other[0].size
            other.forEach {
                if (it.size != g)
                    throw RuntimeException("Bad Stack Size!")
            }




            val itList = ArrayList<MutableListIterator<StackRecord>>()

            for (g in other)
                itList += g.iterator()

            val out = ArrayList<Var.GetVar>(other.size)

            for (i in 0..other[0].size - 1) {
                val v = other[0].block.program.createTempVar()

                for (g in 0..other.size - 1) {
                    val value = itList[g].next()
                    val setOp = v.set(value.value, other[g].block)
                    other[g].block += setOp
                    val getOp = setOp.item.get()
                    out += getOp
                    itList[g].set(StackRecord(getOp, value.marged))
                }
            }
            return out.toTypedArray()
        }
    }

    fun copyFrom(steck: ValueSteck) {
        val it = steck.iterator()
        while (it.hasNext()) {
            this.steck.addLast(StackRecord(it.next().value, true))
        }
    }


    fun marge(vararg other: ValueSteck) {
        val values = Companion.marge(*other)

        values.forEach {
            steck.addFirst(StackRecord(it, true))
        }

        /*
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
        */
    }


    constructor(baseBlock: BaseBlock) {
        this.block = baseBlock
    }


    fun pop(): Expression {
        val g = steck.pop()
        g.value.unsteck(block)
        return g.value
    }

    fun peek(): Expression {
        return steck.peek().value
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

    fun replace(expression: Expression, new: Expression) {
        val i = steck.listIterator()
        while (i.hasNext()) {
            val g = i.next()
            if (g.value === expression) {
                i.set(StackRecord(new, g.marged))
                new.steck(block)
                g.value.unsteck(block)
            }
        }
    }

    fun clear() {
        val it = iterator()
        while (it.hasNext()) {
            val g = it.next()
            it.remove()
            g.value.unsteck(block)
        }
    }
}

/*
class VarValue(val index: Int) : Expression() {
    override fun toString(): String {
        return "V$index"
    }
}*/

class IntValue(val value: Int) : Expression() {
    override fun toString(): String {
        return "$value"
    }

    override val constValue: Boolean
        get() = true
}

class LdcValue(val value: Any?) : Expression() {
    override fun toString(): String {
        return "\"$value\""
    }

    override val constValue: Boolean
        get() = true
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
    if (v1 === v2 && v1 is VarianValue)
        return v1
    if (v1 is VarianValue)
    //if (v1.block == block)
    {
        if (v2 is VarianValue) {
            for (v in v2.list)
                v1 += v
        } else
            v1 += v2
        return v1
    }
    return VarianValue(v1, v2)
}