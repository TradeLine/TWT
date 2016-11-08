package org.tlsys.twt.pass

import org.tlsys.twt.graph.LCATree
import org.tlsys.twt.graph.findEnds
import org.tlsys.twt.node.Block
import org.tlsys.twt.node.ConditionEdge
import org.tlsys.twt.node.ElseConditionEdge
import org.tlsys.twt.node.PairedEdge
import java.util.*

object IfElseOptimizator {
    fun optimaze(entry: Block) {
        val ends = findEnds(entry)
        optimazeBlock(entry, ends)
    }

    private fun optimazeBlock(block: Block, ends:Array<Block>) {
        if (block.outEdge.size != 2)
            return
        val it = block.outEdge.iterator()
        val e1 = it.next()
        val e2 = it.next()
        if (e1 !is PairedEdge || e2 !is PairedEdge)
            return
        val yes: ConditionEdge
        val no: ElseConditionEdge
        if (e1 is ConditionEdge) {
            yes = e1
            no = e2 as ElseConditionEdge
        } else {
            yes = e2 as ConditionEdge
            no = e1 as ElseConditionEdge
        }



        val visited = HashSet<Block>()
        fun end(n: Block): Block? {
            if (n in visited)
                return null
            visited+=n
            if (n.dominator == block)
                return n
            for (e in n.outEdge) {
                val g = end(e.to!!)
                if (g !== null)
                    return g
            }
            return null
        }

        for (e in ends) {
            val tr = LCATree(e, false)
            val b = tr[yes.to!!, no.to!!]
            println("b=$b")
        }

        /*
        val yes_e = end(yes.to!!)
        visited.clear()
        val no_e = end(no.to!!)
        println("$yes_e, $no_e")
        */
    }

    private fun findEnd(start: Block, from: Block, entry: Block): Block {
        if (!entry.isIDom(start))
            return from
        for (g in entry.outEdge) {
            return findEnd(start, entry, g.to!!)
        }
        return from
    }
}