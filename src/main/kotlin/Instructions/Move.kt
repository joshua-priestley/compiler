package compiler.Instructions

import java.lang.StringBuilder

class Move: Instruction {
    private val dstReg: Register
    private val srcReg: Register?
    private val srcInt: Int?
    private val srcChar: Char?

    constructor(dstReg: Register, srcReg: Register) {
        this.dstReg = dstReg
        this.srcReg = srcReg
        this.srcInt = null
        this.srcChar = null
    }

    constructor(dstReg: Register, srcInt: Int) {
        this.dstReg = dstReg
        this.srcReg = null
        this.srcInt = srcInt
        this.srcChar = null
    }

    constructor(dstReg: Register, srcChar: Char) {
        this.dstReg = dstReg
        this.srcReg = null
        this.srcInt = null
        this.srcChar = srcChar
    }

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tMOV")
        instr.append(" $dstReg, ")
        if (srcReg != null) {
            instr.append("$srcReg")
        } else if (srcInt != null){
            instr.append("#$srcInt")
        } else {
            instr.append("'#$srcChar'")
        }
        return instr.toString()    }
}