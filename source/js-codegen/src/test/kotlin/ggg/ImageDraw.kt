package ggg

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Stroke
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.imageio.ImageIO

object ImageDraw {

    class Vec2D(val X: Double, val Y: Double) {
        operator fun plus(v: ImageDraw.Vec2D): Vec2D = Vec2D(X + v.X, Y + v.Y)
        operator fun div(i: Int):Vec2D = Vec2D(X/i.toDouble(), Y/i.toDouble())
        operator fun div(i: Double):Vec2D = Vec2D(X/i, Y/i)
    }

    class Cursor(var Y: Double = 0.0) {
        val stec = LinkedList<Double>()
        infix operator fun plusAssign(height: Double) {
            Y += height
        }
    }

    fun draw(b: Block) {
        val i = BufferedImage(1200, 1300, BufferedImage.TYPE_INT_RGB)
        val g = i.createGraphics()!!
        g.color = Color.RED

        g.drawLine(0, 0, 300, 300)

        val drawedBlocks = HashMap<Block, Vec2D>()
        val cursor = Cursor(20.0);

        b.draw(g, 20.0, drawedBlocks, cursor, null)
        ImageIO.write(i, "jpg", File("Test.jpg"));
    }
}

fun String.size(g: Graphics2D) = g.getFontMetrics(g.font).getStringBounds(this, g)!!

fun Statement.size(g: Graphics2D) = toString().size(g)
fun Statement.draw(g: Graphics2D, x: Double, y: Double) {
    g.drawString(toString(), x.toInt(), y.toInt())
}

fun Block.size(g: Graphics2D): Rectangle2D {
    var widthMax = ID.toString().size(g).width + 20
    var height = ID.toString().size(g).height
    var o = first
    while (o != null) {
        val f = o.toString().size(g)
        if (f.width + 20 > widthMax)
            widthMax = f.width + 20
        height += f.height
        o = o.next
    }

    return Rectangle2D.Double(0.0, 0.0, widthMax, height + 10)
}

fun Block.draw(g: Graphics2D, x: Double, drowed: HashMap<Block, ImageDraw.Vec2D>, cur: ImageDraw.Cursor, edge: Edge?): Boolean {
    val size = size(g)
    val old = drowed[this]
    if (old !== null) {


        return false
    }
    drowed.put(this, ImageDraw.Vec2D(x, cur.Y))
    g.setStroke(BasicStroke(1f));
    val startY = cur.Y
    g.color = Color.WHITE
    g.fillRect(x, cur.Y, size.width, size.height)
    g.color = Color.BLACK
    cur += ID.toString().size(g).height
    g.drawString(ID.toString(), x + (size.width / 2.0 - ID.toString().size(g).width / 2.0), cur.Y);

    var o = first
    while (o != null) {
        cur += o.size(g).height
        o.draw(g, x + 10, cur.Y)
        o = o.next
    }

    val it = outEdge.iterator()

    val endy = cur.Y + 10

    cur += 50.0

    var paddingX = x + 50

    val it2 = inEdge.iterator()

    while (it.hasNext()) {
        val e = it.next()
        val block = e.to!!

        val oldPos = drowed[block]
        val from: ImageDraw.Vec2D
        val to: ImageDraw.Vec2D
        if (oldPos !== null) {
            from = ImageDraw.Vec2D(x + size.width, endy)
            to = ImageDraw.Vec2D(oldPos.X + block.size(g).width, oldPos.Y)
        } else {
            from = ImageDraw.Vec2D(x + size.width, endy)
            to = ImageDraw.Vec2D(paddingX + block.size(g).width, cur.Y)
            block.draw(g = g, x = paddingX, cur = cur, drowed = drowed, edge = e)
            paddingX += block.size(g).width + 50.0
        }
        g.setStroke(BasicStroke(1f));
        g.color = Color.RED
        g.drawLine(from, to)

        val center = (from + to) / 2
        g.color = Color.YELLOW
        g.drawString(e.toString(), center.X, center.Y)
    }
    return true
}



fun Graphics2D.drawLine(v1: ImageDraw.Vec2D, v2: ImageDraw.Vec2D) {
    drawLine(v1.X, v1.Y, v2.X, v2.Y)
}


fun Graphics2D.drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
    drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
}

fun Graphics2D.drawString(text: String, x: Double, y: Double) {
    drawString(text, x.toInt(), y.toInt())
}

fun Graphics2D.fillRect(x: Double, y: Double, width: Double, height: Double) {
    fillRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
}