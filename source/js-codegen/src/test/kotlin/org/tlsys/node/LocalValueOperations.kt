package org.tlsys.node

import org.tlsys.BaseBlock
import org.tlsys.Expression

open class VarOp():Node()

class GetVar(val index:Int):VarOp() {
    override fun toString(): String{
        return "PUSH_Var(index=$index)"
    }
}

class SetVar(val index:Int,var value: Expression):VarOp() {
    override fun toString(): String{
        return "V$index=$value"
    }
}

