package org.tlsys

import org.tlsys.edge.Edge
import org.tlsys.edge.ValueSteck
import org.tlsys.node.LabelNode
import org.tlsys.node.Node
import java.util.*

abstract open class EdgeContener(val parent: BaseBlock) : Iterable<Edge> {
    override fun iterator(): Iterator<Edge> = list.iterator()

    protected val list = HashSet<Edge>()

    open operator fun plusAssign(e: Edge) {
        if (e in list)
            return
        list += e
    }

    open operator fun minusAssign(e: Edge) {
        if (e !in list)
            return
        list -= e
    }

    operator fun contains(e: Edge) = e in list
    //fun find(f: (Edge) -> Boolean): Edge? = list.find(f)
    val size: Int get() = list.size

    fun isEmpty() = list.isEmpty()
    fun isNotEmpty() = list.isNotEmpty()

    fun toList() = list.toList()

    fun getOne(): Edge {
        if (isEmpty())
            throw RuntimeException("No Value")
        if (size != 1)
            throw RuntimeException("No Value")
        return iterator().next()
    }

    abstract fun clear()
}

class InEdgeContener(parent: BaseBlock) : EdgeContener(parent) {
    override fun clear() {
        for (g in toList().toTypedArray())
            g.from = null
    }

    override fun plusAssign(e: Edge) {
        val v = e in list
        super.plusAssign(e)
        if (v)
            e.to = parent
    }

    override fun minusAssign(e: Edge) {
        val v = e in list
        super.minusAssign(e)
        if (!v)
            e.to = null
    }
}

class OutEdgeContener(parent: BaseBlock) : EdgeContener(parent) {

    override fun clear() {
        for (g in toList().toTypedArray())
            g.to = null
    }

    override fun plusAssign(e: Edge) {
        val v = e in list
        super.plusAssign(e)
        if (v)
            e.from = parent
    }

    override fun minusAssign(e: Edge) {
        val v = e in list
        super.minusAssign(e)
        if (!v)
            e.from = null
    }
}

class BaseBlock(val program: Program, val rigen: String = "") {
    private companion object {
        var ITERATOR: Int = 0
    }

    val ID = ITERATOR++
    private val operations = ArrayList<Node>()

    val inEdge = InEdgeContener(this)
    val outEdge = OutEdgeContener(this)

    fun isEmpty() = operations.isEmpty()
    fun isNotEmpty() = operations.isNotEmpty()

    fun toList() = operations

    val size: Int get() = operations.size

    operator fun get(index: Int) = operations[index]

    val steck = ValueSteck(this)
    operator fun plusAssign(node: Node) {
        if (node in operations)
            throw IllegalArgumentException("Node already in this block")
        if (node.block != null)
            throw IllegalArgumentException("Node already have parent!")
        node.block = this
        operations += node
        operationChanges = true
    }

    private var operationChanges: Boolean = false

    operator fun minusAssign(node: Node) {
        if (node !in operations)
            throw IllegalArgumentException("Node already in this block")
        if (node.block == null)
            throw IllegalArgumentException("Node already have parent!")
        node.block = null
        operations.remove(node)
        operationChanges = true
    }

    fun giveEdgeTo(block: BaseBlock) {
        for (e in inEdge) {
            e.to = block
        }
        for (e in outEdge) {
            e.from = block
        }
    }

    fun operationIterator(): MutableListIterator<Node> = operations.listIterator()

    private var _operationCount: Int = 0

    private fun updateCash() {
        var h = 0
        for (g in operations) {
            if (g is LabelNode)
                continue
            h++
        }

        _operationCount = h
        operationChanges = false
    }

    val operationCount: Int get() {
        if (operationChanges)
            updateCash()
        return _operationCount
    }


    fun getPathLengthTo_UP(block: BaseBlock, from: Path? = null): OneBlock? {
        if (block === this)
            return OneBlock(from, this)

        if (inEdge.isEmpty())
            return null

        val o = OneBlock(from, this)
        if (inEdge.size == 1) {
            o.next = inEdge.iterator().next().from!!.getPathLengthTo_UP(block, o)
            if (o.next === null)
                return null
            return o
        }

        val branch = ArrayList<OneBlock>(inEdge.size)

        for (g in inEdge) {
            branch += g.from!!.getPathLengthTo_UP(block, o) ?: continue
        }

        if (branch.isEmpty())
            return null
        if (branch.size == 1) {
            o.next = branch[0]
            return o
        }
        o.next = PathBranch(o, branch.toTypedArray())
        return o
    }

    fun addFirst(op: Operation) {
        operations.add(0, op)
    }
}

open abstract class Path(val parent: Path?) {

    abstract fun blockEqual(path: Path): Boolean

    companion object {
        fun findDominator_start_end(list: Array<Path>): Path? {
            var doms = Array(list.size, { list[it] })

            var dom: Path? = null

            CYCL@while (true) {
                for (c1 in doms) {
                    for (c2 in doms) {
                        if (!c1.blockEqual(c2))
                            break@CYCL
                    }
                }

                val ends = doms.filter { !it.hasNext() }
                if (ends.size == doms.size) {
                    //дошли до самого конца, и все ветви сошлись. значит пути одинаковы
                    return doms[0]
                }
                if (ends.isNotEmpty()) {
                    break@CYCL
                }
                dom = doms[0]
                doms = Array(doms.size, { doms[it].next() })
            }
            return dom
        }

        fun findDominator_end_start(list: Array<Path>): Path? {
            var doms = Array(list.size, { list[it] })

            var dom: Path? = null

            CYCL@while (true) {
                for (c1 in doms) {
                    for (c2 in doms) {
                        if (!c1.blockEqual(c2))
                            break@CYCL
                    }
                }

                val ends = doms.filter { !it.hasPrevious() }
                if (ends.size == doms.size) {
                    //дошли до самого конца, и все ветви сошлись. значит пути одинаковы
                    return doms[0]
                }
                if (ends.isNotEmpty()) {
                    break@CYCL
                }
                dom = doms[0]
                doms = Array(doms.size, { doms[it].previous() })
            }
            return dom
        }
    }

    fun hasPrevious(): Boolean = parent !== null

    fun nextIndex(): Int = if (hasNext()) index + 1 else index

    fun previous(): Path = parent!!

    fun previousIndex(): Int = if (hasPrevious()) index - 1 else index

    val level: Int
    val index: Int

    init {
        if (parent === null) {
            level = 0
            index = 0
        } else {
            index = parent.index + 1
            if (parent is PathBranch)
                level = parent.level
            else
                level = parent.level + 1
        }
    }

    fun hasNext(): Boolean = next !== null

    fun next(): Path = next!!

    var next: Path? = null

    open fun last(): Path {
        if (hasNext()) return next().last() else return this
    }

    open fun first(): Path {
        if (hasPrevious()) return previous().last() else return this
    }
}

class OneBlock(parent: Path?, val block: BaseBlock) : Path(parent) {
    override fun blockEqual(path: Path): Boolean {
        if (path === this)
            return true
        if (path is OneBlock) {
            return path.block === block
        }
        return true
    }

}

class PathBranch(parent: Path?, val list: Array<OneBlock>) : Path(parent) {
    override fun blockEqual(path: Path): Boolean {
        if (path === this)
            return true
        if (path is PathBranch) {
            if (path.list.size != list.size)
                return false
            for (b in path.list)
                if (b !in list)
                    return false
        }
        return false
    }

    override fun last(): Path {
        var p: Path? = null
        var l: Int = Int.MAX_VALUE
        for (g in list) {
            val h = g.last()
            if (h.level < l) {
                l = h.level
                p = h
            }
        }
        return p!!
    }
}