package compiler.Instructions

import java.lang.StringBuilder

class Compare: Instruction {
    private val firstReg: Register
    private val secondReg: Register?
    private val secondInt: Int?
    private val cond: Conditions?

    constructor(firstReg: Register, secondReg: Register, cond: Conditions? = null) {
        this.firstReg = firstReg
        this.secondReg = secondReg
        this.secondInt = null
        this.cond = cond
    }

    constructor(firstReg: Register, secondInt: Int, cond: Conditions? = null) {
        this.firstReg = firstReg
        this.secondReg = null
        this.secondInt = secondInt
        this.cond = cond
    }

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tCMP")
        if (cond != null) instr.append(cond)
        instr.append(" $firstReg, ")
        if (secondReg != null) {
            instr.append(secondReg)
        } else {
            instr.append("#$secondInt")
        }
        return instr.toString()
    }
}