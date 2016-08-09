package org.tlsys.edge

import org.tlsys.BaseBlock
import org.tlsys.Expression

open abstract class Edge(private var _from: BaseBlock?, private var _to: BaseBlock?) {
    abstract fun _from(): String
    abstract fun _to(): String

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
            if (_from === it)
                return
            if (_to != null)
                _to!!.inEdge -= this

            _to = it

            if (it != null && this !in it.inEdge)
                it.inEdge += this

        }
}

class SimpleEdge(from: BaseBlock, to: BaseBlock) : Edge(from, to) {
    override fun toString(): String {
        return "SimpleEdge(from=${from?.ID} to ${to?.ID})"
    }

    override fun _from() = "${from?.ID}"
    override fun _to() = "${to?.ID}"
}

class ConditionEdge(from: BaseBlock, to: BaseBlock, val value: Expression) : Edge(from, to) {
    override fun toString(): String {
        return "ConditionEdge(from=${from?.ID} to ${to?.ID}, value=$value)"
    }

    override fun _from() = "${from?.ID} value=$value"
    override fun _to() = "${to?.ID} value=$value"
}

class ElseConditionEdge(var fromEdge: ConditionEdge, to: BaseBlock) : Edge(fromEdge.from, to) {
    override fun toString(): String {
        return "ElseEDGE from=${from?.ID} to ${to?.ID}, value=NOT ${fromEdge.value}"
    }

    override fun _from() = "${from?.ID} value=NOT ${fromEdge.value}"
    override fun _to() = "${to?.ID} value=NOT ${fromEdge.value}"
}
