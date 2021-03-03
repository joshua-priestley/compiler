package compiler.Instructions

class Minus(private val reg : Register) : Instruction{
    override fun toString(): String {
        return ("\tRSBS $reg, $reg, #0")
    }
}