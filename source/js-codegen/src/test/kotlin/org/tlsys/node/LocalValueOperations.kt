package org.tlsys.node

import org.tlsys.Expression
import org.tlsys.Operation

class GetVar(val index: Int) : Expression() {
    override fun toString(): String {
        return "V$index"
    }
}

class SetVar(val index: Int, var value: Expression) : Operation() {
    override fun toString(): String {
        return "V$index=$value"
    }

    init {
        value.use(this)
    }

    override fun freeUsingValues() {
        value.unuse(this)
    }

    override fun replaceUsingValue(old: Expression, new: Expression) {
        if (old === value)
            value = new
    }
}

