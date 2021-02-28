package compiler.Instructions

import java.lang.StringBuilder

class Move: Instruction {
    private val dstReg: Register
    private val srcReg: Register?
    private val srcInt: Int?
    private val srcChar: Char?
    private val cond: Conditions?

    constructor(dstReg: Register, srcReg: Register, conditions: Conditions? = null) {
        this.dstReg = dstReg
        this.srcReg = srcReg
        this.srcInt = null
        this.srcChar = null
        this.cond = conditions
    }

    constructor(dstReg: Register, srcInt: Int, condition: Conditions? = null) {
        this.dstReg = dstReg
        this.srcReg = null
        this.srcInt = srcInt
        this.srcChar = null
        this.cond = condition
    }

    constructor(dstReg: Register, srcChar: Char, condition: Conditions? = null) {
        this.dstReg = dstReg
        this.srcReg = null
        this.srcInt = null
        this.srcChar = srcChar
        this.cond = condition
    }

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tMOV")
        if (cond != null) instr.append(cond)
        instr.append(" $dstReg, ")
        if (srcReg != null) {
            instr.append("$srcReg")
        } else if (srcInt != null){
            instr.append("#$srcInt")
        } else {
            instr.append("#'$srcChar'")
        }
        return instr.toString()    }
}