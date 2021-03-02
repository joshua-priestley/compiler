package compiler.Instructions

class Not(private val dst: Register, private val src: Register) : Instruction {
    override fun toString(): String {
      return ("EOR $dst, $src, #1")
    }
}