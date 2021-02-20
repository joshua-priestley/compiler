package compiler.Instructions

import java.lang.StringBuilder

class Branch: Instruction {
    private val label: String
    private val cond: Conditions?

    constructor(label: String, cond: Conditions) {
        this.label = label
        this.cond = cond
    }

    constructor(label: String) {
        this.label = label
        this.cond = null
    }

    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tB")
        if (cond != null) instr.append(cond)
        instr.append(" $label")
        return instr.toString()
    }


}