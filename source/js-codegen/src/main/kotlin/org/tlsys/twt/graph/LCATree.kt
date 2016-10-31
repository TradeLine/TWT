package org.tlsys.twt.graph

import org.tlsys.twt.node.Block
import java.util.*


/**
 * @author Иванов Максим (e-maxx@inbox.ru), Субочев Антон (caffeine.mgn@gmail.com)
 */
class LCATree(root: Block) {
    class Graph : ArrayList<ArrayList<Int>>()

    private val indexes_1 = HashMap<Block, Int>()
    private val indexes_2 = HashMap<Int, Block>()

    private val lca_h = ArrayList<Int>()
    private val lca_dfs_list = ArrayList<Int>()
    private val lca_first = ArrayList<Int>()
    private val lca_tree = ArrayList<Int>()
    private val lca_dfs_used = ArrayList<Boolean>()

    val Block.index: Int
        get() {
            val b = indexes_1[this]
            if (b===null)
                TODO("Can't find index for ${this.ID}")
            return b
        }

    val Int.block: Block
        get() = indexes_2[this]!!

    init {
        val g = Graph()
        fun workBlock(b: Block): Int {
            var id = indexes_1[b]
            if (id !== null)
                return id

            id = g.size
            val out = ArrayList<Int>(b.outEdge.size)
            g.add(out)

            indexes_1[b] = id
            indexes_2[id] = b


            for (i in b.outEdge) {
                out.add(workBlock(i.to!!))
            }
            return id
        }
        workBlock(root)
        //lca_h.resize(g.size);
        lca_h.ensureCapacity(g.size)
        for (i in 1..g.size)
            lca_h.add(0)
        lca_prepare(g, 0)
    }



    private fun lca_dfs(g: Graph, v: Int, h: Int = 1) {
        lca_dfs_used[v] = true;
        lca_h[v] = h;
        lca_dfs_list.add(v);
        for (i in g[v]) {
            if (!lca_dfs_used[i]) {
                lca_dfs(g, i, h + 1);
                lca_dfs_list.add(v);
            }
        }
    }

    private fun lca_build_tree(i: Int, l: Int, r: Int) {
        if (l == r)
            lca_tree[i] = lca_dfs_list[l];
        else {
            val m = (l + r) shr 1;
            lca_build_tree(i + i, l, m);
            lca_build_tree(i + i + 1, m + 1, r);
            if (lca_h[lca_tree[i + i]] < lca_h[lca_tree[i + i + 1]])
                lca_tree[i] = lca_tree[i + i];
            else
                lca_tree[i] = lca_tree[i + i + 1];
        }
    }

    private fun <T> ArrayList<T>.assign(count: Int, value: T) {
        clear()
        ensureCapacity(count)
        for (i in 1..count) {
            add(value)
        }
    }

    private fun lca_prepare(g: Graph, root: Int) {
        val n = g.size

        lca_dfs_list.ensureCapacity(n * 2);
        lca_dfs_used.assign(n, false);

        lca_dfs(g, root);

        val m = lca_dfs_list.size;
        lca_tree.assign(lca_dfs_list.size * 4 + 1, -1);
        lca_build_tree(1, 0, m - 1);

        lca_first.assign(n, -1);
        for (i in 0..m - 1) {
            val v = lca_dfs_list[i];
            if (lca_first[v] == -1)
                lca_first[v] = i;
        }
    }

    private fun lca_tree_min(i: Int, sl: Int, sr: Int, l: Int, r: Int): Int {
        if (sl == l && sr == r)
            return lca_tree[i];
        val sm = (sl + sr) shr 1;
        if (r <= sm)
            return lca_tree_min(i + i, sl, sm, l, r);
        if (l > sm)
            return lca_tree_min(i + i + 1, sm + 1, sr, l, r);
        val ans1 = lca_tree_min(i + i, sl, sm, l, sm);
        val ans2 = lca_tree_min(i + i + 1, sm + 1, sr, sm + 1, r);
        return if (lca_h[ans1] < lca_h[ans2]) ans1 else ans2;
    }

    private fun lca(a: Int, b: Int): Int {
        var left = lca_first[a]
        var right = lca_first[b];
        if (left > right) {
            val c = left
            left = right
            right = c
        }
        return lca_tree_min(1, 0, lca_dfs_list.size - 1, left, right);
    }

    operator fun get(a: Block, b: Block): Block = lca(a.index, b.index).block
}