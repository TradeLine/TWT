package org.tlsys.twt.node

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.tlsys.twt.graph.buildDominationTree
import java.util.*

class BlockTest {

    val blocks = HashMap<String, Block>()
    val blocks_name = HashMap<Block, String>()

    @Before
    fun clear() {
        blocks.clear()
    }

    val Block.name: String
        get() = blocks_name[this]!!

    val String.block: Block
        get() = blocks[this]!!

    fun block(a1: String, a2: String) {
        fun getOrCreate(a: String): Block {
            var g = blocks[a]
            if (g !== null)
                return g
            g = Block()
            blocks[a] = g
            blocks_name[g] = a
            return g
        }
        SimpleEdge(from = getOrCreate(a1), to = getOrCreate(a2), resion = "");
    }

    infix fun String.to(b: String) {
        block(this, b)
    }

    operator fun String.rangeTo(b: String):String {
        block(this, b)
        return b
    }

    @Test
    fun test_idom_simple() {
        "A".."B".."C".."D"
        buildDominationTree("A".block)
        assertTrue("D".block.isIDom("A".block))
        assertTrue("D".block.isIDom("C".block))
        assertTrue("D".block.isIDom("B".block))
        assertTrue("D".block.isIDom("D".block))
        assertTrue("A".block.isIDom("A".block))

        clear()

        "A".."B"
        "B".."F1"
        "B".."F2"
        buildDominationTree("A".block)
        assertTrue("F1".block.isIDom("B".block))
        assertTrue("F1".block.isIDom("A".block))
        assertFalse("F1".block.isIDom("F2".block))
    }
}