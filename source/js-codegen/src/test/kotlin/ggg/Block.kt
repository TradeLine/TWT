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

    infix operator fun plusAssign(statement: Statement) {
        if (first === null && last === null) {
            first = statement
            last = statement
        } else {
            if (first === null || last === null)
                TODO()
            last!!.next = statement
            last = statement
        }
        statement.block = this
    }

    fun isEmpty(): Boolean = first === null

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