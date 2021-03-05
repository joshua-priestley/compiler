package compiler.CodeGen.Instructions.Operators

import compiler.Instructions.Instruction
import compiler.Instructions.Operand2
import compiler.Instructions.Register

open class AddSub(
        private val type: String,
        private val dstReg: Register,
        private val srcReg1: Register,
        private val op2: Operand2,
        private val s: Boolean
) : Instruction {


    override fun toString(): String {
        val sb : StringBuilder = StringBuilder("\t$type")
        sb.append(if(s) "S " else " ")
        sb.append("$dstReg, ")
        sb.append("$srcReg1, ")
        sb.append(op2)
        return sb.toString()
    }
}