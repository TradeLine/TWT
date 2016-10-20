package org.tlsys.node

import org.objectweb.asm.Opcodes
import org.tlsys.Expression
import org.tlsys.twt.statement.ConditionType

class ConditionExp(var left: Expression, var right: Expression, var type: ConditionType) : Expression() {
    override fun toString(): String {
        return "($left ${type.text} $right)"
    }

    init {
        left.use(this)
        right.use(this)
    }

    override fun freeUsingValues() {
        left.unuse(this)
        right.unuse(this)
    }

    override val constValue: Boolean
        get() = left.constValue && right.constValue

    override fun replaceUsingValue(old: Expression, new: Expression) {
        if (old === left)
            left == new
        if (old === right)
            right = new
    }
}

class ConditionNot(var value: Expression) : Expression() {
    override fun toString(): String {
        return "!($value)"
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

fun Expression.not(): Expression {
    if (this is ConditionNot)
        return this.value
    return ConditionNot(this)
}