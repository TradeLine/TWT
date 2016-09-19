package org.tlsys

import org.tlsys.node.PhiFunction
import java.util.*

abstract open class Var {
    val variants = ArrayList<VarVariantValue>()
    fun set(value: Expression, block: BaseBlock): SetVar {
        val index = variants.size
        val v = VarVariantValue(this, index, value, block)
        variants += v
        return SetVar(v)
    }

    class SetVar internal constructor(val item: VarVariantValue) : Operation() {
        override fun toString(): String {
            return "${item.parent}[${item.index}]=${item.value}"
        }

        init {
            item.value.use(this)
        }

        override fun freeUsingValues() {
            item.value.unuse(this)
        }

        override fun replaceUsingValue(old: Expression, new: Expression) {
            if (old === item.value)
                item.value = new
        }

    }

    class GetVar internal constructor(val item: VarVariantValue) : Expression() {

        override fun toString(): String {
            return item.toString()
        }
    }

    class VarVariantValue(val parent: Var, val index: Int, private var _value: Expression, val block: BaseBlock) {
        override fun toString(): String = "$parent{$index}($value)"

        var value:Expression
        get() = _value
        set(it) {
            _value.unuse(this)
            _value = it
            _value.use(this)
        }

        fun get(): GetVar {
            val g = GetVar(this)
            getters += g
            return g
        }
        val getters = ArrayList<GetVar>()
        val using:Set<Operation>
        get() {
            val h = HashSet<Operation>()
            for (g in getters)
                h+=g.usingOperation
            return h
        }

        val vars:Set<VarVariantValue>
            get() {
                val h = HashSet<VarVariantValue>()
                for (g in getters)
                    h+=g.varValue
                return h
            }
    }

    fun getValueForBlock(from: BaseBlock): VarVariantValue {
        val pathes = HashMap<VarVariantValue, Path>()
        for (v in variants) {
            pathes.put(v, from.getPathLengthTo_UP(v.block) ?: continue)
        }
        if (pathes.isEmpty())
            throw TODO("Почему-то не выяснено значение этой переменной для данного блока, this=$this")
        if (pathes.size == 1)
            return pathes.keys.iterator().next()

        var l = Int.MAX_VALUE
        pathes.values.forEach {
            val h = it.last()
            if (h.level < l)
                l = h.level
        }

        val cleared = pathes.filter {
            val g = it.value.last()
            g.level == l
        }

        if (cleared.size == 1)
            return cleared.keys.iterator().next()


        val dominator = Path.findDominator(cleared.values.toTypedArray())!!

        dominator as OneBlock
        val t = dominator.block.program.createTempVar()

        val s = t.set(PhiFunction(cleared.keys.toMutableList()), dominator.block)
        dominator.block.addFirst(s)
        return s.item
    }
}


class NamedVar(val index: Int) : Var() {
    var name: String = "V$index"
    override fun toString(): String = name
}

class TempVar : Var() {
    override fun toString(): String = "T" + hashCode().toString()
}

class Program {
    val blocks = ArrayList<BaseBlock>()
    var entryBlock: BaseBlock? = null
    //private var tempVarId: Int = 0

    val namedVars = HashMap<Int, NamedVar>()

    val tempVars = ArrayList<TempVar>()

    fun createNamedVar(index: Int): NamedVar {
        if (namedVars.containsKey(index)) {
            throw RuntimeException("Named Var on Index $index already created!")
        }

        val n = NamedVar(index)
        namedVars[index] = n
        return n
    }

    fun getVar(index: Int): NamedVar {
        return namedVars[index] ?: createNamedVar(index)
    }

    fun createTempVar(): TempVar {
        val t = TempVar()
        tempVars += t
        return t
    }

    /*
    fun getTempId(): Int = --tempVarId
    */

    fun createBlock(regin: String = ""): BaseBlock {
        val b = BaseBlock(this, regin)
        if (blocks.isEmpty())
            entryBlock = b
        blocks += b
        return b
    }

    infix operator fun minusAssign(block: BaseBlock) {
        blocks -= block
        block.outEdge.clear()
        block.inEdge.clear()
    }
}