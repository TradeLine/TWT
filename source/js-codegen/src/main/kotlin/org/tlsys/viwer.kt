package org.tlsys

import org.tlsys.node.Block
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.*
import java.awt.geom.Rectangle2D
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import javax.swing.*

class Props(val fileName: String) {
    val file = File(fileName)
    val p = Properties()

    init {
        if (file.isFile) {
            FileInputStream(file).use {
                p.load(it)
            }


        }
    }

    fun set(name: String, value: String) {
        p.setProperty(name, value)
        FileOutputStream(file).use {
            p.store(it, null)
        }
    }

    fun set(name: String, value: Int) {
        set(name, value.toString())
    }

    fun set(name: String, value: Double) {
        set(name, value.toString())
    }

    fun get(name: String, def: String) = p.getProperty(name) ?: def
    fun getd(name: String, def: Double = 0.0) = get(name, def.toString()).toDouble()
    fun geti(name: String, def: Int = 0) = get(name, def.toString()).toInt()
}

object Viwer {
    fun show(title: String, entry: Block) {
        //Create and set up the window.
        //javax.swing.SwingUtilities.invokeLater { buildWindow() }
        buildWindow(title, entry)
        println("Window closed")
    }

    private fun buildWindow(title: String, entry: Block) {
        val frame = Window(title, entry)
        frame.isVisible = true
    }


    class Window(title: String, val entry: Block) : JDialog() {
        val p = Props("temp.properties")
        val map = HashMap<Block, DBlock>()
        val stage = Stage(this)

        fun create(b: Block) {
            if (map.containsKey(b))
                return
            val d = DBlock(b, this, stage)
            d.pos = ImageDraw.Vec2D(p.getd("b${b.ID}X"), p.getd("b${b.ID}Y"))
            map.put(b, d)

            for (g in b.outEdge) {
                create(g.to!!)
            }
        }

        fun getFor(x: Int, y: Int): DBlock? {
            for (g in map.values) {
                if (g.isIn(x.toDouble(), y.toDouble()))
                    return g
            }
            return null
        }

        init {
            create(entry)
            defaultCloseOperation = DISPOSE_ON_CLOSE
            setSize(p.geti("width", 400), p.geti("height", 400))
            add(stage)

            this.title = title
            modalityType = ModalityType.APPLICATION_MODAL

            addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent) {
                    p.set("width", e.component.width)
                    p.set("height", e.component.height)
                }
            })
        }

        override fun dispose() {
            super.dispose()
        }
    }

    class DBlock(val block: Block, val parent: Window, val stage: Stage) {
        private var _pos = ImageDraw.Vec2D(0.0, 0.0)
        var pos: ImageDraw.Vec2D
            get() = _pos
            set(it) {
                _pos = it
                parent.p.set("b${block.ID}X", it.X)
                parent.p.set("b${block.ID}Y", it.Y)
            }


        lateinit var size: Rectangle2D

        fun isIn(x: Double, y: Double) = (
                x - stage.pos.X > pos.X &&
                        y - stage.pos.Y >= pos.Y &&

                        x - stage.pos.X <= pos.X + size.width &&
                        y - stage.pos.Y <= pos.Y + size.height)

        fun drawEdigs(g: Graphics2D) {
            val x = pos.X + stage.pos.X
            val y = pos.Y + stage.pos.Y

            val it = block.outEdge.iterator()
            while (it.hasNext()) {
                val e = it.next()
                val block = e.to!!

                val oldPos = parent.map[block]!!
                val from: ImageDraw.Vec2D
                val to: ImageDraw.Vec2D
                from = ImageDraw.Vec2D(x + size.width / 2.0, y + size.height)
                to = ImageDraw.Vec2D(oldPos.pos.X + stage.pos.X + block.size(g).width / 2.0, oldPos.pos.Y + stage.pos.Y)
                g.stroke = BasicStroke(1f)
                g.color = Color.RED
                g.drawLine(from, to)

                val center = (from + to) / 2
                g.color = Color.YELLOW
                g.drawString(e.toString(), center.X, center.Y)
            }
        }

        fun draw(g: Graphics2D): Boolean {
            size = block.size(g)
            g.stroke = BasicStroke(1f)
            val x = pos.X + stage.pos.X
            var y = pos.Y + stage.pos.Y
            g.color = Color.WHITE
            g.fillRect(x, y, size.width, size.height)
            g.color = Color.BLACK
            y += block.title.size(g).height
            g.drawString(block.title, x + (size.width / 2.0 - block.title.size(g).width / 2.0), y)

            var o = block.first
            while (o != null) {
                y += o.size(g).height
                o.draw(g, x + 10, y)
                o = o.next
            }
            return true
        }
    }

    class Stage(val parent: Window) : JPanel() {
        var mouseDown = false
        var oldX = 0
        var oldY = 0
        var selectedBlock: DBlock? = null

        private var _pos = ImageDraw.Vec2D(parent.p.getd("viewX"), parent.p.getd("viewY"))
        var pos: ImageDraw.Vec2D
            get() = _pos
            set(it) {
                parent.p.set("viewX", it.X)
                parent.p.set("viewY", it.Y)
                _pos = it
            }

        init {

            addMouseMotionListener(object : MouseMotionListener {
                override fun mouseMoved(e: MouseEvent) {
                    oldX = e.x
                    oldY = e.y
                }

                override fun mouseDragged(e: MouseEvent) {
                    if (e.modifiers == 16) {
                        if (selectedBlock !== null) {
                            selectedBlock!!.pos = ImageDraw.Vec2D(selectedBlock!!.pos.X + (e.x - oldX), selectedBlock!!.pos.Y + (e.y - oldY))
                            repaint()
                        }
                    }
                    if (e.modifiers == 8) {
                        pos = ImageDraw.Vec2D(pos.X + (e.x - oldX), pos.Y + (e.y - oldY))
                        repaint()
                    }
                    oldX = e.x
                    oldY = e.y
                }

            })

            addMouseListener(object : MouseListener {
                override fun mouseEntered(e: MouseEvent?) {
                }

                override fun mouseClicked(e: MouseEvent?) {
                }

                override fun mouseReleased(e: MouseEvent?) {
                    mouseDown = false
                }

                override fun mouseExited(e: MouseEvent?) {
                    mouseReleased(e)
                }

                override fun mousePressed(e: MouseEvent) {
                    mouseDown = true
                    selectedBlock = parent.getFor(e.x, e.y)
                }

            })
        }

        override fun paintComponent(g: Graphics) {
            g as Graphics2D
            g.color = Color(210, 210, 210)
            g.fillRect(0, 0, width - 1, height - 1)

            for (b in parent.map.values) {
                b.draw(g)
            }

            for (b in parent.map.values) {
                b.drawEdigs(g)
            }

            g.color = Color.RED
            g.drawRect(0, 0, width - 1, height - 1)
        }
    }
}