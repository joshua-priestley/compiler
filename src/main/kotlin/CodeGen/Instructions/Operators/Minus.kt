package compiler.CodeGen.Instructions.Operators

import CodeGen.Instructions.Instruction
import compiler.Instructions.Register

class Minus(private val reg : Register) : Instruction {
    override fun toString(): String {
        return ("\tRSBS $reg, $reg, #0")
    }
}