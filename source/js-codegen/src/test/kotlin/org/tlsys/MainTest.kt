package org.tlsys

import org.junit.Test
import org.objectweb.asm.*
import org.tlsys.edge.*
import org.tlsys.level2.Optimazer
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
        val cr = ClassReader(AAA::class.java.name)
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
    override fun visitMethod(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor? {
        val m = MethodV()
        program += m.program
        return m
    }
}

class MethodV : MethodVisitor(org.objectweb.asm.Opcodes.ASM5) {

    val program = Program()

    var currentBlock = program.createBlock("entry block")

    val labelRef = hashMapOf<Label, LabelNode>()
    val labelBlock = hashMapOf<Label, BaseBlock>()

    fun blockForLabel(l: Label): BaseBlock {
        val g = labelBlock[l]
        if (g != null)
            return g;

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
            currentBlock.steck.push(VarValue(index))
            return
        }

        if (opcode == Opcodes.ISTORE) {
            val o = SetVar(index, currentBlock.steck.pop())
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

    override fun visitJumpInsn(opcode: Int, label: Label?) {
        label!!.id
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

            currentBlock = nextBlock

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

            currentBlock = nextBlock

            return
        }
        if (opcode == Opcodes.IF_ICMPLE) {

            val value2 = currentBlock.steck.pop()
            val value1 = currentBlock.steck.pop()


            val forJump = blockForLabel(label!!)
            val nextBlock = program.createBlock("Next after if")

            val exp = ConditionExp(value1, value2, ConditionType.IFLE)
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

        if (opcode == Opcodes.IF_ICMPGT) {

            val value2 = currentBlock.steck.pop()
            val value1 = currentBlock.steck.pop()

            val forJump = blockForLabel(label!!)
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


        if (opcode == Opcodes.GOTO) {
            val forJump = blockForLabel(label!!)
            val e = SimpleEdge(currentBlock, forJump)
            currentBlock.outEdge += e
            forJump.inEdge += e
            forJump.steck.marge(currentBlock.steck)

            val next = program.createBlock("next after jump $e")
            next.steck.marge(currentBlock.steck)
            currentBlock = next
/*
            waitLabel {l,it->
                println("NEXT ${l.id}")
            }
            */
            return
        }

        TODO("opcode=$opcode")
    }

    override fun visitLdcInsn(cst: Any?) {
        currentBlock.steck.push(LdcValue(cst))
        currentBlock += PushLdc(cst!!)
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

    override fun visitTypeInsn(opcode: Int, type: String?) {
        if (opcode == Opcodes.CHECKCAST)
            return
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
                currentBlock = b
            } else {
                val h = SimpleEdge(currentBlock, b)
                currentBlock.outEdge += h
                b.inEdge += h
                b.steck.marge(currentBlock.steck)
                currentBlock = b
            }
        }

        val l = LabelNode(label!!)
        labelRef.put(label!!, l)
        currentBlock += l
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
                currentBlock+=v
            } else {
                currentBlock.steck.push(v)
            }
            return
        }

        if (opcode == Opcodes.INVOKESPECIAL) {
            val v = StaticSpecial(currentBlock.steck.pop(), owner!!, name!!, desc, args.toTypedArray())

            if (params.ret == Primitive.get('V')) {
                currentBlock+=v
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

    override fun visitLocalVariable(name: String?, desc: String?, signature: String?, start: Label?, end: Label?, index: Int) {
        super.visitLocalVariable(name, desc, signature, start, end, index)
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