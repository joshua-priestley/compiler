package compiler.CodeGen.Instructions.Operators

import compiler.Instructions.Operand2
import compiler.Instructions.Register

class And(dstReg: Register, src1: Register, op2: Operand2) : AndOr("AND", dstReg, src1, op2) {
}