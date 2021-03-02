package compiler.Instructions

import java.lang.StringBuilder

class Branch: Instruction {
    private val label: String
    private val cond: Conditions?
    private val link : Boolean
    constructor(label: String, link : Boolean, cond: Conditions? = null) {
        this.label = label
        this.cond = cond
        this.link = link
    }



    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tB")
        if (link) instr.append("L")
        if (cond != null) instr.append(cond)
        instr.append(" $label")
        return instr.toString()
    }


}