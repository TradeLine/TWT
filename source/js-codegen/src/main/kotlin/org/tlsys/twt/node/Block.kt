package org.tlsys.twt.node

import org.tlsys.twt.JMethod
import org.tlsys.twt.statement.Statement

private var iterator: Int = 0

class Block(val method: JMethod, private val levelProvider: Block.() -> Int) {
    private class EmptyStatement() : Statement()
    var description:String = ""

    /*
    abstract class StatementIterator(val block: Block, startStatement: Statement?) : MutableIterator<Statement> {
        protected var cursor: Statement? = startStatement
        val current: Statement?
            get() = cursor

        abstract fun changeCurrentAfterRemove(): Statement?

        override fun remove() {
            val c = cursor!!
            c.block!!.testValid()
            if (block.first !== block.last) {
                if (block.first === null) {//если в блоке нет элементов
                    TODO()
                } else {//если в блоке один элемент
                    cursor = null
                    block.first = null
                    block.last = null
                }
            } else {//много элементов в блоке
                if (block.first === c) {//текущий - первый элемент
                    val n = changeCurrentAfterRemove()
                    c.next!!.previous = null
                    block.first = c.next
                    cursor = n
                } else if (block.last === c) {//текущий - последний элемент
                    val n = changeCurrentAfterRemove()
                    c.previous!!.next = null
                    block.last = c.previous
                    cursor = n
                } else {//текущий -  где-то в центре
                    val n = changeCurrentAfterRemove()
                    c.previous!!.next = c.next
                    c.next!!.previous = c.previous
                    cursor = n
                }
            }
        }
    }

    class NextIterator(block: Block, startStatement: Statement?) : StatementIterator(block, startStatement) {
        private var first = true

        override fun changeCurrentAfterRemove(): Statement? = cursor!!.previous

        override fun hasNext(): Boolean {
            if (first) {
                if (cursor !== null)
                    return true
                return block.first !== null
            } else {
                return (cursor === null) && (cursor!!.next !== null)
            }
        }

        override fun next(): Statement {
            if (first) {
                if (cursor === null)
                    cursor = block.first
                first = false
                return cursor!!
            } else {
                cursor = cursor!!.next
                return cursor!!
            }
        }
    }

    class PreviousIterator(block: Block, startStatement: Statement?) : StatementIterator(block, startStatement) {
        private var first = true

        override fun changeCurrentAfterRemove(): Statement? = cursor!!.next

        override fun hasNext(): Boolean {
            if (first) {
                if (cursor != null)
                    return true
                return block.last !== null
            } else {
                val o = (cursor !== null) && (cursor!!.previous !== null)
                return o
            }
        }

        override fun next(): Statement {
            if (first) {
                if (cursor === null)
                    cursor = block.last
                first = false
                return cursor!!
            } else {
                cursor = cursor!!.previous
                return cursor!!
            }
        }
    }
*/
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

    val level: Int
        get() = levelProvider()

    val ID: Int = ++iterator
    var first: Statement? = null
    var last: Statement? = null

    var dominator: Block?=null

    val inEdge = InEdgeContener(this)
    val outEdge = OutEdgeContener(this)

    fun testValid() {
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

    companion object {
        val LEVEL_PARENT_MIN: Block.() -> Int = {
            if (inEdge.isEmpty())
                0
            else {
                if (inEdge.size == 1) {
                    inEdge.iterator().next().from!!.level
                } else {
                    var min = Int.MAX_VALUE
                    for (g in inEdge) {
                        val h = g.from!!.level
                        min = if (h < min) h else min
                    }
                    min
                }
            }
        }
    }
}
