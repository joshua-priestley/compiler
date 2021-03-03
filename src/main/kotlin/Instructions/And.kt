package compiler.Instructions

class And(dstReg: Register, src1: Register, op2: Operand2) : AndOr("AND", dstReg, src1, op2) {
}