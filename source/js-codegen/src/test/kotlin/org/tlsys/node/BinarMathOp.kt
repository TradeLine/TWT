package org.tlsys.node

import org.objectweb.asm.Opcodes
import org.tlsys.BaseBlock
import org.tlsys.Expression

class BinarMathOp(var left: Expression, var right: Expression, var type: Types) : Expression() {
    override fun toString(): String {
        return "(${left}${type.text}${right})"
    }

    enum class Types(val text: String) {
        ADD("+"),
        SUB("-"),
        MUL("*"),
        DIV("/");

        companion object {
            fun fromCode(opcode: Int): Types {
                when (opcode) {
                    Opcodes.IADD, Opcodes.FADD, Opcodes.DADD, Opcodes.LADD -> return ADD
                    Opcodes.ISUB, Opcodes.LSUB, Opcodes.FSUB, Opcodes.DSUB -> return SUB
                    Opcodes.IMUL, Opcodes.LMUL, Opcodes.FMUL, Opcodes.DMUL -> return MUL
                    Opcodes.IDIV, Opcodes.LDIV, Opcodes.FDIV, Opcodes.DDIV -> return DIV
                    else -> TODO("Unknown opcode: $opcode")
                }
            }

            fun isBinarOp(opcode: Int): Boolean {
                try {
                    fromCode(opcode)
                    return true
                } catch (e: Throwable) {
                    return false
                }
            }
        }
    }
}
