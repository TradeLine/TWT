package ggg.pass

import ggg.*
import java.util.*

object StackValueOptimazer {

    fun optimazeRecursive(entry: Block, optimazed: HashSet<Block>) {
        println("OPTIMAZED ${entry.ID}")
        optimazed += entry

        var o = entry.last

        fun resolveStack(st: Statement): Boolean {
            println("resolve $st")
            var o = st.previous

            while (st.stackNeed !== null) {
                while (o !== null) {
                    if (o!!.stackNeed !== null)
                        resolveStack(o!!)
                    val out = o!!.stackOut
                    val oldPr = o!!.previous
                    if (out !== null) {
                        st.push(out)
                        o!!.remove()
                    }

                    if (st is SkipOne) {
                        st.remove()
                        return true
                    }

                    if (st.stackNeed===null)
                        return true

                    if (oldPr === null)
                        return false

                    o = if (o !== oldPr) oldPr else oldPr
                    if (o === null)
                        return false
                }
            }

            return true
        }

        while (o !== null) {
            if (!resolveStack(o!!))
                TODO()
            o = o!!.previous
        }

        for (g in entry.outEdge) {
            if (g.to in optimazed)
                continue
            optimazeRecursive(g.to!!, optimazed)
        }
    }
}