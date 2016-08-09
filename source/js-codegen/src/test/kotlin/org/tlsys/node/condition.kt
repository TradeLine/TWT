package org.tlsys.node

import org.objectweb.asm.Opcodes
import org.tlsys.Expression

class ConditionExp(var left: Expression, var right: Expression, var type: ConditionType) : Expression() {
    override fun toString(): String {
        return "($left ${type.text} $right)"
    }
}

enum class ConditionType(var text: String) {
    IFEQ("=="),
    IFNE("!="),

    IFLT("<"),
    IFLE("<="),

    IFGT(">"),
    IFGE(">=");

    operator fun not(): ConditionType {
        return when (this) {
            IFEQ -> IFNE
            IFNE -> IFEQ

            IFLT -> IFGE
            IFGE -> IFLT

            IFLE -> IFGT
            IFGT -> IFLE
        }
    }

    companion object {
        fun fromOpcode(opcode: Int):ConditionType {
            return when (opcode) {
                Opcodes.IFLE, Opcodes.IF_ICMPLE -> IFLE
                Opcodes.IFGE, Opcodes.IF_ICMPGE -> IFGE
                Opcodes.IFGT -> IFGT
                Opcodes.IFLE -> IFLE
                Opcodes.IFLT -> IFLT
                else -> TODO()
            }
        }

        fun isCondition(opcode:Int):Boolean{
            try {
                fromOpcode(opcode)
                return true
            } catch (e:Throwable) {
                return false
            }
        }
    }
/*
    val v2: BinarLogicOp.Type
    get() {
        when(this){
            IFEQ->return BinarLogicOp.Type.EQ
            IFNE->return BinarLogicOp.Type.NOT_EQ
            IFLT->return BinarLogicOp.Type.LT
            IFLE->return BinarLogicOp.Type.LE

            IFGT->return BinarLogicOp.Type.GT
            IFGE->return BinarLogicOp.Type.GE
        }
    }*/
}

class ConditionNot(var value: Expression) : Expression() {
    override fun toString(): String {
        return "!$value"
    }
}