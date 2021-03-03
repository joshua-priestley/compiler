package compiler.Instructions

class Or(dstReg: Register, src1: Register, op2: Operand2) : AndOr("ORR", dstReg, src1, op2) {
}