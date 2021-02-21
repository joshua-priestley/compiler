package compiler.Instructions

class Store : Instruction {
    private val srcReg: Register
    private val dstAddress: Register
    private val offset: Int
    private val cond: Conditions?

    constructor(srcReg: Register, dstAddress: Register, offset: Int, condition: Conditions? = null) {
        this.srcReg = srcReg
        this.dstAddress = dstAddress
        this.offset = offset
        this.cond = condition
    }

    constructor(srcReg: Register, dstAddress: Register, condition: Conditions? = null) {
        this.srcReg = srcReg
        this.dstAddress = dstAddress
        this.offset = 0
        this.cond = condition
    }

    override fun toString(): String {
        val output = StringBuilder()
        output.append("\tSTR")
        if (cond != null) output.append(cond)
        output.append(" $srcReg, [$dstAddress")
        if (offset != 0) output.append(", #$offset")
        output.append("]")
        return output.toString()
    }
}