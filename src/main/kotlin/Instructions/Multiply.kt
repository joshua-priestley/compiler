package compiler.Instructions

class Multiply(private val dstHi: Register, private val dstLo: Register, private val srcReg1: Register, private val srcReg2: Register, private val s: Boolean) {

    override fun toString(): String {
        val sb : StringBuilder = StringBuilder("SMULL")
        sb.append(if(s) "S " else " ")
        sb.append("$dstHi, ")
        sb.append("$dstLo, ")
        sb.append("$srcReg1, ")
        sb.append("$srcReg2")

        return sb.toString()
    }
}