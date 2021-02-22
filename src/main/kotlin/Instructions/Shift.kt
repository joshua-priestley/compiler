package compiler.Instructions

import java.lang.StringBuilder

abstract class Shift {
    private val imm: Int?
    private val reg: Register?

    constructor() {
        this.imm = null
        this.reg = null
    }

    constructor(imm: Int) {
        this.imm = imm
        this.reg = null
    }

    constructor(reg: Register) {
        this.imm = null
        this.reg = reg
    }

    abstract fun getType(): ShiftType

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append(getType())
        when {
            imm != null -> instr.append(" #$imm")
            else -> instr.append(" $reg")
        }
        return instr.toString()
    }
}

class LogicalShiftLeft : Shift {
    constructor(imm: Int) : super(imm)
    constructor(reg: Register) : super(reg)

    override fun getType(): ShiftType {
        return ShiftType.LSL
    }

}

class LogicalShiftRight : Shift {
    constructor(imm: Int) : super(imm)
    constructor(reg: Register) : super(reg)

    override fun getType(): ShiftType {
        return ShiftType.LSR
    }

}

class ArithmeticShiftRight : Shift {
    constructor(imm: Int) : super(imm)
    constructor(reg: Register) : super(reg)

    override fun getType(): ShiftType {
        return ShiftType.ASR
    }

}

class RotateRight : Shift {
    constructor(imm: Int) : super(imm)
    constructor(reg: Register) : super(reg)

    override fun getType(): ShiftType {
        return ShiftType.ROR
    }

}

class RotateRightExtend : Shift() {

    override fun toString(): String {
        return "RRX"
    }

    override fun getType(): ShiftType {
        return ShiftType.RRX
    }
}

enum class ShiftType {
    LSL,    // Logical Shift Left
    LSR,    // Logical Shift Right
    ASR,    // Arithmetic Shift Right
    ROR,    // Rotate Right
    RRX     // Rotate Right Extended
}