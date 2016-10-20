package org.tlsys.graph

import org.tlsys.node.Block
import org.tlsys.node.Edge
import java.util.*

/*
class FullTree(entry: Block) {
    //val from = HashMap<Block, Edge>()
    //val to = HashMap<Block, Edge>()
    //val edigs = HashSet<Edge>()
    val blocks = HashSet<Block>()

    init {
        fun r(b: Block) {
            if (b in blocks)
                return
            blocks += b
            for (g in b.outEdge) {
                //from.putIfAbsent(b, g)
                //to.putIfAbsent(g.to!!, g)
                //edigs += g
                r(g.to!!)
            }
        }
        r(entry)
    }
}
*/

val names = HashMap<Block, String>()
/*
var Block.mark: Boolean
    get() = this in marks
    set(it) {
        if (it)
            marks += this
        else
            marks -= this
    }
*/



fun buildDominationTree(entry: Block) {
    val sidoms_id = HashMap<Block, Int>()

    fun Block.id() = sidoms_id[this] ?: 0

    fun Block.id(index: Int) {
        sidoms_id.put(this, index)
    }

    val marks2 = HashSet<Edge>()

    fun Edge.mark(): Boolean = this in marks2

    fun Edge.mark(it: Boolean) {
        if (it)
            marks2 += this
        else
            marks2 -= this
    }

    fun doNumirate(block: Block) {
        if (sidoms_id.containsKey(block))
            return
        block.id(sidoms_id.size + 1)
        //block.description += " [${sidoms_id.size}]"
        for (e in block.outEdge)
            doNumirate(e.to!!)
    }
    doNumirate(entry)


    val marks = HashSet<Block>(sidoms_id.size)

    fun isMark(it: Block): Boolean {
        return it in marks
    }

    fun setMark(it: Block) {
        marks += it
    }


    fun FindSemi(it: Block, node: Block) {
        // делаем пометки что бы не зацыклиться
        if (isMark(it)) return; setMark(it);
        // если номер вершины меньше нашей, то дорогу через неё не прокладываем, но коректируем семидоминатор, если надо
        if (it.id() < node.id()) {
            if (node.dominator === null || it.id() < node.dominator!!.id())
                node.dominator = it;
            return;
        }
        // продолжаем искать дорогу через большие вершины
        for (l in it.inEdge)
            FindSemi(l.from!!, node);

    }


    /**
     * находим сами доминаторы, рекурсивная часть
     *
     * @param from откуда ищем дорогу
     * @param to куда надо найти дорогу
     * @returntrue если есть дорого вдоль дерева
     */
    fun FindDomi(from: Block, to: Block): Boolean {
        var res = false;
        if (from === to) return true; // вершины совпали, значит весь путь найден

        for (l in from.outEdge)
            if (l.mark() && FindDomi(l.to!!, to)) {
                // если путь был найден коректируем семидоминатор
                res = true;
                if (l.to!!.dominator!!.id() < to.dominator!!.id())
                    to.dominator = l.to!!.dominator;
            }


        return res;
    }

    //поиск сидоминаторов
    for (n in sidoms_id.keys) {
        marks.clear()
        FindSemi(n, n);
    }

    //поиск доминаторов
    for (n in sidoms_id.keys) {
        if (n.dominator === null) {
            n.dominator = n
            continue
        }
        FindDomi(n.dominator!!, n)
    }
}

/*
class DominationTest {
    @Test
    fun show() {
        val method = JMethod()

        fun Block(from: Block, name: String): Block {
            val test = Block(method, Block.Companion.LEVEL_PARENT_MIN)
            SimpleEdge(from, test, "")
            test.description = name
            names.put(test, name)
            return test
        }


        val R = Block(method, Block.Companion.LEVEL_PARENT_MIN)
        R.description = "R"
        names.put(R, "R")


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
        Viwer.show("Domination tree", R)
    }
}
*/