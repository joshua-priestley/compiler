package compiler.Instructions

class Add(dstReg: Register, srcReg1: Register, op2: Operand2, s: Boolean = false) :
    AddSub("ADD", dstReg, srcReg1, op2, s) {
}