package org.tlsys.twt

import org.junit.Test
import org.tlsys.twt.graph.buildDominationTree
import org.tlsys.twt.node.Block
import org.tlsys.twt.node.SimpleEdge

class DominationTest {
    @Test
    fun show() {
        val method = JMethod()

        fun Block(from: Block, name: String): Block {
            val test = Block(method, Block.Companion.LEVEL_PARENT_MIN)
            SimpleEdge(from, test, "")
            test.description = name
            return test
        }


        val R = Block(method, Block.Companion.LEVEL_PARENT_MIN)
        R.description = "R"


        val B = Block(R, "B")
        val C = Block(R, "C")
        val F = Block(C, "F")
        val G = Block(C, "G")
        val I = Block(G, "I")
        SimpleEdge(F, I, "")
        val A = Block(B, "A")
        val D = Block(A, "D")
        SimpleEdge(B, D, "")
        SimpleEdge(R, A, "")
        val L = Block(D, "L")
        val H = Block(L, "H")
        val E = Block(H, "E")
        SimpleEdge(E, H, "")
        SimpleEdge(B, E, "")
        val J = Block(G, "J")
        SimpleEdge(J, I, "")

        val K = Block(I, "K")
        SimpleEdge(K, I, "")
        SimpleEdge(K, R, "")
        SimpleEdge(H, K, "")
        buildDominationTree(R)
        //Viwer.show("Domination tree", R)
    }
}