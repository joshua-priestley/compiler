package compiler.Instructions

class Add : Instruction {
    private val dstReg : Register
    private val srcReg1 : Register?
    private val srcReg2 : Register?
    private val s : Boolean
    private val imm : Int?

    constructor(dstReg : Register, srcReg1 : Register, srcReg2: Register, s : Boolean){
        this.dstReg = dstReg
        this.srcReg1 = srcReg1
        this.srcReg2 = srcReg2
        this.s = s
        this.imm = null
    }

    constructor(dstReg: Register, srcReg1: Register, imm : Int, s : Boolean){
        this.dstReg = dstReg
        this.srcReg1 = srcReg1
        this.srcReg2 = null
        this.s = s
        this.imm = imm
    }
    override fun toString(): String {
        val sb : StringBuilder = StringBuilder("AND")
        sb.append(if(s) "S " else " ")
        sb.append("$dstReg, ")
        sb.append(", ")
        if (srcReg1 != null) sb.append("$srcReg1, ")
        if (srcReg2 != null) sb.append("$srcReg2, ")
        if (imm != null) sb.append("#$imm")

        return sb.toString()
    }
}