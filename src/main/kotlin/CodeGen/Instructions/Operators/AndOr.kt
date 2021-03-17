package compiler.CodeGen.Instructions.Operators

import compiler.CodeGen.Instructions.Instruction
import compiler.Instructions.Operand2
import compiler.Instructions.Register
//Class to generate AND and ORR instructions
open class AndOr(private val type: String, private val dstReg: Register, private val src1: Register, private val op2: Operand2) : Instruction {
    override fun toString(): String {
        return ("\t$type $dstReg, $src1, $op2")
    }
}