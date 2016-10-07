package ggg

import org.objectweb.asm.*
import org.tlsys.BaseBlock
import org.tlsys.ClassRef
import org.tlsys.Primitive
import org.tlsys.Var
import org.tlsys.node.ConditionType
import org.tlsys.node.SReader
import java.util.*

class MethodParser(val method: JMethod) : MethodVisitor(org.objectweb.asm.Opcodes.ASM5) {

    var current: Block = method.entryBlock

    val enterLabel = HashMap<Label, () -> Unit>()
    val visitedLabels = HashMap<Label, LabelSt>()

    fun Block.enter(label: Label, f: () -> Unit) {
        enterLabel.put(label, f)
    }

    data class BlockForLabel(val block: Block, val f: (Block) -> Unit)

    val blockForLabel = HashMap<Label, BlockForLabel>()

    fun blockOnLabel(label: Label, f: (Block) -> Unit): Block {
        val g = visitedLabels[label]
        if (g !== null) {
            val l = g.block!!.find<LabelSt> { it is LabelSt && it.label === label } ?: TODO()
            val newBlock = l.split()
            f(newBlock)
            return newBlock
        } else {
            val b = Block(method, Block.LEVEL_PARENT_MIN)
            blockForLabel.put(label, BlockForLabel(b, f))
            return b
        }
    }

    override fun visitMultiANewArrayInsn(p0: String?, p1: Int) {
        super.visitMultiANewArrayInsn(p0, p1)
    }


    override fun visitFrame(type: Int, nLocal: Int, local: Array<out Any>?, nStack: Int, stack: Array<out Any>?) {

        fun toFrameType(type: Int) =
                when (type) {
                    Opcodes.F_NEW -> "NEW"
                    Opcodes.F_FULL -> "FULL"
                    Opcodes.F_APPEND -> "APPEND"
                    Opcodes.F_CHOP -> "CHOP"
                    Opcodes.F_SAME -> "SAME"
                    Opcodes.F_SAME1 -> "SAME1"
                    else -> TODO()
                }


        //super.visitFrame(p0, p1, p2, p3, p4)

        val newBlck = Block(method, Block.LEVEL_PARENT_MIN)
        SimpleEdge(current, newBlck)
        current = newBlck


        current += StringValue("FRAME ${toFrameType(type)}, nLocal=$nLocal, local={${local?.joinToString(",")?:"NULL"}}, nStack=$nStack, stack={${stack?.joinToString(",")?:"NULL"}}")
    }

    override fun visitVarInsn(opcode: Int, index: Int) {

        when (opcode) {
            Opcodes.ILOAD,
            Opcodes.LLOAD,
            Opcodes.ALOAD -> {
                val v = method.getVar(index) ?:
                        TODO("Var $index not set")
                current += GetVar(current.last!!.findValueOfVar(v))
                return
            }

            Opcodes.ISTORE -> {
                val v = method.getVar(index) ?:
                        TODO("Var $index not set")
                val state = current.last!!.findValueOfVar(v).set(PopOne(v.type))
                current += SetVar(state)
                return
            }
        }
        super.visitVarInsn(opcode, index)
    }

    override fun visitTryCatchBlock(p0: Label?, p1: Label?, p2: Label?, p3: String?) {
        super.visitTryCatchBlock(p0, p1, p2, p3)
    }

    override fun visitLookupSwitchInsn(p0: Label?, p1: IntArray?, p2: Array<out Label>?) {
        super.visitLookupSwitchInsn(p0, p1, p2)
    }

    override fun visitJumpInsn(opcode: Int, label: Label) {
        when (opcode) {
            Opcodes.IF_ICMPLE -> {
                val state = method.createTemp(Primitive.get('Z')).first(ConditionExp(left = PopOne(Primitive.get('I')), right = PopOne(Primitive.get('I')), conType = ConditionType.fromOpcode(opcode)))
                current += SetVar(state)
                val oldBlock = current
                val noBlock = Block(method, Block.LEVEL_PARENT_MIN)//next block
                val yesBlock = blockOnLabel(label) {
                    if (it.isEmpty())
                        current = it
                    it.outEdge.copyFrom(noBlock.outEdge)
                }
                val yesEdge = ConditionEdge(from = oldBlock, to = yesBlock, value = GetVar(state))
                val noEdge = ElseConditionEdge(origenal = yesEdge, to = noBlock)
                current = noBlock
                return
            }
            Opcodes.GOTO -> {
                val afterJump = blockOnLabel(label) {
                    if (it.isEmpty())
                        current = it
                }
                SimpleEdge(current, afterJump)
                current = Block(method, Block.LEVEL_PARENT_MIN)
                return
            }
        }
        super.visitJumpInsn(opcode, label)
    }

    override fun visitLdcInsn(p0: Any?) {
        if (p0 is Int) {
            val exp = IntValue(p0)
            val v = method.createTemp(exp.type).first(exp)
            current += SetVar(v)
            current += GetVar(v)
            return
        }
        super.visitLdcInsn(p0)
    }

    override fun visitIntInsn(opcode: Int, operand: Int) {

        val exp = when (opcode) {
            Opcodes.BIPUSH -> {
                IntValue(operand)
            }
            Opcodes.SIPUSH -> {
                IntValue(operand)
            }
            else -> TODO()
        }
        val v = method.createTemp(exp.type).first(exp)
        current += SetVar(v)
        current += GetVar(v)

        //super.visitIntInsn(opcode, operand)
    }

    override fun visitTypeInsn(p0: Int, p1: String?) {
        super.visitTypeInsn(p0, p1)
    }

    override fun visitAnnotationDefault(): AnnotationVisitor {
        return super.visitAnnotationDefault()
    }

    override fun visitAnnotation(p0: String?, p1: Boolean): AnnotationVisitor {
        return super.visitAnnotation(p0, p1)
    }

    override fun visitTypeAnnotation(p0: Int, p1: TypePath?, p2: String?, p3: Boolean): AnnotationVisitor {
        return super.visitTypeAnnotation(p0, p1, p2, p3)
    }

    override fun visitMaxs(p0: Int, p1: Int) {
        super.visitMaxs(p0, p1)
    }

    override fun visitInvokeDynamicInsn(p0: String?, p1: String?, p2: Handle?, vararg p3: Any?) {
        super.visitInvokeDynamicInsn(p0, p1, p2, *p3)
    }

    override fun visitLabel(label: Label) {
        val f = enterLabel[label]
        if (f !== null) {
            f()
            enterLabel.remove(label)
        }
        val d = blockForLabel[label]

        if (d !== null) {
            d.f(d.block)
        }

        val l = LabelSt(label)
        visitedLabels.put(label, l)
        current += l
    }

    override fun visitTryCatchAnnotation(p0: Int, p1: TypePath?, p2: String?, p3: Boolean): AnnotationVisitor {
        return super.visitTryCatchAnnotation(p0, p1, p2, p3)
    }

    override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, desc: String?, itf: Boolean) {

        val params = SReader.parse(desc!!)

        val args = LinkedList<Expression>()

        for (i in 0..params.params.size - 1) {
            args.addFirst(PopOne(params.params[i]))
        }

        val inv = when (opcode) {
            Opcodes.INVOKESPECIAL -> {
                InvokeSpecial(self = PopOne(ClassRef.get(owner!!)), type = params.ret, methodName = name!!, arguments = args.toTypedArray())
            }

            Opcodes.INVOKESTATIC -> {
                InvokeStatic(type = params.ret, methodName = name!!, arguments = args.toTypedArray())
            }

            else -> TODO()
        }

        if (inv.type !== Primitive.get('V')) {
            val state = method.createTemp(inv.type).first(inv)
            current += SetVar(state)
            current += GetVar(state)
            return
        } else {
            current += inv
            return
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf)
    }

    override fun visitInsn(opcode: Int) {

        if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) {
            val state = method.createTemp(Primitive.get('I')).first(IntValue(opcode - Opcodes.ICONST_0))
            current += SetVar(state)
            current += GetVar(state)
            return
        }

        when (opcode) {
            Opcodes.POP -> {
                current += SkipOne()
                return
            }

            Opcodes.ISUB -> {
                val state = method.createTemp(Primitive.get('I')).first(
                        Math(left = PopOne(Primitive.get('I')), right = PopOne(Primitive.get('I')), mathType = Math.MathOp.SUB, type = Primitive.get('I'))
                )
                current += SetVar(state)
                current += GetVar(state)
                return
            }
            Opcodes.RETURN -> {
                current += Return()
                return
            }
        }


        super.visitInsn(opcode)
    }

    override fun visitInsnAnnotation(p0: Int, p1: TypePath?, p2: String?, p3: Boolean): AnnotationVisitor {
        return super.visitInsnAnnotation(p0, p1, p2, p3)
    }

    override fun visitParameterAnnotation(p0: Int, p1: String?, p2: Boolean): AnnotationVisitor {
        return super.visitParameterAnnotation(p0, p1, p2)
    }

    override fun visitIincInsn(p0: Int, p1: Int) {
        super.visitIincInsn(p0, p1)
    }

    override fun visitLineNumber(p0: Int, p1: Label?) {
        super.visitLineNumber(p0, p1)
    }

    override fun visitLocalVariableAnnotation(p0: Int, p1: TypePath?, p2: Array<out Label>?, p3: Array<out Label>?, p4: IntArray?, p5: String?, p6: Boolean): AnnotationVisitor {
        return super.visitLocalVariableAnnotation(p0, p1, p2, p3, p4, p5, p6)
    }

    override fun visitTableSwitchInsn(p0: Int, p1: Int, p2: Label?, vararg p3: Label?) {
        super.visitTableSwitchInsn(p0, p1, p2, *p3)
    }

    override fun visitEnd() {
        ImageDraw.draw(method.entryBlock)
        super.visitEnd()
    }

    override fun visitLocalVariable(p0: String?, p1: String?, p2: String?, p3: Label?, p4: Label?, p5: Int) {
        super.visitLocalVariable(p0, p1, p2, p3, p4, p5)
    }

    override fun visitParameter(p0: String?, p1: Int) {
        super.visitParameter(p0, p1)
    }

    override fun visitAttribute(p0: Attribute?) {
        super.visitAttribute(p0)
    }

    override fun visitFieldInsn(p0: Int, p1: String?, p2: String?, p3: String?) {
        super.visitFieldInsn(p0, p1, p2, p3)
    }

    override fun visitCode() {
        super.visitCode()
    }
}