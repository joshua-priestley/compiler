package compiler.Instructions

class Or(dstReg: Register, src1: Register, src2: Register) : AndOr("ORR", dstReg, src1, src2) {
}