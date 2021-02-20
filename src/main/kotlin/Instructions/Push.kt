package compiler.Instructions

import java.lang.StringBuilder

class Push: Instruction {

    private val registers: List<Register>

    constructor(registers: List<Register>) {
        this.registers = registers
    }

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tPUSH")
        instr.append(" {${registers.joinToString(separator = ",")}")
        return instr.toString()
    }
}