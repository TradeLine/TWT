package org.tlsys.twt

import org.tlsys.twt.TypeID
import org.tlsys.twt.node.Block
import org.tlsys.twt.statement.Expression
import org.tlsys.twt.statement.UnknownVarValue
import java.util.*

private var VAR_COUNTER = 0

open class Var(val type: TypeID) {
    val ID = VAR_COUNTER++
    private var _first: VarState? = null

    private val first: VarState
        get() = _first!!

    override fun toString(): String = "TEMP_$ID"

    fun first(value: Expression): VarState {
        if (_first !== null)
            TODO()
        _first = VarState(this, null, value)
        return _first!!
    }

    fun unkownState() = VarState(this, null, UnknownVarValue(this))

    fun unkownState(value: Expression) = VarState(this, null, value)

    class VarState(val parent: Var, val from: VarState?, val value: Expression) {
        fun set(exp: Expression) = VarState(parent, this, exp)
    }
}

class NamedVar(val index: Int, type: TypeID) : Var(type) {
    var name: String = "V$index"
    override fun toString(): String = "$name(${type.sinature})"
}

class JMethod() {
    val entryBlock = Block(this, { 0 })
    val namedVar = HashMap<Int, NamedVar>()
    val tempVar = ArrayList<Var>()

    fun createArg(index: Int, type: TypeID): NamedVar {
        val v = NamedVar(index, type)
        namedVar[index] = v
        return v
    }

    fun createVar(index: Int, type: TypeID) = createArg(index, type)

    fun getVar(index: Int): NamedVar? = namedVar[index]

    fun createTemp(type: TypeID): Var {
        val v = Var(type)
        tempVar += v
        return v
    }
}

