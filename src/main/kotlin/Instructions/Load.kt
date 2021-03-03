package compiler.Instructions

import java.lang.StringBuilder

class Load : Instruction {
    private val dstReg: Register
    private val srcReg: Register?
    private val srcValue: String?
    private val offset: Int
    private val cond: Conditions?
    private val sb: Boolean

    constructor(dstReg: Register, srcValue: String, condition: Conditions? = null, sb : Boolean = false) {
        this.dstReg = dstReg
        this.srcValue = srcValue
        this.srcReg = null
        this.offset = 0
        this.cond = condition
        this.sb = sb
    }

    constructor(dstReg: Register, srcReg: Register, condition: Conditions? = null, sb : Boolean = false) {
        this.dstReg = dstReg
        this.srcReg = srcReg
        this.srcValue = null
        this.offset = 0
        this.cond = condition
        this.sb = sb
    }

    constructor(dstReg: Register, srcReg: Register, offset: Int, condition: Conditions? = null, sb : Boolean = false) {
        this.dstReg = dstReg
        this.srcReg = srcReg
        this.srcValue = null
        this.offset = offset
        this.cond = condition
        this.sb = sb
    }

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tLDR")
        if (sb) instr.append("SB")
        if (cond != null) instr.append(cond)
        instr.append(" $dstReg, ")
        if (srcReg != null) {
            instr.append("[$srcReg")
            if (offset != 0) {
                instr.append(", #$offset")
            }
            instr.append("]")
        } else {
            instr.append("=$srcValue")
        }
        return instr.toString()
    }
}