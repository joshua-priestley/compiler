package compiler.Instructions

class Sub(dstReg: Register, srcReg1: Register, op2: Operand2, s: Boolean = false) :
    AddSub("SUB", dstReg, srcReg1, op2, s) {
}