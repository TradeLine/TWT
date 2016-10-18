package org.tlsys.node

import org.objectweb.asm.Opcodes
import org.objectweb.asm.signature.SignatureReader
import org.objectweb.asm.signature.SignatureVisitor
import org.tlsys.*
import java.util.*

open abstract class Invoke:Expression {

    val params:Array<TypeID>
    val out:TypeID
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
class StaticSpecial(private val _own:Expression, val ownClass:ClassRef, methodName:String, signature: String, args:Array<Expression>):Invoke(methodName,signature,args) {
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

class SReader : SignatureVisitor {
    constructor() : super(Opcodes.ASM5) {
    }

    companion object {
        fun parse(signature: String): SReader {
            val r = SReader()
            SignatureReader(signature).accept(r)
            return r
        }
    }

    val params = ArrayList<TypeID>()
    var ret: TypeID = UNKNOWN_TYPE

    override fun visitReturnType(): SignatureVisitor {
        return object : SignatureVisitor(Opcodes.ASM5) {
            var arr: Int = 0
            override fun visitArrayType(): SignatureVisitor {
                arr++
                return super.visitArrayType()
            }

            override fun visitClassType(name: String?) {
                var t: TypeID = ClassRef.get(name!!)
                while (arr > 0) {
                    arr--
                    t = t.asArray()
                }
                ret = t
            }

            override fun visitBaseType(descriptor: Char) {
                var t: TypeID? = Primitive.get(descriptor)
                if (t == null)
                    throw RuntimeException("Type $descriptor not found")
                while (arr > 0) {
                    arr--
                    t = t!!.asArray()
                }
                ret = t!!
            }
        }
    }

    override fun visitParameterType(): SignatureVisitor {
        val g = params.size
        params.add(UNKNOWN_TYPE)
        return object : SignatureVisitor(Opcodes.ASM5) {
            var arr: Int = 0
            override fun visitArrayType(): SignatureVisitor {
                arr++
                return super.visitArrayType()
            }

            override fun visitClassType(name: String?) {
                var t: TypeID = ClassRef.get(name!!)
                while (arr > 0) {
                    arr--
                    t = t.asArray()
                }
                params[g] = t
            }

            override fun visitBaseType(descriptor: Char) {
                var t: TypeID? = Primitive.get(descriptor)
                if (t == null)
                    throw RuntimeException("Type $descriptor not found")
                while (arr > 0) {
                    arr--
                    t = t!!.asArray()
                }
                params[g] = t!!
            }
        }
    }
}