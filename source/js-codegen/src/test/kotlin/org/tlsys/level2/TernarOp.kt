package org.tlsys.level2

import org.tlsys.Expression

class TernarOp(val value: Expression, val trueExp: Expression, val falseExp: Expression) : Expression() {
    override fun toString(): String = "($value ? $trueExp : $falseExp)"
}