package org.tlsys.edge

import org.tlsys.BaseBlock
import org.tlsys.Expression
import org.tlsys.node.ConditionNot

open abstract class Edge(from: BaseBlock, to: BaseBlock) {
    abstract fun _from(): String
    abstract fun _to(): String

    private var _from: BaseBlock?=null
    private var _to: BaseBlock?=null

    var from: BaseBlock?
        get() = _from
        set(it) {
            if (_from === it)
                return
            if (_from != null)
                _from!!.outEdge -= this

            _from = it

            if (it != null && this !in it.outEdge)
                it.outEdge += this

        }
    var to: BaseBlock?
        get() = _to
        set(it) {
            if (_to === it)
                return
            if (_to != null)
                _to!!.inEdge -= this

            _to = it

            if (it != null && this !in it.inEdge)
                it.inEdge += this

        }

    fun free() {
        from = null
        to = null
    }

    init {
        this.from = from
        this.to = to
    }
}

class SimpleEdge(from: BaseBlock, to: BaseBlock) : Edge(from, to) {
    override fun toString(): String {
        return "SimpleEdge(from=${from?.ID} to ${to?.ID})"
    }

    override fun _from() = "${from?.ID}"
    override fun _to() = "${to?.ID}"
}

abstract class PairedEdge(from: BaseBlock, to: BaseBlock) : Edge(from, to) {
    abstract val pair: PairedEdge?
    abstract val value: Expression
}

class ConditionEdge(from: BaseBlock, to: BaseBlock, override val value: Expression) : PairedEdge(from, to) {
    override val pair: PairedEdge?
        get() {
            if (from == null)
                println("123")
            val h = from!!.outEdge.find { it is ElseConditionEdge && it.fromEdge == this }
            if (h == null)
                println("123")
            return h as ElseConditionEdge
        }

    override fun toString(): String {
        return "ConditionEdge(from=${from?.ID} to ${to?.ID}, value=$value)"
    }

    override fun _from() = "${from?.ID} value=$value"
    override fun _to() = "${to?.ID} value=$value"
}

class ElseConditionEdge(var fromEdge: ConditionEdge, to: BaseBlock) : PairedEdge(fromEdge.from!!, to) {
    private val _value: Expression

    init {
        _value = ConditionNot(fromEdge.value)
    }

    override val value: Expression
        get() = _value
    override val pair: PairedEdge?
        get() = fromEdge

    override fun toString(): String {
        return "ElseEDGE from=${from?.ID} to ${to?.ID}, value=NOT ${fromEdge.value}"
    }

    override fun _from() = "${from?.ID} value=NOT ${fromEdge.value}"
    override fun _to() = "${to?.ID} value=NOT ${fromEdge.value}"
}
