package compiler.Instructions

enum class Conditions {
    EQ,     // Equal
    NE,     // Not Equal
    GE,     // >
    GT,     // >=
    LE,     // <
    LT,     // <=
    VS,     // Overflow
    VC,     // No Overflow
    AL,     // Always
    MI,     // Negative
    PL,     // Positive or Zero
    CS,     // Carry set
}