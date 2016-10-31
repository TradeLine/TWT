package org.tlsys.twt.pass

import org.tlsys.twt.node.Block
import org.tlsys.twt.node.ConditionEdge
import org.tlsys.twt.node.ElseConditionEdge
import org.tlsys.twt.node.PairedEdge

object IfElseOptimizator {
    fun optimaze(entry: Block) {
        optimazeBlock(entry)
    }

    private fun optimazeBlock(block: Block) {
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

        val b = findEnd(block, no.to!!, no.to!!)
        println("=>$b")
    }

    private fun findEnd(start: Block, from:Block, entry: Block): Block {
        if (!entry.isIDom(start))
            return from
        for (g in entry.outEdge) {
            return findEnd(start, entry, g.to!!)
        }
        return from
    }
}