package org.tlsys.level2

import org.tlsys.BaseBlock
import org.tlsys.NamedVar
import org.tlsys.Var
import org.tlsys.edge.ConditionEdge
import org.tlsys.edge.ElseConditionEdge
import org.tlsys.edge.PairedEdge
import org.tlsys.edge.SimpleEdge
import org.tlsys.node.*
import java.util.*

object Optimazer {
    fun optimaze(block: BaseBlock) {
        //optimazeEmptyVars(block)
        //optimazeAnd(block)
        //optimazeTernar(block)
    }

    private fun optimazeEmptyVars(block: BaseBlock) {
        val it = block.operationIterator()
        while (it.hasNext()) {
            val n = it.next()
            if (n is Var.SetVar) {
                var flag = false
                if (n.item.parent is NamedVar && (n.item.parent as NamedVar).name == "b") {
                    flag = true
                    println("123")
                }
                if (n.item.using.isEmpty() && n.item.vars.isEmpty()) {
                    if (flag)
                        println("123")
                    println("Replace $n => ${n.item.value}")
                    it.set(n.item.value)
                }
                continue
            }
        }
        for (g in block.outEdge)
            optimazeEmptyVars(g.to!!)
    }

    //работает стабильно и хорошо
    private fun optimazeAnd(block: BaseBlock) {
        for (e in block.outEdge)
            optimazeAnd(e.to!!)

        for (g in block.outEdge.toList().toTypedArray()) {
            if (g !in block.outEdge)
                continue
            if (g !is PairedEdge)
                continue

            if (g.to!!.operationCount != 0)
                continue
            if (g.to!!.steck.addedSize != 0)
                continue


            val e_else = g.pair ?: break
            val else_block = e_else.to!!

            val then_block = g.to!!

            val h = (then_block.outEdge.find { it.to == e_else.to && it is PairedEdge } ?: break) as PairedEdge
            val end = h.pair!!.to!!

            g.to!!.program.blocks -= g.to!!
            h.pair!!.to = null
            h.pair!!.from = null
            g.to = null
            g.from = null
            e_else.from = null
            e_else.to = null
            h.from = null
            h.to = null

            if (g is ConditionEdge) {
                TODO()
            } else {
                val newExp = ConditionExp(g.value.not(), h.value, ConditionType.OR)
                val c = ConditionEdge(block, else_block, newExp)
                val n = ElseConditionEdge(c, end)
            }


        }
    }

    //отбирает "хлеб" у функции оптимизации "И"
    private fun optimazeTernar(block: BaseBlock) {
        for (e in block.outEdge.toList().toTypedArray()) {
            if (e in block.outEdge)
                optimazeTernar(e.to!!)
        }
        val pairs = findTernar(block)
        if (pairs.isNotEmpty()) {
            for (p in pairs) {
                val left = p.if_edge.to!!.steck.getOne { !it.marged }
                val right = p.else_edge.to!!.steck.getOne { !it.marged }
                if (left.usingVariants.size != right.usingVariants.size)
                    throw TODO("Не совпадает количество использований значений тренарного оператора")

                if (!left.usingVariants.containsAll(right.usingVariants))
                    throw TODO("Не совпадают значения, где использовались тринарные операторы")

                assert(p.if_edge.from === p.else_edge.from)
                assert(p.if_edge.to!!.outEdge.getOne().to === p.else_edge.to!!.outEdge.getOne().to)
                val branchBlock = p.if_edge.from!!
                val connectBlock = p.if_edge.to!!.outEdge.getOne().to!!


                p.if_edge.to!!.steck.remove(left)
                p.else_edge.to!!.steck.remove(right)

                /*
                fun clearOutEdges(b:BaseBlock) {
                    b.outEdge.clear()
                    b.inEdge.clear()
                }

                clearOutEdges(p.if_edge.to!!)
                clearOutEdges(p.else_edge.to!!)
                */
                branchBlock.program -= p.if_edge.to!!
                branchBlock.program -= p.else_edge.to!!

                p.if_edge.to = null
                p.if_edge.from = null

                p.else_edge.to = null
                p.else_edge.from = null

                val goto = SimpleEdge(branchBlock, connectBlock)

                connectBlock.inEdge += goto
                branchBlock.outEdge += goto

                val t = TernarOp(p.if_edge.value, left, right)
                branchBlock.steck.push(t)


                for (v in left.usingVariants) {
                    v -= left
                    v -= right
                    v += t
                }
            }
            println("Ternar operator founeded!")
        }


    }

    private data class EdigPair(val if_edge: ConditionEdge, val else_edge: ElseConditionEdge)

    /*
        private fun findAnd(block: BaseBlock): List<EdigPair> {
            val pairs = ArrayList<EdigPair>(Math.floor(block.outEdge.size / 2.0).toInt())
            for (g in block.outEdge) {
                if (g !is ConditionEdge)
                    continue
                if (g.to!!.operationCount != 0)
                    continue
                if (g.to!!.steck.addedSize != 0)
                    continue


                val e = g.pair ?: break

                if (e.to!!.operationCount == 0 && e.to!!.steck.addedSize == 0) {

                }
                if (true) {
                    val e = block.outEdge.find {
                        if (it !is ElseConditionEdge)
                            return@find false
                        if (it.fromEdge !== g)
                            return@find false

                        return@find it.to!!.operationCount == 0 && (it.to!!.steck.addedSize == 1/* || it.to.outEdge.filter { findTernar(it.to).isNotEmpty() } != null*/)
                    }
                    if (e != null)
                        pairs += EdigPair(g, e as ElseConditionEdge)
                }

            }
            return pairs
        }
    */
    private fun findTernar(block: BaseBlock): List<EdigPair> {
        val pairs = ArrayList<EdigPair>(Math.floor(block.outEdge.size / 2.0).toInt())

        for (g in block.outEdge) {
            if (g !is ConditionEdge)
                continue
            if (g.to!!.operationCount != 0)
                continue
            if (g.to!!.steck.addedSize != 1)
                continue
            if (true) {
                val e = block.outEdge.find {
                    if (it !is ElseConditionEdge)
                        return@find false
                    if (it.fromEdge !== g)
                        return@find false

                    return@find it.to!!.operationCount == 0 && (it.to!!.steck.addedSize == 1/* || it.to.outEdge.filter { findTernar(it.to).isNotEmpty() } != null*/)
                }
                if (e != null)
                    pairs += EdigPair(g, e as ElseConditionEdge)
            }

        }
        return pairs
    }

}