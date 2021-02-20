package compiler.Instructions

import java.lang.StringBuilder

class Pop(private val registers: List<Register>) : Instruction {

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tPOP")
        instr.append(" {${registers.joinToString(separator = ",")}")
        return instr.toString()
    }
}