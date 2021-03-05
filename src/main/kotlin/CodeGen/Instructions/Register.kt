package compiler.Instructions

const val NO_GEN_PURPOSE_REG = 12

enum class Register : Operand2 {
    r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, sp, lr, pc, cpsr;

    //Get the next available register (from r0 to r11)
    fun nextAvailable(): Register {
        return values()[(this.ordinal + 1) % NO_GEN_PURPOSE_REG]

    }
}