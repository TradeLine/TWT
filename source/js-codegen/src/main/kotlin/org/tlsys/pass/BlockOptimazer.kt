package org.tlsys.pass

import org.tlsys.node.Block
import org.tlsys.node.SimpleEdge
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
            optimaze(g.to!!, optimazed)
        }
    }
}
