package compiler.Instructions

class And(dstReg: Register, src1: Register, src2: Register) : AndOr("AND", dstReg, src1, src2) {
}