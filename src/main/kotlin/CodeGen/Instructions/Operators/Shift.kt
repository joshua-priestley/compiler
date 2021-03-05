package compiler.Instructions

import java.lang.StringBuilder

abstract class Shift(reg: Register, imm: Int) : Operand2 {

    private val imm: Int? = imm
    private val reg: Register? = reg

    abstract fun getType(): ShiftType

    override fun toString(): String {
        val instr = StringBuilder()
        if (reg != null) instr.append("$reg, ")
        instr.append(getType())
        if (imm != null) instr.append(" #$imm")
        return instr.toString()
    }
}

class LogicalShiftLeft(private val reg: Register, private val imm: Int) : Shift(reg, imm) {
    override fun getType(): ShiftType = ShiftType.LSL
}


class ArithmeticShiftRight(private val reg: Register, private val imm: Int) : Shift(reg, imm) {
    override fun getType(): ShiftType = ShiftType.ASR
}


enum class ShiftType {
    LSL,    // Logical Shift Left
    ASR,    // Arithmetic Shift Right
}