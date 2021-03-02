package compiler.Instructions

const val NO_GEN_PURPOSE_REG = 13

enum class Register {
    R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, SP, LR, PC, CPSR;

    fun nextAvailable(): Register {
        return values()[(this.ordinal + 1) % NO_GEN_PURPOSE_REG]
    }
}