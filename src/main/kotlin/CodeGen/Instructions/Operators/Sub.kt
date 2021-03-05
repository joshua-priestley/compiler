package compiler.CodeGen.Instructions.Operators

import compiler.Instructions.Operand2
import compiler.Instructions.Register

class Sub(dstReg: Register, srcReg1: Register, op2: Operand2, s: Boolean = false) :
    AddSub("SUB", dstReg, srcReg1, op2, s) {
}