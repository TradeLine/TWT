package org.tlsys.twt.graph

import org.tlsys.twt.node.Block
import java.util.*

fun findEnds(root: Block): Array<Block> {

    val visited = HashSet<Block>()
    val ends = HashSet<Block>()

    fun step(n: Block) {
        if (n in visited)
            return
        if (n.outEdge.isEmpty()) {
            ends += n
            return
        }
        for (i in n.outEdge) {
            step(i.to!!)
        }
    }
    step(root)
    return ends.toTypedArray()
}