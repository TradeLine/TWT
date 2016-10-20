package org.tlsys.twt.generator

import org.tlsys.twt.JMethod
import org.tlsys.twt.NamedVar
import org.tlsys.twt.node.Block
import org.tlsys.twt.node.ConditionEdge
import org.tlsys.twt.node.ElseConditionEdge
import org.tlsys.twt.node.SimpleEdge
import org.tlsys.twt.statement.*
import org.tlsys.twt.statement.ConditionExp
import org.tlsys.twt.statement.New
import org.tlsys.twt.statement.Return

object MethodBodyGenerator {
    fun generate(method: JMethod, writer: Appendable) {
        MethodBodyGenerator.generateBlock(method.entryBlock, writer)

    }

    fun simpleGenerateBlock(block: Block, writer: Appendable){
        val it = block.nextIterator
        var lines = 0
        while (it.hasNext()) {
            val s = it.next()
            if (s is SetVar) {
                if (s.state.value is InitValue) {
                    continue
                }
            }
            writer.append("${++lines}.\t")
            MethodBodyGenerator.generateStatement(s, writer)
            writer.append("\n")
        }
    }

    fun generateBlock(block: Block, writer: Appendable) {
        MethodBodyGenerator.simpleGenerateBlock(block, writer)
        if (block.outEdge.size == 1 && block.outEdge.iterator().next() is SimpleEdge) {
            MethodBodyGenerator.generateBlock(block.outEdge.iterator().next().to!!, writer)
            return
        }

        if (block.outEdge.size == 2) {
            val it = block.outEdge.iterator()
            val e1 = it.next()
            val e2 = it.next()
            if ((e1 is ConditionEdge && e1.pair == e2) || (e2 is ConditionEdge && e2.pair == e1)) {
                val yes: ConditionEdge
                val no: ElseConditionEdge
                if (e1 is ConditionEdge) {
                    yes = e1
                    no = e2 as ElseConditionEdge
                } else {
                    yes = e2 as ConditionEdge
                    no = e1 as ElseConditionEdge
                }

                writer.append("if (!(")
                MethodBodyGenerator.generateExpression(yes.value, writer)
                writer.append(")){\n")
                MethodBodyGenerator.simpleGenerateBlock(no.to!!, writer)
                writer.append("}else{\n")
                MethodBodyGenerator.simpleGenerateBlock(yes.to!!, writer)
                writer.append("}")

            }
            return
        }

        if (block.outEdge.isEmpty())
            return

        TODO()
    }

    fun generateStatement(s: Statement, writer: Appendable) {
        when (s) {
            is SetVar -> {
                if (s.state.parent is NamedVar) {
                    writer.append((s.state.parent as NamedVar).name)
                } else {
                    writer.append(s.state.parent.toString())
                }
                writer.append("=")
                MethodBodyGenerator.generateExpression(s.state.value, writer)
                return
            }
            is LabelSt -> {
                writer.append("L${s.hashCode()}:")
                return
            }
            is ExeInvoke -> {
                MethodBodyGenerator.generateExpression(s.invoke, writer)
                return
            }
            is Return ->{
                writer.append("return")
                return
            }
            else -> TODO("${s.javaClass.name}")
        }
    }

    fun generateExpression(e: Expression, writer: Appendable) {

        fun writeArgs(list: Array<Expression>) {
            var first = true
            for (arg in list) {
                if (!first)
                    writer.append(", ")
                MethodBodyGenerator.generateExpression(arg, writer)
                first = false
            }
        }

        when (e) {
            is New -> {
                writer.append("new ${e.type.sinature.replace('/', '.')}()")
                return
            }
            is InvokeStatic -> {
                writer.append("${e.parentClass.sinature.replace('/', '.')}.${e.methodName}(")
                writeArgs(e.arguments)
                writer.append(")")
                return
            }
            is InvokeSpecial -> {
                MethodBodyGenerator.generateExpression(e.self, writer)
                writer.append(".${e.methodName}(")
                writeArgs(e.arguments)
                writer.append(")")
                return
            }
            is IntValue -> {
                writer.append(e.value.toString())
                return
            }
            is Math -> {
                MethodBodyGenerator.generateExpression(e.left, writer)
                writer.append(when (e.mathType) {
                    Math.MathOp.SUB -> "-"
                })
                MethodBodyGenerator.generateExpression(e.right, writer)
                return
            }
            is ConditionExp -> {
                MethodBodyGenerator.generateExpression(e.left, writer)
                writer.append(when (e.conType) {
                    ConditionType.IFEQ -> "=="
                    ConditionType.IFNE -> "!="

                    ConditionType.IFLT -> "<"
                    ConditionType.IFLE -> "<="

                    ConditionType.IFGT -> ">"
                    ConditionType.IFGE -> ">="
                    ConditionType.AND -> "&&"
                    ConditionType.OR -> "||"
                })
                MethodBodyGenerator.generateExpression(e.right, writer)
                return
            }
            is GetVar -> {
                if (e.state.parent is NamedVar) {
                    writer.append(e.state.parent.name)
                } else {
                    writer.append(e.state.parent.toString())
                }
                return
            }
            else -> TODO("${e.javaClass.name}")
        }
        writer.append(e.toString())
    }
}