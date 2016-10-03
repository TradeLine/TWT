package ggg

import org.junit.Test
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.tlsys.*
import org.tlsys.ClassV
import org.tlsys.node.SReader
import java.util.*

open class Var(val type: TypeID) {

    private var _first: VarState? = null

    private val first: VarState
        get() = _first!!

    fun first(value: Expression): VarState {
        if (_first !== null)
            TODO()
        _first = VarState(this, null, value)
        return _first!!
    }

    class VarState(val parent: Var, val from: VarState?, val value: Expression) {
        fun set(exp: Expression) = VarState(parent, this, exp)
    }
}

class NamedVar(val index: Int, type: TypeID) : Var(type) {
    var name: String = "V$index"
}

class JMethod() {
    val entryBlock = Block(this, {0})
    val namedVar = HashMap<Int, NamedVar>()
    val tempVar = ArrayList<Var>()

    fun createArg(index: Int, type: TypeID): NamedVar {
        val v = NamedVar(index, type)
        namedVar[index] = v
        return v
    }

    fun createVar(index: Int, type: TypeID) = createArg(index, type)

    fun getVar(index: Int): NamedVar? = namedVar[index]

    fun createTemp(type: TypeID): Var {
        val v = Var(type)
        tempVar += v
        return v
    }
}

