package compiler.Instructions

class Store : Instruction {
    private val srcReg: Register
    private val dstAddress: Register
    private val offset: Int
    private val cond: Conditions?
    private val parameter: Boolean

    constructor(srcReg: Register, dstAddress: Register, offset: Int, condition: Conditions? = null, parameter: Boolean = false) {
        this.srcReg = srcReg
        this.dstAddress = dstAddress
        this.offset = offset
        this.cond = condition
        this.parameter = parameter
    }

    constructor(srcReg: Register, dstAddress: Register, condition: Conditions? = null, parameter: Boolean = false) {
        this.srcReg = srcReg
        this.dstAddress = dstAddress
        this.offset = 0
        this.cond = condition
        this.parameter = parameter
    }

    override fun toString(): String {
        val output = StringBuilder()
        output.append("\tSTR")
        if (cond != null) output.append(cond)
        output.append(" $srcReg, [$dstAddress")
        if (offset != 0) output.append(", #$offset")
        output.append("]")
        if (parameter) output.append("!")
        return output.toString()
    }
}