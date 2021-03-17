package compiler.CodeGen.Instructions.Operators

import compiler.CodeGen.Instructions.Instruction
import compiler.Instructions.Operand2
import compiler.Instructions.Register
//Class to generate ADD and SUB instructions
open class AddSub(
        private val type: String,           //ADD or SUB
        private val dstReg: Register,
        private val srcReg1: Register,
        private val op2: Operand2,
        private val s: Boolean              //Update flags
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