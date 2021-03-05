package compiler.CodeGen.Instructions.Operators

import compiler.Instructions.Operand2
import compiler.Instructions.Register

class Or(dstReg: Register, src1: Register, op2: Operand2) : AndOr("ORR", dstReg, src1, op2) {
}