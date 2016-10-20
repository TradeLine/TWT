package org.tlsys.node

import org.objectweb.asm.Opcodes
import org.objectweb.asm.signature.SignatureReader
import org.objectweb.asm.signature.SignatureVisitor
import org.tlsys.*
import org.tlsys.twt.ClassRef
import org.tlsys.twt.SReader
import org.tlsys.twt.TypeID
import java.util.*

open abstract class Invoke:Expression {

    val params:Array<TypeID>
    val out: TypeID
    //val args:Array<Expression>
    val args = ExpValList<Expression, Invoke>(this, {use(it)}, {unuse(it)})
    val methodName:String
    constructor(methodName:String, signature: String, args:Array<Expression>):super(){
        this.methodName = methodName
        val g = SReader.parse(signature)
        params = g.params.toTypedArray()
        out = g.ret
        this.args += args.toList()
    }
}

class StaticInvoke(val ownClass:String, methodName:String, signature: String, args:Array<Expression>):Invoke(methodName,signature,args) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("[$ownClass].$methodName(")
        var first = true
        for (g in args) {
            if (!first) {
                sb.append(", ")
            } else
                first = false
            sb.append(g)
        }
        sb.append(")")
        return sb.toString()
    }
}
class StaticSpecial(private val _own:Expression, val ownClass: ClassRef, methodName:String, signature: String, args:Array<Expression>):Invoke(methodName,signature,args) {
    init {
        _own.use(this)
    }
    val own:Expression
    get() = _own
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("$own[${ownClass.sinature}].$methodName(")
        var first = true
        for (g in args) {
            if (!first) {
                sb.append(", ")
            } else
                first = false
            sb.append(g)
        }
        sb.append(")")
        return sb.toString()
    }
}

