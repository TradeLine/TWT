package org.tlsys

import org.junit.Test
import org.objectweb.asm.*
import org.tlsys.edge.*
import org.tlsys.level2.Optimazer
import org.tlsys.level2.TernarOp
import org.tlsys.node.*
import java.util.*

var labelIdIterator: Int = 0
val labelCodes = hashMapOf<Label, Int>()

val Label.id: Int get() {
    var g = labelCodes[this]
    if (g == null) {
        var id = labelIdIterator++
        labelCodes.put(this, id)
        return id
    }
    return g
}

class MainTest {
    @Test
    fun test() {
        val cr = ClassReader(BBB::class.java.name)
        val v = ClassV()
        cr.accept(v, 0)
        for (p in v.program) {
            Optimazer.optimaze(p.entryBlock!!)
            drawPragma(p)
        }
    }

    fun drawPragma(p: Program) {
        for (b in p.blocks) {
            println("\n====${b.ID}===${b.rigen}")
            drawBlock(b)
            println("===============\n")
        }
    }

    fun drawBlock(b: BaseBlock) {
        if (!b.inEdge.isEmpty()) {
            println("IN:")
            val l = b.inEdge.toList()
            for (i in 0..l.size - 1)
                println("${i + 1}. ${l[i]._from()}")
        }

        if (!b.isEmpty()) {
            println("-----------------")
            for (i in 0..b.size - 1)
                println("${i + 1} ${b[i]}")
            println("-----------------")
        }

        if (!b.steck.isEmpty()) {
            println("Steck:")
            val l = b.steck.toList()
            for (i in 0..l.size - 1)
                println("${i + 1}. ${l[i].value}, isNew=${!l[i].marged}")
            println()
        }

        if (!b.outEdge.isEmpty()) {
            println("OUT:")
            val l = b.outEdge.toList()
            for (i in 0..l.size - 1)
                println("${i + 1}. ${l[i]._to()}")
        }
    }
}

class ClassV : ClassVisitor(Opcodes.ASM5) {
    val program = ArrayList<Program>()
    override fun visitMethod(access: Int, name: String?, desc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor? {
        val m = MethodV()
        program += m.program
        if (access and Opcodes.ACC_STATIC == 0)
            m.program.createNamedVar(0).set(ThisRef(), m.program.entryBlock!!)

        val g = SReader.parse(desc)
        for (i in 1..g.params.size)
            m.program.createNamedVar(i).set(ThisRef(), m.program.entryBlock!!)
        return m
    }
}

class MethodV : MethodVisitor(org.objectweb.asm.Opcodes.ASM5) {

    val program = Program()

    var _currentBlock = program.createBlock("entry block")
    val currentBlock: BaseBlock
        get() = _currentBlock

    val lavelBlocks = HashMap<Label, BaseBlock>()

    fun changeCurrentBlock(block: BaseBlock) {
        val g = onClose[_currentBlock]
        if (g !== null) {
            g(ChangeBlock(from = _currentBlock, to = block))
            onClose.remove(_currentBlock)
        }
        val old = _currentBlock
        _currentBlock = block

        val h = onStart[block]
        if (h !== null) {
            h(ChangeBlock(from = old, to = block))
            onStart.remove(block)
        }

        println("Change from ${old.rigen} => ${block.rigen}")
    }

    val labelRef = hashMapOf<Label, LabelNode>()
    val labelBlock = hashMapOf<Label, BaseBlock>()

    class ChangeBlock(val from: BaseBlock, val to: BaseBlock)

    val onClose = HashMap<BaseBlock, (ChangeBlock) -> Unit>()
    val onStart = HashMap<BaseBlock, (ChangeBlock) -> Unit>()

    class ConnectRecord(val b1: BaseBlock, val b2: BaseBlock)

    val onConnect = HashMap<ConnectRecord, BaseBlock.(BaseBlock) -> Unit>()

    fun BaseBlock.connect(blocK: BaseBlock, l: BaseBlock.(BaseBlock) -> Unit) {
        onConnect.put(ConnectRecord(this, blocK), l)
    }

    fun BaseBlock.end(l: (ChangeBlock) -> Unit) {
        onClose.put(this, l)
    }

    fun BaseBlock.start(l: (ChangeBlock) -> Unit) {
        onStart.put(this, l)
    }

    fun blockForLabel(l: Label): BaseBlock {

        if (lavelBlocks.containsKey(l)) {
            val block = lavelBlocks[l]!!
            val firstOp = block.operationIterator().next()
            if (firstOp is LabelNode && firstOp.point===l)
                return block
            else {
                val blocks = block.split(l)
                return blocks.second
            }

        }

        val g = labelBlock[l]
        if (g != null)
            return g

        var h = BaseBlock(program, "for label ${l.id}");
        program.blocks += h
        labelBlock.put(l, h)
        return h
    }

    override fun visitMultiANewArrayInsn(desc: String?, dims: Int) {
        TODO()
    }

    override fun visitFrame(type: Int, nLocal: Int, local: Array<out Any>?, nStack: Int, stack: Array<out Any>?) {
        //TODO()
    }

    override fun visitVarInsn(opcode: Int, index: Int) {
        if (opcode == Opcodes.ALOAD || opcode == Opcodes.ILOAD) {
            //val o = GetVar(currentBlock, index)
            //currentBlock += o
            val _var = program.getVar(index)
            currentBlock.steck.push(_var.getValueForBlock(currentBlock).get())
            return
        }

        if (opcode == Opcodes.ISTORE) {
            val o = program.getVar(index).set(currentBlock.steck.pop(), currentBlock)
            currentBlock += o
            return
        }

        TODO()
    }

    override fun visitTryCatchBlock(start: Label?, end: Label?, handler: Label?, type: String?) {
        TODO()
    }

    override fun visitLookupSwitchInsn(dflt: Label?, keys: IntArray?, labels: Array<out Label>?) {
        TODO()
    }

    val margeAfterBlock = HashMap<BaseBlock, BaseBlock>()

    override fun visitJumpInsn(opcode: Int, label: Label?) {
        label!!.id

        fun buildIf(type: ConditionType) {
            val value2 = currentBlock.steck.pop()
            val value1 = currentBlock.steck.pop()


            val forJump = blockForLabel(label)
            val nextBlock = program.createBlock("Next after if")
            nextBlock.steck.copyFrom(currentBlock.steck)
            forJump.steck.copyFrom(currentBlock.steck)

            val exp = ConditionExp(value1, value2, type)

            val if_yes = ConditionEdge(currentBlock, forJump, exp)
            val if_no = ElseConditionEdge(if_yes, nextBlock)


            var vars: Array<Var>? = null
            if_no.to!!.end {
                vars = it.from.steck.convertToVar()
            }

            if_yes.to!!.end {
                if (vars === null)
                    println("Vars not ready!")
                if (if_no.to == it.to)//if-else
                    it.from.steck.convertToVar(vars!!)
            }

            if_no.to!!.connect(if_yes.to!!) {
                Optimazer.replaceConstInBlock(this)
                Optimazer.replaceConstInBlock(it)


                if (it.steck.addedSize == 1) {
                    println("pass1=${this.operationCount == 0 && it.operationCount == 0}")//у обоих нет операций в блоке
                    println("pass2=${this.inEdge.size == 1 && it.inEdge.size == 1}")//у обоих только один блок входит в них
                    println("pass3=${this.inEdge.iterator().next().from === it.inEdge.iterator().next().from}")//входящий в них обоих блок один и тот же
                    println("pass4=${this.outEdge.size == 1 && it.outEdge.size == 1}")//у обоих только один блок входит в них
                    println("pass5=${this.outEdge.iterator().next().to === it.outEdge.iterator().next().to}")//входящий в них обоих блок один и тот же
                    println("pass6=${this.steck.addedSize == 1 && it.steck.addedSize == 1}")
                    if (this.operationCount == 0 && it.operationCount == 0//у обоих нет операций в блоке
                            && this.inEdge.size == 1 && it.inEdge.size == 1//у обоих только один блок входит в них
                            && this.inEdge.iterator().next().from === it.inEdge.iterator().next().from//входящий в них обоих блок один и тот же
                            && this.outEdge.size == 1 && it.outEdge.size == 1//у обоих только один блок входит в них
                            && this.outEdge.iterator().next().to === it.outEdge.iterator().next().to//входящий в них обоих блок один и тот же
                            && this.steck.addedSize == 1 && it.steck.addedSize == 1//оба блока добавили только одно значение в стек
                            && this.operationCount == 0 && it.operationCount == 0) {//в блоках нет операций

                        val left = this.steck.getOne { !it.marged }
                        val right = it.steck.getOne { !it.marged }

                        val newBlock = BaseBlock(program, "Marged trenar operator")
                        program.blocks += newBlock
                        val it1 = it.steck.iterator()
                        while (it1.hasNext()) {
                            val g = it1.next()
                            if (g.marged)
                                newBlock.steck.push(g.value)
                            g.value.unsteck(it)
                            it1.remove()
                        }
                        this.steck.clear()
                        val t = if (this.inEdge.iterator().next() is ConditionEdge) TernarOp((this.inEdge.iterator().next() as ConditionEdge).value, left, right) else TernarOp((it.inEdge.iterator().next() as ConditionEdge).value, left, right)


                        SimpleEdge(this.inEdge.iterator().next().from!!, newBlock)
                        SimpleEdge(newBlock, this.outEdge.iterator().next().to!!)
                        newBlock.steck.push(t)
                        this.inEdge.iterator().next().free()
                        this.outEdge.iterator().next().free()

                        it.inEdge.iterator().next().free()
                        it.outEdge.iterator().next().free()
                        this.program -= this
                        this.program -= it

                    }
                }
                println("123")
            }


            changeCurrentBlock(nextBlock)
        }
        /*
        if (opcode == Opcodes.IFLE) {

            val value = currentBlock.steck.pop()

            val forJump = blockForLabel(label)
            val nextBlock = program.createBlock("Next after if")

            val exp = ConditionExp(value, IntValue(0), ConditionType.IFLE)
            val not_exp = ConditionNot(exp)

            val if_yes = ConditionEdge(currentBlock, forJump, exp)
            val if_no = ConditionEdge(currentBlock, nextBlock, not_exp)

            currentBlock.outEdge += if_yes
            forJump.inEdge += if_yes
            forJump.steck.marge(if_yes.from!!.steck)

            currentBlock.outEdge += if_no
            nextBlock.inEdge += if_no
            forJump.steck.marge(if_no.from!!.steck)

            changeCurrentBlock(nextBlock)

            return
        }


        if (opcode == Opcodes.IF_ICMPGE) {

            val value2 = currentBlock.steck.pop()
            val value1 = currentBlock.steck.pop()


            val forJump = blockForLabel(label!!)
            val nextBlock = program.createBlock("Next after if")

            val exp = ConditionExp(value1, value2, ConditionType.IFGE)
            //val not_exp = ConditionNot(currentBlock, exp)

            val if_yes = ConditionEdge(currentBlock, forJump, exp)
            val if_no = ElseConditionEdge(if_yes, nextBlock)

            currentBlock.outEdge += if_yes
            forJump.inEdge += if_yes
            forJump.steck.marge(if_yes.from!!.steck)

            currentBlock.outEdge += if_no
            nextBlock.inEdge += if_no
            forJump.steck.marge(if_no.from!!.steck)

            changeCurrentBlock(nextBlock)

            return
        }
        */
        if (opcode == Opcodes.IF_ICMPLE) {
            buildIf(ConditionType.IFLE)
            return
        }
        if (opcode == Opcodes.IF_ICMPGT) {
            buildIf(ConditionType.IFGT)
            return
        }
/*
        if (opcode == Opcodes.IF_ICMPGT) {

            val value2 = currentBlock.steck.pop()
            val value1 = currentBlock.steck.pop()

            val forJump = blockForLabel(label)
            val nextBlock = program.createBlock("Next after if")

            val exp = ConditionExp(value1, value2, ConditionType.IFGT)
            //val not_exp = ConditionNot(currentBlock, exp)

            val if_yes = ConditionEdge(currentBlock, forJump, exp)
            val if_no = ElseConditionEdge(if_yes, nextBlock)

            currentBlock.outEdge += if_yes
            forJump.inEdge += if_yes
            forJump.steck.marge(if_yes.from!!.steck)

            currentBlock.outEdge += if_no
            nextBlock.inEdge += if_no
            forJump.steck.marge(if_no.fromEdge.from!!.steck)

            currentBlock = nextBlock

            return
        }
*/

        if (opcode == Opcodes.GOTO) {
            val forJump = blockForLabel(label!!)
            val e = SimpleEdge(currentBlock, forJump)
            currentBlock.outEdge += e
            forJump.inEdge += e
            //forJump.steck.marge(currentBlock.steck)

            val next = program.createBlock("next after jump $e")
            //next.steck.copyFrom(currentBlock.steck)
            changeCurrentBlock(next)
            return
        }

        TODO("opcode=$opcode")
    }

    override fun visitLdcInsn(cst: Any?) {
        val t = program.createTempVar()
        val s = t.set(LdcValue(cst), currentBlock)
        currentBlock += s
        currentBlock.steck.push(s.item.get())
    }

    override fun visitIntInsn(opcode: Int, operand: Int) {
        if (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) {
            //val p = PushInt(currentBlock, operand)
            //currentBlock += p
            currentBlock.steck.push(IntValue(operand))
            return
        }
        TODO("$opcode")
    }

    override fun visitTypeInsn(opcode: Int, type: String) {
        if (opcode == Opcodes.CHECKCAST)
            return
        if (opcode == Opcodes.NEW) {
            val t = currentBlock.program.createTempVar()
            //val id = currentBlock.program.getTempId()
            val op = t.set(New(ClassRef.get(type)), currentBlock)
            currentBlock += op
            currentBlock.steck.push(op.item.get())
            return
        }
        TODO()
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack, maxLocals)
    }

    override fun visitInvokeDynamicInsn(name: String?, desc: String?, bsm: Handle?, vararg bsmArgs: Any?) {
        TODO()
    }

    override fun visitLabel(label: Label?) {
        label!!.id
        //println("LABEL ${label!!.id} to ${currentBlock.ID}")
        val b = labelBlock[label]
        if (b != null) {
            if (currentBlock.isEmpty()) {
                currentBlock.giveEdgeTo(b)
                program.blocks -= currentBlock
                //b.steck.marge(currentBlock.steck)
                changeCurrentBlock(b)
            } else {
                val h = SimpleEdge(currentBlock, b)
                currentBlock.outEdge += h
                b.inEdge += h
                changeCurrentBlock(b)
                //b.steck.marge(currentBlock.steck)

                if (b.inEdge.size == 2) {
                    val it = b.inEdge.iterator()
                    val b1 = it.next().from!!
                    val b2 = it.next().from!!
                    for (g in onConnect.entries) {
                        if (g.key.b1 === b1) {
                            g.value.invoke(b1, b2)
                            onConnect.remove(g.key)
                            break
                        }
                        if (g.key.b1 === b2) {
                            g.value.invoke(b2, b1)
                            onConnect.remove(g.key)
                            break
                        }
                    }

                }

                if (b.inEdge.size > 1) {
                    //Optimazer.optimaze(program.entryBlock!!, b)

                    /*
                    val itt1 = b.inEdge.iterator()

                    val pathes = Array(b.inEdge.size) {
                        itt1.next().from!!.getPathLengthTo_UP(program.entryBlock!!)!! as Path
                    }
                    val startBranch = Path.findDominator_end_start(pathes)
                    println("start = $startBranch")
                    */

                    val itt = b.inEdge.iterator()
                    val values = ValueSteck.getMarged(*Array(b.inEdge.size) {
                        itt.next().from!!.steck
                    })
                    if (values.isNotEmpty())
                        for (i in values.size - 1..0) {
                            b.steck.push(values[i])
                        }
                } else {
                    currentBlock.steck.copyFrom(currentBlock.inEdge.iterator().next().from!!.steck)
                }

            }
        }

        val l = LabelNode(label)
        labelRef.put(label, l)
        currentBlock += l
        lavelBlocks.put(label, currentBlock)
    }

    override fun visitTryCatchAnnotation(typeRef: Int, typePath: TypePath?, desc: String?, visible: Boolean): AnnotationVisitor? {
        TODO()
    }

    override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, desc: String?, itf: Boolean) {
        val params = SReader.parse(desc!!)

        val args = LinkedList<Expression>()

        for (i in 1..params.params.size) {
            args.addFirst(currentBlock.steck.pop())
        }

        if (opcode == Opcodes.INVOKESTATIC) {
            val v = StaticInvoke(owner!!, name!!, desc, args.toTypedArray())

            if (params.ret == Primitive.get('V')) {
                currentBlock += v
            } else {
                val id = currentBlock.program.createTempVar()
                val op = id.set(v, currentBlock)
                currentBlock += op

                currentBlock.steck.push(op.item.get())
            }
            return
        }

        if (opcode == Opcodes.INVOKESPECIAL) {
            val v = StaticSpecial(currentBlock.steck.pop(), ClassRef.get(owner!!), name!!, desc, args.toTypedArray())

            if (params.ret == Primitive.get('V')) {
                currentBlock += v
            } else {
                currentBlock.steck.push(v)
            }
            return
        }


        TODO()
    }

    override fun visitInsn(opcode: Int) {
        fun pushInt(i: Int) {
            val p = IntValue(i)
            currentBlock.steck.push(p)
        }
        if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) {
            pushInt(opcode - Opcodes.ICONST_0)
            return
        }
        if (opcode == Opcodes.RETURN) {
            currentBlock += Return()
            return
        }

        if (BinarMathOp.Types.isBinarOp(opcode)) {
            val v2 = currentBlock.steck.pop()
            val v1 = currentBlock.steck.pop()
            currentBlock.steck.push(BinarMathOp(v1, v2, BinarMathOp.Types.fromCode(opcode)))
            return
        }
        if (opcode == Opcodes.DUP) {
            val g = currentBlock.steck.peek()
            val t = currentBlock.program.createTempVar()
            val op = t.set(g, currentBlock)
            currentBlock += op
            currentBlock.steck.push(op.item.get())
            return
        }
        if (opcode == Opcodes.POP) {
            currentBlock.steck.pop()
            return
        }
        if (opcode == Opcodes.ATHROW) {
            currentBlock += Throw(currentBlock.steck.pop())
            return
        }
        TODO("$opcode")
    }

    override fun visitInsnAnnotation(typeRef: Int, typePath: TypePath?, desc: String?, visible: Boolean): AnnotationVisitor? {
        return super.visitInsnAnnotation(typeRef, typePath, desc, visible)
    }

    override fun visitParameterAnnotation(parameter: Int, desc: String?, visible: Boolean): AnnotationVisitor? {
        return super.visitParameterAnnotation(parameter, desc, visible)
    }

    override fun visitIincInsn(`var`: Int, increment: Int) {
        TODO()
    }

    override fun visitTableSwitchInsn(min: Int, max: Int, dflt: Label?, vararg labels: Label?) {
        TODO()
    }

    override fun visitLocalVariable(name: String, desc: String, signature: String?, start: Label?, end: Label?, index: Int) {
        program.getVar(index).name = name
        //super.visitLocalVariable(name, desc, signature, start, end, index)
    }

    override fun visitParameter(name: String?, access: Int) {
        super.visitParameter(name, access)
    }

    override fun visitAttribute(attr: Attribute?) {
        super.visitAttribute(attr)
    }

    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, desc: String?) {
        TODO()
    }
}