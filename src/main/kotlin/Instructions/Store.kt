package compiler.Instructions

class Store : Instruction {
    private val srcReg: Register
    private val dstAddress: Register
    private val offset: Int

    constructor(srcReg: Register, dstAddress: Register, offset: Int) {
        this.srcReg = srcReg
        this.dstAddress = dstAddress
        this.offset = offset
    }

    constructor(srcReg: Register, dstAddress: Register) {
        this.srcReg = srcReg
        this.dstAddress = dstAddress
        this.offset = 0
    }

    override fun toString(): String {
        TODO("Not yet implemented")
    }
}