package org.tlsys.twt.statement

import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.tlsys.ClassRef
import org.tlsys.Primitive
import org.tlsys.TypeID
import org.tlsys.Var
import org.tlsys.node.Block
import org.tlsys.node.SimpleEdge
import java.util.*

open class Statement() {
    abstract class StatementIterator(startStatement: Statement) : MutableIterator<Statement> {
        protected var cursor: Statement? = startStatement
        val current: Statement?
            get() = cursor

        abstract fun changeCurrentAfterRemove(): Statement?

        override fun remove() {
            val newCursor = changeCurrentAfterRemove()
            cursor!!.remove()
            cursor = newCursor
            /*
            if (block.first !== block.last) {
                if (block.first === null) {//если в блоке нет элементов
                    TODO()
                } else {//если в блоке один элемент
                    cursor = null
                    block.first = null
                    block.last = null
                }
            } else {//много элементов в блоке
                if (block.first === c) {//текущий - первый элемент
                    val n = changeCurrentAfterRemove()
                    c.next!!.previous = null
                    block.first = c.next
                    cursor = n
                } else if (block.last === c) {//текущий - последний элемент
                    val n = changeCurrentAfterRemove()
                    c.previous!!.next = null
                    block.last = c.previous
                    cursor = n
                } else {//текущий -  где-то в центре
                    val n = changeCurrentAfterRemove()
                    c.previous!!.next = c.next
                    c.next!!.previous = c.previous
                    cursor = n
                }
            }
            */
        }
    }

    class NextIterator(startStatement: Statement) : StatementIterator(startStatement) {

        override fun changeCurrentAfterRemove(): Statement? {
            cursor!!.previous

            var n = cursor!!.previous
            if (n === null) {
                n = Statement()
                n.next = cursor!!.next
            }
            return n
        }

        override fun hasNext(): Boolean {
            return (cursor !== null) && (cursor!!.next !== null)
        }

        override fun next(): Statement {
            cursor = cursor!!.next
            return cursor!!
        }
    }

    class PreviousIterator(startStatement: Statement) : StatementIterator(startStatement) {

        override fun changeCurrentAfterRemove(): Statement? {
            var n = cursor!!.next
            if (n === null) {
                n = Statement()
                n.previous = cursor!!.previous
            }
            return n
        }

        override fun hasNext(): Boolean {
            val o = (cursor !== null) && (cursor!!.previous !== null)
            return o
        }

        override fun next(): Statement {
            cursor = cursor!!.previous
            return cursor!!
        }
    }

    var previous: Statement? = null
    var next: Statement? = null
    var block: Block? = null

    val nextIterator: NextIterator
        get() = NextIterator(this)

    val previousIterator: PreviousIterator
        get() = PreviousIterator(this)

    fun remove() {
        val p = previous
        val n = next
        if (block != null && block!!.first === this) {
            if (n !== null)
                block!!.first = n
            else
                block!!.first = null
        }

        if (block != null && block!!.last === this) {
            if (p !== null)
                block!!.last = p
            else
                block!!.last = null
        }

        block = null
        if (n !== null)
            n.previous = p
        if (p !== null)
            p.next = n
        previous = null
        next = null
    }

    open val stackOut: Expression? = null
    open val stackNeed: org.tlsys.TypeID? = null
    open fun push(value: Expression): Boolean = false

    fun moveToLast(block: Block) {
        remove()
        block += this
        block.testValid()
    }
}

class GetVar(val state: Var.VarState) : Expression() {
    override val type: org.tlsys.TypeID
        get() = state.parent.type

    override fun toString(): String = "${state.parent}"
}


class SkipOne : Statement() {

    var value: Expression? = null

    override val stackNeed: org.tlsys.TypeID?
        get() = if (value === null) org.tlsys.Primitive.Companion.get('V') else null

    override fun push(value: Expression): Boolean {
        if (this.value === null) {
            this.value = value
            return true
        }
        return false
    }

    override fun toString(): String = "SKIP${if (value === null) "" else " VAL:$value"}"
}

class InitValue(private var initType: org.tlsys.TypeID) : Expression() {
    override val type: org.tlsys.TypeID
        get() = initType

    override fun toString(): String = "INIT (${initType.sinature})"
}

class UnknownVarValue(val parent: Var) : Expression() {
    override val type: org.tlsys.TypeID
        get() = parent.type

    override fun toString(): String = "IK_$parent"
}

abstract open class Expression() {
    abstract val type: org.tlsys.TypeID
    open val stackTypeNeed: org.tlsys.TypeID? = null
    open fun push(value: Expression): Boolean = false
}

class Return() : Statement() {
    override fun toString(): String = "RETURN"
}

class LabelSt(val label: Label) : Statement()

class StringValue(val value: String) : Expression() {
    override val type: org.tlsys.TypeID
        get() = org.tlsys.ClassRef.Companion.get("STRING")

    override fun toString(): String = "\"$value\""
}

class IntValue(val value: Int) : Expression() {
    override val type: org.tlsys.TypeID
        get() = org.tlsys.Primitive.Companion.get('I')

    override fun toString(): String = "$value"
}

class ExeInvoke(val invoke: Invoke) : Statement() {

    override val stackNeed: org.tlsys.TypeID?
        get() = invoke.stackTypeNeed

    override fun push(value: Expression): Boolean {
        return invoke.push(value)
    }

    override fun toString(): String = "EXE $invoke"
}

class PushVar(val state: Var.VarState) : Statement() {
    override val stackOut: Expression?
        get() = GetVar(state)

    override fun toString(): String = "PUSH VAR ${state.parent}[${state.parent.type.sinature}]"
}

class SetVar(var state: Var.VarState) : Statement() {
    override fun toString(): String = "SET VAR ${state.parent}  =  {${state.value}}"

    override val stackNeed: org.tlsys.TypeID?
        get() {
            if (state.value is PopVar)
                return state.parent.type
            return state.value.stackTypeNeed
        }

    override fun push(value: Expression): Boolean {
        if (state.value is PopVar) {
            state = state.parent.unkownState(value)
            return true
        }
        return state.value.push(value)
    }
}

fun Statement.split(): Block {
    val b = block
    val newBlock = Block(block!!.method, Block.LEVEL_PARENT_MIN)

    for (e in block!!.outEdge.toList()) {
        e.from = newBlock
    }

    SimpleEdge(block!!, newBlock, "SPLIT")

    if (previous !== null) {
        block!!.last = previous!!
        previous!!.next = null
    } else {
        block!!.last = null
        block!!.first = null
    }
    previous = null
    var cur: Statement? = this
    newBlock.first = this
    while (cur != null) {
        cur.block = newBlock
        newBlock.last = cur
        cur = cur.next
    }


    if (b !== null)
        b.testValid()
    newBlock.testValid()
    return newBlock
}

fun Block.findValueOfVar(v: Var): Var.VarState? {
    if (last !== null)
        return last!!.findValueOfVar(v)
    if (inEdge.size == 1) {
        val e = inEdge.iterator().next()
        if (e is SimpleEdge)
            return e.from!!.findValueOfVar(v)
    }
    return null
}

fun Statement.findValueOfVar(v: Var): Var.VarState? {
    if (this is SetVar && state.parent === v)
        return state

    if (previous === null) {
        if (block!!.inEdge.size == 1) {
            val e = block!!.inEdge.iterator().next()
            if (e is SimpleEdge)
                return e.from!!.findValueOfVar(v)
        }
        val findedValues = ArrayList<Var.VarState>()
        for (g in block!!.inEdge) {
            val d = g.from!!.findValueOfVar(v)
            if (d !== null && d !in findedValues)
                findedValues += d
        }
        if (findedValues.isEmpty())
            return null
        if (findedValues.size == 1)
            return findedValues[0]
        return null
    } else {
        return previous!!.findValueOfVar(v)
    }
}


class PopVar(override val type: org.tlsys.TypeID) : Expression() {
    override fun toString(): String = "POP"
}

open class Invoke(val methodName: String, val arguments: Array<Expression>, override var type: org.tlsys.TypeID) : Expression() {

    override val stackTypeNeed: org.tlsys.TypeID?
        get() {
            for (i in arguments.size - 1 downTo 0) {
                if (arguments[i] is PopVar)
                    return arguments[i].type

                val g = arguments[i].stackTypeNeed
                if (g !== null) {
                    return g
                }
            }
            return null
        }

    override fun push(value: Expression): Boolean {
        for (i in arguments.size - 1 downTo 0) {
            if (arguments[i] is PopVar) {
                arguments[i] = value
                return true
            }


            val g = arguments[i].stackTypeNeed
            if (g !== null) {
                return arguments[i].push(value)
            }
        }

        return false
    }
}

class InvokeStatic(val parentClass: org.tlsys.TypeID, methodName: String, arguments: Array<Expression>, type: org.tlsys.TypeID) : Invoke(methodName, arguments, type) {
    override fun toString(): String = "INV_STATIC ${parentClass.sinature}.$methodName(${arguments.joinToString(",")}):${type.sinature}"
}

class InvokeSpecial(var self: Expression, methodName: String, arguments: Array<Expression>, type: org.tlsys.TypeID) : Invoke(methodName, arguments, type) {
    override val stackTypeNeed: org.tlsys.TypeID?
        get() {
            val g = super.stackTypeNeed
            if (g !== null)
                return g
            if (self is PopVar)
                return self.type
            return null
        }

    override fun push(value: Expression): Boolean {
        if (super.push(value))
            return true
        if (self is PopVar) {
            self = value
            return true
        }
        return false
    }

    override fun toString(): String = "SPECIAL $self.$methodName(${arguments.joinToString(",")}):${type.sinature}"
}

enum class ConditionType(var text: String) {
    IFEQ("=="),
    IFNE("!="),

    IFLT("<"),
    IFLE("<="),

    IFGT(">"),
    IFGE(">="),
    AND("&&"),
    OR("||");

    operator fun not(): ConditionType {
        return when (this) {
            IFEQ -> IFNE
            IFNE -> IFEQ

            IFLT -> IFGE
            IFGE -> IFLT

            IFLE -> IFGT
            IFGT -> IFLE
            else -> TODO()
        }
    }

    companion object {
        fun fromOpcode(opcode: Int): ConditionType {
            return when (opcode) {
                Opcodes.IFLE, Opcodes.IF_ICMPLE -> IFLE
                Opcodes.IFGE, Opcodes.IF_ICMPGE -> IFGE
                Opcodes.IFGT, Opcodes.IF_ICMPGT -> IFGT
                Opcodes.IFLE -> IFLE
                Opcodes.IFLT -> IFLT
                else -> TODO("Unknown opcode $opcode")
            }
        }

        fun isCondition(opcode: Int): Boolean {
            try {
                fromOpcode(opcode)
                return true
            } catch (e: Throwable) {
                return false
            }
        }
    }
}

class ConditionExp(var left: Expression, var right: Expression, var conType: ConditionType) : Expression() {

    override val stackTypeNeed: org.tlsys.TypeID?
        get() {
            if (right is PopVar)
                return right.type
            if (left is PopVar)
                return left.type
            return null
        }

    override fun push(value: Expression): Boolean {
        if (right is PopVar) {
            right = value
            return true
        }
        if (left is PopVar) {
            left = value
            return true
        }
        return false
    }

    override val type: org.tlsys.TypeID
        get() = org.tlsys.Primitive.Companion.get('Z')

    override fun toString(): String = "$left ${conType.text} $right"
}

class New(override val type: org.tlsys.TypeID) : Expression() {
    override fun toString(): String = "NEW ${type.sinature}"
}

class Math(var left: Expression, var right: Expression, var mathType: MathOp, override val type: org.tlsys.TypeID) : Expression() {

    override val stackTypeNeed: org.tlsys.TypeID?
        get() {
            if (right is PopVar)
                return right.type
            if (left is PopVar)
                return left.type
            return null
        }

    override fun push(value: Expression): Boolean {
        if (right is PopVar) {
            right = value
            return true
        }
        if (left is PopVar) {
            left = value
            return true
        }
        return false
    }

    enum class MathOp(val txt: String) {
        SUB("-")
    }

    override fun toString(): String = "$left $mathType $right (${type.sinature})"
}