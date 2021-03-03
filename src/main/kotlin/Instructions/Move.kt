package compiler.Instructions

import java.lang.StringBuilder

class Move(
    private val dstReg: Register,
    private val op2: Operand2,
    private val cond: Conditions? = null
) : Instruction {

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tMOV")
        if (cond != null) instr.append(cond)
        instr.append(" $dstReg, ")
        instr.append(op2)
        return instr.toString()
    }
}