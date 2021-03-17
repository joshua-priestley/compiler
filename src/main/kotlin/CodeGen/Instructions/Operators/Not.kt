package compiler.CodeGen.Instructions.Operators

import compiler.CodeGen.Instructions.Instruction
import compiler.Instructions.Register

class Not(private val dst: Register, private val src: Register) : Instruction {
    override fun toString(): String {
      return ("\tEOR $dst, $src, #1")
    }
}