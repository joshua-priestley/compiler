package compiler.Instructions

class Sub: AddSub {

    constructor(dstReg : Register, srcReg1 : Register, srcReg2: Register, s : Boolean = false) : super("SUB", dstReg, srcReg1, srcReg2, s)

    constructor(dstReg: Register, srcReg1: Register, imm : Int, s : Boolean = false) : super("SUB", dstReg, srcReg1, imm, s)
}