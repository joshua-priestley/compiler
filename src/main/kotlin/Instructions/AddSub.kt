package compiler.Instructions

open class AddSub: Instruction {
    private val type: String
    private val dstReg : Register
    private val srcReg1 : Register?
    private val srcReg2 : Register?
    private val s : Boolean
    private val imm : Int?

    constructor(type: String, dstReg : Register, srcReg1 : Register, srcReg2: Register, s : Boolean){
        this.type = type
        this.dstReg = dstReg
        this.srcReg1 = srcReg1
        this.srcReg2 = srcReg2
        this.s = s
        this.imm = null
    }

    constructor(type: String, dstReg: Register, srcReg1: Register, imm : Int, s : Boolean){
        this.type = type
        this.dstReg = dstReg
        this.srcReg1 = srcReg1
        this.srcReg2 = null
        this.s = s
        this.imm = imm
    }

    override fun toString(): String {
        val sb : StringBuilder = StringBuilder("\t$type")
        sb.append(if(s) "S " else " ")
        sb.append("$dstReg, ")
        if (srcReg1 != null) sb.append("$srcReg1, ")
        if (srcReg2 != null) sb.append("$srcReg2, ")
        if (imm != null) sb.append("#$imm")

        return sb.toString()
    }
}