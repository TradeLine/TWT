package ggg.pass

import ggg.*
import java.util.*

object StackValueOptimazer {

    private fun resolveStack(current: Statement): Boolean {
        if (current is SetVar)
            println(123)
        while (current.stackNeed !== null) {//пока нужно значение из стека
            val it = current.previousIterator
            while (it.hasNext()) {
                var v = it.next()
                if (v.stackNeed !== null){
                    if (resolveStack(v)) {
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
        println("OPTIMAZED ${entry.ID}")
        optimazed += entry

        val cur = entry.previousIterator




        if (entry.ID == 1)
            println("123")
        while (cur.hasNext()) {
            if (resolveStack(cur.next()))
                cur.remove()
        }

        for (g in entry.outEdge) {
            if (g.to in optimazed)
                continue
            optimazeRecursive(g.to!!, optimazed)
        }
    }
}