package compiler.Instructions

import java.lang.StringBuilder

class Load : Instruction {
    private val dstReg: Register
    private val srcReg: Register?
    private val srcValue: String?
    private val offset: Int
    private val cond: Conditions?

    constructor(dstReg: Register, srcValue: String, condition: Conditions? = null) {
        this.dstReg = dstReg
        this.srcValue = srcValue
        this.srcReg = null
        this.offset = 0
        this.cond = condition
    }

    constructor(dstReg: Register, srcReg: Register, condition: Conditions? = null) {
        this.dstReg = dstReg
        this.srcReg = srcReg
        this.srcValue = null
        this.offset = 0
        this.cond = condition
    }

    constructor(dstReg: Register, srcReg: Register, offset: Int, condition: Conditions? = null) {
        this.dstReg = dstReg
        this.srcReg = srcReg
        this.srcValue = null
        this.offset = offset
        this.cond = condition
    }

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tLDR")
        if (cond != null) instr.append(cond)
        instr.append(" $dstReg, ")
        if (srcReg != null) {
            instr.append("[$srcReg")
            if (offset != 0) {
                instr.append(" , #$offset")
            }
            instr.append("]")
        } else {
            instr.append("=$srcValue")
        }
        return instr.toString()
    }
}