package compiler.Instructions

interface Operand2

data class ImmOp(val imm: Int) : Operand2 {
    override fun toString(): String = "#$imm"
}

data class CharOp(val chr: Char) : Operand2 {
    override fun toString(): String = "#'$chr'"
}

data class ShiftInstruction(val reg: Register, val shiftType: ShiftType, val shift: Int) : Operand2 {
    override fun toString(): String = "$reg, $shiftType, #$shift"
}

data class RegOp(val reg: Register): Operand2 {
    override fun toString(): String = reg.toString()
}