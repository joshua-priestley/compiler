package compiler.Instructions

import java.lang.StringBuilder

abstract class Shift : Operand2 {

    private val imm: Int?
    private val reg: Register?

    constructor(reg: Register) {
        this.reg = reg
        this.imm = null
    }

    constructor(imm: Int) {
        this.reg = null
        this.imm = imm
    }

    constructor(reg: Register, imm: Int) {
        this.reg = reg
        this.imm = imm
    }

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

class LogicalShiftRight(private val reg: Register, private val imm: Int) : Shift(reg, imm) {
    override fun getType(): ShiftType = ShiftType.LSR
}

class ArithmeticShiftRight(private val reg: Register, private val imm: Int) : Shift(reg, imm) {
    override fun getType(): ShiftType = ShiftType.ASR
}

class RotateRight(private val reg: Register, private val imm: Int) : Shift(reg, imm) {
    override fun getType(): ShiftType = ShiftType.ROR
}

class RotateRightExtend(private val reg: Register) : Shift(reg) {
    override fun getType(): ShiftType = ShiftType.RRX
}

enum class ShiftType {
    LSL,    // Logical Shift Left
    LSR,    // Logical Shift Right
    ASR,    // Arithmetic Shift Right
    ROR,    // Rotate Right
    RRX     // Rotate Right Extended
}