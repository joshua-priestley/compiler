package compiler.Instructions

import java.lang.StringBuilder

class Load : Instruction {
    private val dstReg: Register
    private val srcReg: Register?
    private val srcValue: String?
    private val offset: Int

    constructor(dstReg: Register, srcValue: String) {
        this.dstReg = dstReg
        this.srcValue = srcValue
        this.srcReg = null
        this.offset = 0
    }

    constructor(dstReg: Register, srcReg: Register) {
        this.dstReg = dstReg
        this.srcReg = srcReg
        this.srcValue = null
        this.offset = 0
    }

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tLDR")
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