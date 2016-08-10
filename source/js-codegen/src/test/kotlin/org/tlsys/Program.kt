package org.tlsys

import java.util.*

class Program {
    val blocks = ArrayList<BaseBlock>()
    var entryBlock: BaseBlock? = null
    private var tempVarId: Int = 0

    fun getTempId(): Int = --tempVarId

    fun createBlock(regin: String = ""): BaseBlock {
        val b = BaseBlock(this, regin)
        if (blocks.isEmpty())
            entryBlock = b
        blocks += b
        return b
    }

    infix operator fun minusAssign(block: BaseBlock) {
        blocks -= block
        block.outEdge.clear()
        block.inEdge.clear()
    }
}