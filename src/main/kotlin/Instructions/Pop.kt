package compiler.Instructions

import java.lang.StringBuilder

class Pop: Instruction {

    private val registers: List<Register>

    constructor(registers: List<Register>) {
        this.registers = registers
    }

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tPOP")
        instr.append(" {${registers.joinToString(separator = ",")}")
        return instr.toString()
    }
}