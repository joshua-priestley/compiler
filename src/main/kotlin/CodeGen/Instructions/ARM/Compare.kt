package compiler.CodeGen.Instructions.ARM

import compiler.Instructions.Conditions
import compiler.CodeGen.Instructions.Instruction
import compiler.Instructions.Operand2
import compiler.Instructions.Register
import java.lang.StringBuilder

class Compare(
        private val firstReg: Register,
        private val op2: Operand2,
        private val cond: Conditions? = null
) : Instruction {

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tCMP")
        if (cond != null) instr.append(cond)
        instr.append(" $firstReg, ")
        instr.append(op2)
        return instr.toString()
    }
}