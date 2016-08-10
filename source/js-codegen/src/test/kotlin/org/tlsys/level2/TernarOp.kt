package org.tlsys.level2

import org.tlsys.Expression

class TernarOp(var value: Expression, var trueExp: Expression, var falseExp: Expression) : Expression() {
    override fun toString(): String = "($value ? $trueExp : $falseExp)"

    init {
        value.use(this)
        trueExp.use(this)
        falseExp.use(this)
    }

    override fun freeUsingValues() {
        value.unuse(this)
        trueExp.unuse(this)
        falseExp.unuse(this)
    }

    override fun replaceUsingValue(old: Expression, new: Expression) {
        if (value === old)
            value = new
        if (trueExp === old)
            trueExp = new
        if (falseExp === old)
            falseExp = old
    }
}