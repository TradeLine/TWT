package org.tlsys.node

import org.tlsys.ClassRef
import org.tlsys.Expression
import org.tlsys.Operation

class New(val type: ClassRef) : Expression() {
    override fun toString(): String = "NEW ${type.sinature}"
}

class Throw(var value: Expression) : Operation() {
    override fun toString(): String = "Throw $value"

    init {
        value.use(this)
    }

    override fun freeUsingValues() {
        value.unuse(this)
    }

    override fun replaceUsingValue(old: Expression, new: Expression) {
        if (value === old)
            value = new
    }
}