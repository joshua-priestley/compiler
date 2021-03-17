package compiler.CodeGen.Instructions.ARM

import compiler.Instructions.Conditions
import compiler.CodeGen.Instructions.Instruction
import java.lang.StringBuilder

class Branch(private val label: String, private val link: Boolean, private val cond: Conditions? = null) : Instruction {


    override fun toString(): String {
        val instr = StringBuilder()
        instr.append("\tB")
        if (link) instr.append("L")
        if (cond != null) instr.append(cond)
        instr.append(" $label")
        return instr.toString()
    }


}