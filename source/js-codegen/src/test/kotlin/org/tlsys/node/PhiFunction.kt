package org.tlsys.node

import org.tlsys.Expression
import org.tlsys.Var
import java.util.*

class ExpValList<T, P>(val parent: P, val onAdd: T.(P) -> Unit, val onRemove: T.(P) -> Unit) : MutableIterable<T> {

    class ExpIterator<T, P>(val parent: ExpValList<T, P>) : MutableIterator<T> {
        private val it = parent.list.iterator()
        override fun hasNext(): Boolean = it.hasNext()

        private var last: T? = null
        override fun next(): T {
            last = it.next()
            return last as T
        }

        override fun remove() {
            parent.onRemove.invoke(last!!, parent.parent)
            it.remove()
        }

    }

    override fun iterator(): MutableIterator<T> = ExpIterator<T, P>(this)

    private val list = ArrayList<T>()
    infix operator fun plusAssign(list: Collection<T>) {
        for (e in list)
            e.onAdd(parent)
        this.list.addAll(list)
    }

    infix operator fun plusAssign(exp: T) {
        exp.onAdd(parent)
        this.list += exp
    }

    override fun toString(): String = list.toString()

    fun replace(old: T, new: T) {
        val p = list.indexOf(old)
        if (p >= 0) {
            list[p]!!.onRemove(parent)
            list[p] = new
            new.onAdd(parent)
        }
    }

}

class PhiFunction(list: List<Var.VarVariantValue>) : Expression() {
    val list = ExpValList<Expression, PhiFunction>(this, { use(it) }, { unuse(it) })

    init {
        for (e in list)
            this.list += e.get()
    }

    override fun toString(): String {

        return "OneOf{${list.joinToString("  ,  ")}}"
    }

    override fun replaceUsingValue(old: Expression, new: Expression) {
        super.replaceUsingValue(old, new)
        list.replace(old, new)
    }
}