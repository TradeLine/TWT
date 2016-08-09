package org.tlsys

import org.tlsys.edge.Edge
import org.tlsys.edge.ValueSteck
import org.tlsys.node.LabelNode
import org.tlsys.node.Node
import java.util.*

abstract open class EdgeContener : Iterable<Edge> {
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
    fun find(f: (Edge) -> Boolean): Edge? = list.find(f)
    val size: Int get()=list.size
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

class InEdgeContener : EdgeContener() {
    override fun clear() {
        for (g in toList().toTypedArray())
            g.from = null
    }

    override fun plusAssign(e: Edge) {
        val v = e in list
        super.plusAssign(e)
        if (v)
            e.to = null
    }

    override fun minusAssign(e: Edge) {
        val v = e in list
        super.minusAssign(e)
        if (!v)
            e.to = null
    }
}

class OutEdgeContener : EdgeContener() {

    override fun clear() {
        for (g in toList().toTypedArray())
            g.to = null
    }

    override fun plusAssign(e: Edge) {
        val v = e in list
        super.plusAssign(e)
        if (v)
            e.from = null
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

    val inEdge = InEdgeContener()
    val outEdge = OutEdgeContener()

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

    var operationChanges: Boolean = false

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
}