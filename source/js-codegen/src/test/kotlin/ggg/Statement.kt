package ggg

import org.objectweb.asm.Label
import org.objectweb.asm.tree.LabelNode
import org.tlsys.ClassRef
import org.tlsys.Primitive
import org.tlsys.TypeID
import org.tlsys.node.ConditionType
import org.tlsys.node.Push
import java.util.*

open class Statement() {
    private var _previous: Statement? = null
    var block: Block? = null
    var previous: Statement?
        get() = _previous
        set(it) {
            _previous = it

            /*
            val oldNext = _next
            val oldPre = _previous
            if (oldNext !== null)
                oldNext.previous = oldPre
            if (oldPre === null && block !== null) {
                block!!.first = oldNext
            }
            */

            if (it !== null)
                this.block = it.block

            if (it !== null && it.next !== this) {
                it.next = this
            }
        }
    private var _next: Statement? = null
    var next: Statement?
        get() = _next
        set(it) {
            if (it === _next)
                return

            /*
            val oldNext = _next
            val oldPre = _previous
            if (oldNext !== null)
                oldNext.previous = oldPre
            if (oldPre === null && block !== null) {
                block!!.first = oldNext
            }
            */

            _next = it
            if (it !== null)
                this.block = it.block
            if (it !== null && it.previous !== this) {
                it.previous = this
            }
        }

    fun remove() {
        val p = previous
        val n = next
        if (n !== null)
            n.previous = p
        if (p !== null)
            p.next = n
    }

    open val stackOut: Expression? = null
    open val stackNeed: TypeID? = null
    open fun push(value: Expression): Boolean = false

    fun moveToLast(block: Block) {
        if (block.last === null) {
            this.block = block
            block.first = this
            block.last = this
            previous = null
            next = null
        } else {
            TODO()
        }
    }
}

class GetVar(val state: Var.VarState) : Expression() {
    override val type: TypeID
        get() = state.parent.type

    override fun toString(): String = "${state.parent}"
}


class SkipOne : Statement() {

    var value: Expression? = null

    override val stackNeed: TypeID?
        get() = if (value === null) Primitive.get('V') else null

    override fun push(value: Expression): Boolean {
        if (this.value === null) {
            this.value = value
            return true
        }
        return false
    }

    override fun toString(): String = "POP${if (value === null) "" else " VAL:$value"}"
}

class InitValue(private var initType: TypeID) : Expression() {
    override val type: TypeID
        get() = initType

    override fun toString(): String = "P:INIT (${initType.sinature})"
}

class UnknownVarValue(val parent: Var) : Expression() {
    override val type: TypeID
        get() = parent.type

    override fun toString(): String = "IK_$parent"
}

abstract open class Expression() {
    abstract val type: TypeID
    open val stackTypeNeed: TypeID? = null
    open fun push(value: Expression): Boolean = false
}

class Return() : Statement() {
    override fun toString(): String = "RETURN"
}

class LabelSt(val label: Label) : Statement()

class StringValue(val value: String) : Expression() {
    override val type: TypeID
        get() = ClassRef.get("STRING")

    override fun toString(): String = "P:$value"
}

class IntValue(val value: Int) : Expression() {
    override val type: TypeID
        get() = Primitive.get('I')

    override fun toString(): String = "P:$value"
}

class ExeInvoke(val invoke: Invoke) : Statement() {
    override val stackNeed: TypeID?
        get() = invoke.stackTypeNeed

    override fun push(value: Expression): Boolean {
        return invoke.push(value)
    }
}

class PushVar(val state: Var.VarState) : Statement() {
    override val stackOut: Expression?
        get() = GetVar(state)

    override fun toString(): String = "P:VAR ${state.parent}[${state.parent.type.sinature}]"
}

class SetVar(val state: Var.VarState) : Statement() {
    override fun toString(): String = "O:SET VAR ${state.parent}  =  {${state.value}}"

    override val stackNeed: TypeID?
        get() = state.value.stackTypeNeed

    override fun push(value: Expression): Boolean {
        return state.value.push(value)
    }
}

fun Statement.split(): Block {
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
        cur = cur.next
    }
    newBlock.last = cur

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


class PopVar(override val type: TypeID) : Expression() {
    override fun toString(): String = "ST"
}

open class Invoke(val methodName: String, val arguments: Array<Expression>, override var type: TypeID) : Expression() {

    override val stackTypeNeed: TypeID?
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

        return false;
    }
}

class InvokeStatic(methodName: String, arguments: Array<Expression>, type: TypeID) : Invoke(methodName, arguments, type) {
    override fun toString(): String = "P:INV_STATIC $methodName(${arguments.joinToString(",")}):${type.sinature}"
}

class InvokeSpecial(var self: Expression, methodName: String, arguments: Array<Expression>, type: TypeID) : Invoke(methodName, arguments, type) {
    override val stackTypeNeed: TypeID?
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
}

class ConditionExp(var left: Expression, var right: Expression, var conType: ConditionType) : Expression() {

    override val stackTypeNeed: TypeID?
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

    override val type: TypeID
        get() = Primitive.get('Z')

    override fun toString(): String = "P:$left ${conType.text} $right"

}

class Math(var left: Expression, var right: Expression, var mathType: MathOp, override val type: TypeID) : Expression() {

    override val stackTypeNeed: TypeID?
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