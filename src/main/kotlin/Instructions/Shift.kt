package compiler.Instructions

interface Shift {
    override fun toString(): String
}


class RotateRightExtend() : Shift {
    // Doesnt take any values?
    override fun toString(): String {
        return "RRX"
    }

}

enum class ShiftType {
    LSL,    // Logical Shift
    LSR,    // Logical Shift Right
    ASR,    // Arithmetic Shift Right
    ROR,    // Rotate Right
    RRX     // Rotate Right Extended
}