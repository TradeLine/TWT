package org.tlsys.twt.pass

import org.tlsys.twt.node.Block
import org.tlsys.twt.node.SimpleEdge
import java.util.*

object BlockOptimazer {
    fun optimaze(entry: Block, optimazed: HashSet<Block>) {
        optimazed += entry

        if (entry.outEdge.size == 1) {
            val edge = entry.outEdge.iterator().next()
            if (edge is SimpleEdge) {
                val b = edge.to!!
                if (b.inEdge.size == 1) {
                    if (!b.isEmpty()) {
                        var o = b.first
                        while (o !== null) {
                            val n = o.next
                            o.moveToLast(entry)
                            o = n
                        }
                    }
                    b.outEdge.moveTo(entry.outEdge)
                    edge.free()
                }
            }
        }

        for (g in entry.outEdge) {
            if (g.to in optimazed)
                continue
            BlockOptimazer.optimaze(g.to!!, optimazed)
        }

        for (g in entry.inEdge.toList()) {
            if (g.from!!.isEmpty() && g.from!!.inEdge.isEmpty())
                g.free()
        }
    }
}
