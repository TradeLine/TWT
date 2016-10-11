package ggg

import org.tlsys.BaseBlock
import org.tlsys.TypeID
import java.util.*

open abstract class Edge(from: Block, to: Block) {
    abstract fun _from(): String
    abstract fun _to(): String

    private var _from: Block? = null
    private var _to: Block? = null

    val next: Boolean
        get() = from!!.level < to!!.level

    var from: Block?
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
    var to: Block?
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

class CatchEdge(from: Block, to: Block, val type: TypeID) : Edge(from, to){
    override fun _from() = "${from?.ID}"
    override fun _to() = "${to?.ID}"
    override fun toString(): String = "CATCH (${from?.ID}=>${to?.ID}): ${type.sinature}"
}

class SimpleEdge(from: Block, to: Block, val resion: String) : Edge(from, to) {
    override fun toString(): String = "simple(${from?.ID}=>${to?.ID}): $resion"

    override fun _from() = "${from?.ID}"
    override fun _to() = "${to?.ID}"
}

open class PairedEdge(from: Block, to: Block) : Edge(from, to) {
    override fun _from(): String {
        return ""
    }

    override fun _to(): String {
        return ""
    }

}

open class ConditionEdge(from: Block, to: Block, val value: Expression, val regin: String) : PairedEdge(from, to) {
    val pair: PairedEdge?
        get() {
            val h = from!!.outEdge.find { it is ElseConditionEdge && it.origenal == this }
            return h as ElseConditionEdge
        }

    override fun toString(): String {
        return "if(${from?.ID}=>${to?.ID} {$value}):$regin"
    }

    override fun _from() = "${from?.ID} value=$value"
    override fun _to() = "${to?.ID} value=$value"
}

class ElseConditionEdge(var origenal: ConditionEdge, to: Block, val regin: String) : PairedEdge(from = origenal.from!!, to = to) {
    override fun toString(): String {
        return "else(${from?.ID}=>${to?.ID} {${origenal.value}}):$regin"
    }
}


abstract open class EdgeContener(val parent: Block) : Iterable<Edge> {
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

class InEdgeContener(parent: Block) : EdgeContener(parent) {
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

class OutEdgeContener(parent: Block) : EdgeContener(parent) {

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

    fun copyFrom(con: OutEdgeContener) {
        for (g in con) {
            if (g is SimpleEdge) {
                SimpleEdge(from = parent, to = g.to!!, resion = "COPY: ${g.resion}")
            }
        }
    }

    fun moveTo(con: OutEdgeContener) {
        for (g in toList()) {
            g.from = con.parent
        }
    }

    override fun minusAssign(e: Edge) {
        val v = e in list
        super.minusAssign(e)
        if (!v)
            e.from = null
    }
}