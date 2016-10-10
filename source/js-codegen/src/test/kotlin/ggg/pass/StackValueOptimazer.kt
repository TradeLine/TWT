package ggg.pass

import ggg.*
import java.util.*

object StackValueOptimazer {

    fun optimazeRecursive(entry: Block, optimazed: HashSet<Block>) {
        println("OPTIMAZED ${entry.ID}")
        optimazed += entry

        var cur = entry.last

        fun resolveStack(st: Statement): Boolean {
            if (st is SetVar) {
                if (st.state.value is InvokeStatic)
                    println("123")
            }
            println("resolve $st")
            var o = st.previous

            while (st.stackNeed !== null) {
                while (o !== null) {
                    if (o!!.stackNeed !== null)
                        resolveStack(o!!)
                    val out = o!!.stackOut
                    val oldPr = o!!.previous
                    if (out !== null) {
                        cur = o.previous
                        st.push(out)
                        o!!.remove()
                        if (st is SkipOne) {
                            st.remove()
                            return true
                        }
                    }



                    if (st.stackNeed === null) {
                        return true
                    }

                    if (oldPr === null) {
                        cur = o.previous
                        return false
                    }

                    o = if (o !== oldPr) oldPr else oldPr
                    if (o === null) {
                        cur = o.previous
                        return false
                    }
                }
            }
            cur = st.previous

            return true
        }
        if (entry.ID == 4)
            println("123")
        while (cur !== null) {
            if (!resolveStack(cur!!))
                TODO()
        }

        for (g in entry.outEdge) {
            if (g.to in optimazed)
                continue
            optimazeRecursive(g.to!!, optimazed)
        }
    }
}