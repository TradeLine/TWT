package ggg

import org.junit.Assert.*
import org.junit.Test

class StatementTest {

    class S(val t: String = "") : Statement()

    @Test
    fun testAdd() {
        val method = JMethod()
        val block1 = Block(method, Block.LEVEL_PARENT_MIN)
        val s1 = S()
        assertNull(s1.block)
        assertNull(s1.previous)
        assertNull(s1.next)
        assertNull(block1.first)
        assertNull(block1.last)
        block1 += s1
        assertEquals(s1.block, block1)
        assertNull(s1.previous)
        assertNull(s1.next)
        assertEquals(block1.first, s1)
        assertEquals(block1.last, s1)

        val s2 = S()
        block1+=s2

        assertEquals(s1.block, block1)
        assertNull(s1.previous)
        assertEquals(s1.next,s2)
        assertEquals(s2.previous,s1)
        assertEquals(block1.first, s1)
        assertEquals(block1.last, s2)
    }

    @Test
    fun testRemove() {
        val method = JMethod()
        val block1 = Block(method, Block.LEVEL_PARENT_MIN)

        val s1=S()
        block1+=s1

        s1.remove()

        assertNull(block1.first)
        assertNull(block1.last)

        assertNull(s1.block)
        assertNull(s1.previous)
        assertNull(s1.next)
        block1+=s1
        val s2=S()
        block1+=s2
        val s3=S()
        block1+=s3

        s2.remove()
        assertNull(s2.block)
        assertNull(s2.previous)
        assertNull(s2.next)

        assertEquals(s1.next,s3)
        assertEquals(s3.previous,s1)
    }

    @Test
    fun testMoveTo_one() {
        val method = JMethod()
        val block1 = Block(method, Block.LEVEL_PARENT_MIN)
        val block2 = Block(method, Block.LEVEL_PARENT_MIN)
        val s1 = S()
        block1 += s1
        s1.moveToLast(block2)
        assertNull(block1.first)
        assertNull(block1.last)

        assertEquals(block2, s1.block)
        assertEquals(block2.first, s1)
        assertEquals(block2.last, s1)

        val s2 = S()
        block2 += s2
        val s3 = S()
        block2 += s3
    }
}