package compiler.Instructions

class AndOr(private val type: String, private val dstReg: Register, private val src1: Register, private val src2: Register) : Instruction {
    override fun toString(): String {
        return ("$type $dstReg, $src1, $src2")
    }
}