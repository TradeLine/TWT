package org.tlsys.twt.graph

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.tlsys.twt.JMethod
import org.tlsys.twt.node.Block
import org.tlsys.twt.node.SimpleEdge
import java.util.*

class LCATreeTest {
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

    operator fun String.rangeTo(b: String) {
        block(this, b)
    }

    @Test
    fun test_one_if_short() {
        "R" to "YES"
        "R" to "NO"
        "YES" to "RESULT"
        "NO" to "RESULT"

        val tree = LCATree("R".block)

        assertEquals(tree["YES".block, "NO".block], "R".block)
    }

    @Test
    fun test_one_if_long() {
        "R".."YES1"
        "R".."NO1"

        "YES1".."YES2"
        "NO1".."NO2"

        "YES2".."RESULT"
        "NO2".."RESULT"

        val tree = LCATree("R".block)
        assertEquals(tree["YES2".block, "NO1".block], "R".block)
        assertEquals(tree["YES1".block, "NO2".block], "R".block)
        assertEquals(tree["YES2".block, "NO2".block], "R".block)
    }

    @Test
    fun test_one_if_long_with_long_root() {
        "R1".."R2"

        "R2".."YES1"
        "R2".."NO1"

        "YES1".."YES2"
        "NO1".."NO2"

        "YES2".."RESULT"
        "NO2".."RESULT"

        val tree = LCATree("R1".block)
        assertEquals(tree["YES2".block, "NO1".block], "R2".block)
        assertEquals(tree["YES1".block, "NO2".block], "R2".block)
        assertEquals(tree["YES2".block, "NO2".block], "R2".block)
    }
}