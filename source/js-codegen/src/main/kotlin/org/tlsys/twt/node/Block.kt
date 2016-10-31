package org.tlsys.twt.node

import org.tlsys.twt.JMethod
import org.tlsys.twt.statement.Statement

private var iterator: Int = 0

class Block() {
    private class EmptyStatement() : Statement()

    var description: String = ""

    val nextIterator: Statement.NextIterator
        get() {
            val e = Statement()
            e.next = first
            return e.nextIterator
        }

    val previousIterator: Statement.PreviousIterator
        get() {
            val e = Statement()
            e.previous = last
            return e.previousIterator
        }

    val ID: Int = ++iterator
    var first: Statement? = null
    var last: Statement? = null

    var dominator: Block? = null

    val inEdge = InEdgeContener(this)
    val outEdge = OutEdgeContener(this)

    fun isIDom(b: Block): Boolean {
        if (this === b)
            return true

        var o: Block = this
        while (true) {
            if (o.inEdge.isEmpty() && o !== b && o.dominator === o)
                return false
            o = o.dominator!!
            if (o===b)
                return true
        }
    }

    fun testValid() {
        for (g in inEdge)
            if (g.to !== this)
                TODO()
        for (g in outEdge)
            if (g.from !== this)
                TODO()
        if ((first == null && last != null) || (first != null && last == null))
            TODO()

        var o = first
        var h = o
        while (o != null) {
            if (o.block !== this)
                TODO()
            if (o.next !== null)
                h = o.next
            o = o.next
        }

        if (h !== last)
            TODO()
    }

    infix operator fun plusAssign(statement: Statement) {
        testValid()
        if (first === null && last === null) {
            first = statement
            last = statement
        } else {
            if (first === null || last === null)
                TODO()
            last!!.next = statement
            statement.previous = last!!
            last = statement
        }
        statement.block = this

        testValid()
    }

    fun isEmpty(): Boolean = first === null

    fun <T : Statement> find(f: (Statement) -> Boolean): T? {
        var o = first
        while (o !== null) {
            if (f(o))
                return o as T?
            o = o.next
        }

        return null
    }
}
