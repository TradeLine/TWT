package ggg

private var iterator: Int = 0

class Block(val method: JMethod, private val levelProvider: Block.() -> Int) {

    val level: Int
        get() = levelProvider()

    val ID: Int = ++iterator
    var first: Statement? = null
    var last: Statement? = null

    val inEdge = InEdgeContener(this)
    val outEdge = OutEdgeContener(this)

    fun testValid() {
        if ((first == null && last != null) || (first != null && last == null))
            TODO()

        var o = first
        var h = o
        while (o != null) {
            if (o.block !== this)
                TODO()
            if (o.next !== null)
                h = o.next
            o = o.next
        }

        if (h !== last)
            TODO()
    }

    infix operator fun plusAssign(statement: Statement) {
        testValid()
        if (first === null && last === null) {
            first = statement
            last = statement
        } else {
            if (first === null || last === null)
                TODO()
            last!!.next = statement
            statement.previous = last!!
            last = statement
        }
        statement.block = this

        testValid()
    }

    fun isEmpty(): Boolean = first === null

    fun <T : Statement> find(f: (Statement) -> Boolean): T? {
        var o = first
        while (o !== null) {
            if (f(o))
                return o as T?
            o = o.next
        }

        return null
    }

    companion object {
        val LEVEL_PARENT_MIN: Block.() -> Int = {
            if (inEdge.isEmpty())
                0
            else {
                if (inEdge.size == 1) {
                    inEdge.iterator().next().from!!.level
                } else {
                    var min = Int.MAX_VALUE
                    for (g in inEdge) {
                        val h = g.from!!.level
                        min = if (h < min) h else min
                    }
                    min
                }
            }
        }
    }
}
