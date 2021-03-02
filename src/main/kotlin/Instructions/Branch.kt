package compiler.Instructions

import java.lang.StringBuilder

class Branch: Instruction {
    private val label: String
    private val cond: Conditions?
    private val L : Boolean
    constructor(label: String, L : Boolean, cond: Conditions? = null) {
        this.label = label
        this.cond = cond
        this.L = L
    }



    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tB")
        if (L) instr.append("L")
        if (cond != null) instr.append(cond)
        instr.append(" $label")
        return instr.toString()
    }


}