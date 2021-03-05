package compiler.Instructions

interface Operand2

data class ImmOp(val imm: Int) : Operand2 {
    override fun toString(): String = "#$imm"
}

data class CharOp(val chr: Char) : Operand2 {
    override fun toString(): String = "#'$chr'"
}
