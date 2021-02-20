package compiler.Instructions

import java.lang.StringBuilder

class Push(private val registers: List<Register>, private val cond: Conditions? = null) : Instruction {

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tPUSH")
        if (cond != null) instr.append(cond)
        instr.append(" {${registers.joinToString(separator = ",")}")
        return instr.toString()
    }
}