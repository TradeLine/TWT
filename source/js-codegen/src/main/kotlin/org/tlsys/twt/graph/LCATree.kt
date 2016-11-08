package org.tlsys.twt.graph

import org.tlsys.twt.node.Block
import org.tlsys.twt.node.Edge
import org.tlsys.twt.node.EdgeContener
import java.util.*
import java.util.logging.Logger


/**
 * @author Иванов Максим (e-maxx@inbox.ru), Субочев Антон (caffeine.mgn@gmail.com)
 */
class LCATree(root: Block, val drectionDown: Boolean = true) {

    val Edge.next: Block
        get() {
            if (drectionDown)
                return to!!
            else
                return from!!
        }

    val Block.edges: EdgeContener
        get() {
            if (drectionDown)
                return outEdge
            else
                return inEdge
        }

    private val indexes_1 = HashMap<Block, Int>()
    private val indexes_2 = HashMap<Int, Block>()

    private val height: IntArray// = ArrayList<Int>()
    private val first: IntArray// = ArrayList<Int>()
    private val order: IntArray// = ArrayList<Int>()

    val Block.index: Int
        get() {
            val b = indexes_1[this]
            if (b === null)
                TODO("Can't find index for ${this.description}")
            return b
        }

    val Int.block: Block
        get() = indexes_2[this]!!

    init {
        val g = ArrayList<ArrayList<Int>>()
        fun workBlock(b: Block): Int {
            var id = indexes_1[b]
            if (id !== null)
                return id

            id = g.size
            val out = ArrayList<Int>(b.edges.size)
            g.add(out)

            indexes_1[b] = id
            indexes_2[id] = b


            for (i in b.edges) {
                out.add(workBlock(i.next!!))
            }
            return id
        }
        workBlock(root)
        height = IntArray(g.size)


        //--------PREPARE--------//
        val n = g.size
        val lca_dfs_used = BooleanArray(n)
        val lca_dfs_list = IntArray(n * 2)

        order = IntArray(lca_dfs_list.size * 4 + 1)
        Arrays.fill(order, -1)

        var dfs_i = 0


        fun lca_build_tree(i: Int, l: Int, r: Int) {
            if (l == r)
                order[i] = lca_dfs_list[l];
            else {
                val m = (l + r) shr 1;
                lca_build_tree(i + i, l, m);
                lca_build_tree(i + i + 1, m + 1, r);
                if (height[order[i + i]] < height[order[i + i + 1]])
                    order[i] = order[i + i];
                else
                    order[i] = order[i + i + 1];
            }
        }

        fun lca_dfs(g: ArrayList<ArrayList<Int>>, v: Int, h: Int = 1) {
            lca_dfs_used[v] = true;
            height[v] = h;
            lca_dfs_list[dfs_i++] = v;
            for (i in g[v]) {
                if (!lca_dfs_used[i]) {
                    lca_dfs(g, i, h + 1);
                    lca_dfs_list[dfs_i++] = v;
                }
            }
        }




        lca_dfs(g, 0);
        val m = lca_dfs_list.size;

        lca_build_tree(1, 0, m - 1);

        first = IntArray(n)//.assign(n, -1);
        Arrays.fill(first, -1)

        for (i in 0..m - 1) {
            val v = lca_dfs_list[i];
            if (first[v] == -1)
                first[v] = i;
        }
    }


    private fun <T> ArrayList<T>.assign(count: Int, value: T) {
        clear()
        ensureCapacity(count)
        for (i in 1..count) {
            add(value)
        }
    }

    private val LOG = Logger.getLogger(javaClass.name)

    private fun lca_tree_min(i: Int, search_left: Int, search_right: Int, left: Int, right: Int): Int {
        if (search_left == left && search_right == right)
            return order[i];
        val sm = (search_left + search_right) shr 1;//среднее между зонами поиска
        if (right <= sm)
            return lca_tree_min(i + i, search_left, sm, left, right);
        if (left > sm)
            return lca_tree_min(i + i + 1, sm + 1, search_right, left, right);
        val ans1 = lca_tree_min(i + i, search_left, sm, left, sm);
        val ans2 = lca_tree_min(i + i + 1, sm + 1, search_right, sm + 1, right);
        return if (height[ans1] < height[ans2]) ans1 else ans2;
    }

    private fun lca(a: Int, b: Int): Int {
        var left = first[a]
        var right = first[b];
        if (left > right) {
            val c = left
            left = right
            right = c
        }
        return lca_tree_min(1, 0, first.size * 2 - 1, left, right);
    }

    operator fun get(a: Block, b: Block): Block {
        val index = lca(a.index, b.index)
        return index.block
    }

    infix operator fun contains(block: Block): Boolean = block in indexes_1
}