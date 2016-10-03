package ggg

import org.objectweb.asm.Label
import org.objectweb.asm.tree.LabelNode
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
            if (it !== null && it.next !== this) {
                it.next = this
            }
        }
    private var _next: Statement? = null
    var next: Statement?
        get() = _next
        set(it) {
            _next = it
            if (it !== null && it.previous !== this) {
                it.previous = this
            }
        }
}

interface StackOperation
interface PopStack : StackOperation

class SkipOne : PopStack, Statement()

interface PushStack : StackOperation

class InitValue(private var initType: TypeID) : Expression() {
    override val type: TypeID
        get() = initType
}

abstract open class Expression() : Statement() {
    abstract val type: TypeID
}

class Return() : Statement()

class LabelSt(val label: Label) : Statement()

class IntValue(val value: Int) : Expression() {
    override val type: TypeID
        get() = Primitive.get('I')

}

class GetVar(val state: Var.VarState) : Expression(), PushStack {
    override val type: TypeID
        get() = state.parent.type
}

class SetVar(val state: Var.VarState) : Statement()

fun Statement.split(): Block {
    val newBlock = Block(block!!.method, Block.LEVEL_PARENT_MIN)

    SimpleEdge(block!!, newBlock)

    if (previous !== null) {
        block!!.last = previous!!
        previous!!.next = null
    } else {
        block!!.last = null
        block!!.first = null
    }
    var cur: Statement? = this
    newBlock.first = this
    while (cur != null) {
        cur.block = newBlock
        newBlock.last = cur
        cur = cur.next
    }

    return newBlock
}

fun Statement.findValueOfVar(v: Var): Var.VarState {
    if (this is SetVar && state.parent === v)
        return state

    if (previous === null) {
        if (block!!.inEdge.size == 1) {
            val e = block!!.inEdge.iterator().next()
            if (e is SimpleEdge)
                return e.from!!.last!!.findValueOfVar(v)
        }
        val findedValues = ArrayList<Var.VarState>()
        for (g in block!!.inEdge) {
            val d = g.from!!.last!!.findValueOfVar(v)
            if (d !in findedValues)
                findedValues += d
        }
        if (findedValues.isEmpty())
            TODO()
        if (findedValues.size == 1)
            return findedValues[0]
        TODO()
    } else {
        return previous!!.findValueOfVar(v)
    }
}

class PopOne(override val type: TypeID) : Expression(), PopStack

open class Invoke(val methodName: String, val arguments: Array<Expression>, override var type: TypeID) : Expression(), PopStack {

}

class InvokeStatic(methodName: String, arguments: Array<Expression>, type: TypeID) : Invoke(methodName, arguments, type) {

}

class InvokeSpecial(val self: Expression, methodName: String, arguments: Array<Expression>, type: TypeID) : Invoke(methodName, arguments, type) {

}

class ConditionExp(var left: Expression, var right: Expression, var conType: ConditionType) : Expression(), PopStack {
    override val type: TypeID
        get() = Primitive.get('Z')

}

class Math(var left: Expression, var right: Expression, var mathType: MathOp, override val type: TypeID) : Expression() {
    enum class MathOp {
        SUB
    }
}