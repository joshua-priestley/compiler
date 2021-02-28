package compiler.Instructions

import java.lang.StringBuilder

// Set of external functions that we are including in the program
class PredefinedFuncs(private val data: DataSegment) {
    private val funcSet: MutableSet<Predefined> = HashSet()

    fun addFunc(func: Predefined) {
        // Add the data string for the given function to the data segment
        data.addMessage(Message(func.msg))
        // Add the function to the set of external functions
        funcSet.add(func)
    }

    // Get the list of all instructions for external functions
    fun toInstructionList(): List<Instruction> =
        funcSet.toList()
            .map { it.instructions }
            .flatten()
}

//TODO finish once arithmetic ops have been done
abstract class Predefined(data: DataSegment) {
    // Superclasses should use "by lazy" to get the msg label after it is added
    abstract val instructions: List<Instruction>
    abstract val name: String
    abstract val msg: String

    override fun toString(): String {
        val sb = StringBuilder()
        for (instruction in instructions) {
            sb.append(instruction.toString())
        }
        return sb.toString()
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Predefined) {
            // Equivalence on name is sufficient for our predefined semantics
            return name == other.name
        }
        return false
    }
}

class PrintLn(data: DataSegment) : Predefined(data) {
    override val name = "p_print_ln"
    override val msg = "\\0"
    override val instructions by lazy {
        listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Load(Register.R0, data.getLabel(msg)),
            //TODO Add(Register.R0, Register.R0, 4)
            Branch("puts", Conditions.L),
            Move(Register.R0, 0),
            Branch("fflush", Conditions.L),
            Pop(listOf(Register.PC))
        )
    }
}

class PrintString(data: DataSegment) : Predefined(data) {
    override val name = "p_print_string"
    override val msg = "%.*s\\0"
    override val instructions by lazy {
        listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Load(Register.R1, Register.R0),
            //TODO Add(Register.R2, Register.R0, 4)
            Load(Register.R0, data.getLabel(msg)),
            //TODO Add(Register.R0, Register.R0, 4)
            Branch("printf", Conditions.L),
            Move(Register.R0, 0),
            Branch("fflush", Conditions.L),
            Pop(listOf(Register.PC))
        )
    }
}