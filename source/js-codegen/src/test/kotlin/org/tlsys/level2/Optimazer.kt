package org.tlsys.level2

import org.tlsys.BaseBlock
import org.tlsys.edge.ConditionEdge
import org.tlsys.edge.ElseConditionEdge
import org.tlsys.edge.SimpleEdge
import java.util.*

object Optimazer {
    fun optimaze(block: BaseBlock) {
        optimazeTernar(block)
    }

    private fun optimazeTernar(block: BaseBlock) {
        for (e in block.outEdge)
            optimazeTernar(e.to!!)
        val pairs = findTernar(block)
        if (pairs.isNotEmpty()) {
            for (p in pairs) {
                val left = p.if_edge.to!!.steck.getOne()
                val right = p.else_edge.to!!.steck.getOne()
                if (left.usingVariants.size != right.usingVariants.size)
                    throw TODO("Не совпадает количество использований значений тренарного оператора")

                if (!left.usingVariants.containsAll(right.usingVariants))
                    throw TODO("Не совпадают значения, где использовались тринарные операторы")

                assert(p.if_edge.from === p.else_edge.from)
                assert(p.if_edge.to!!.outEdge.getOne().to === p.else_edge.to!!.outEdge.getOne().to)
                val branchBlock = p.if_edge.from!!
                val connectBlock = p.if_edge.to!!.outEdge.getOne().to!!


                p.if_edge.to!!.steck.remove(left)
                //left.unsteck(p.if_edge.to)
                //right.unsteck(p.else_edge.to)
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

    private fun findTernar(block: BaseBlock): List<EdigPair> {
        val pairs = ArrayList<EdigPair>(Math.floor(block.outEdge.size / 2.0).toInt())

        for (g in block.outEdge) {
            if (g !is ConditionEdge)
                continue
            if (g.to!!.operationCount != 0)
                continue
            if (g.to!!.steck.size != 1)
                continue
            if (true) {
                val e = block.outEdge.find {
                    if (it !is ElseConditionEdge)
                        return@find false
                    if (it.fromEdge !== g)
                        return@find false

                    return@find it.to!!.operationCount == 0 && (it.to!!.steck.size == 1/* || it.to.outEdge.filter { findTernar(it.to).isNotEmpty() } != null*/)
                }
                if (e != null)
                    pairs += EdigPair(g, e as ElseConditionEdge)
            }

        }
        return pairs
    }

}