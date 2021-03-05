package compiler.Instructions

import java.lang.StringBuilder

abstract class Shift(private val reg: Register, private val imm: Int) : Operand2 {

    abstract fun getType(): ShiftType

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("$reg, ")
        instr.append(getType())
        instr.append(" #$imm")
        return instr.toString()
    }
}

class LogicalShiftLeft(reg: Register, imm: Int) : Shift(reg, imm) {
    override fun getType(): ShiftType = ShiftType.LSL
}


class ArithmeticShiftRight(reg: Register, imm: Int) : Shift(reg, imm) {
    override fun getType(): ShiftType = ShiftType.ASR
}


enum class ShiftType {
    LSL,    // Logical Shift Left
    ASR,    // Arithmetic Shift Right
}