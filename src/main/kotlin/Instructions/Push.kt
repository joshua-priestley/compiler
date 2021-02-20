package compiler.Instructions

import java.lang.StringBuilder

class Push(private val registers: List<Register>) : Instruction {

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tPUSH")
        instr.append(" {${registers.joinToString(separator = ",")}")
        return instr.toString()
    }
}