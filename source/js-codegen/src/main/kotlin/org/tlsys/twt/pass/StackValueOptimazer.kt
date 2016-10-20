package org.tlsys.twt.pass

import org.tlsys.twt.node.Block
import org.tlsys.twt.statement.SkipOne
import org.tlsys.twt.statement.Statement
import java.util.*

object StackValueOptimazer {

    private fun resolveStack(current: Statement): Boolean {
        while (current.stackNeed !== null) {//пока нужно значение из стека
            val it = current.previousIterator
            while (it.hasNext()) {
                val v = it.next()
                if (v.stackNeed !== null){
                    if (StackValueOptimazer.resolveStack(v)) {
                        it.remove()
                    }
                    break
                }
                if (v.stackOut !== null) {
                    current.push(v.stackOut!!)
                    it.remove()

                    if (current is SkipOne) {
                        return true
                    }
                    break
                }
            }
        }
        return false
    }

    fun optimazeRecursive(entry: Block, optimazed: HashSet<Block>) {
        optimazed += entry

        val cur = entry.previousIterator




        while (cur.hasNext()) {
            if (StackValueOptimazer.resolveStack(cur.next()))
                cur.remove()
        }

        for (g in entry.outEdge) {
            if (g.to in optimazed)
                continue
            StackValueOptimazer.optimazeRecursive(g.to!!, optimazed)
        }
    }
}