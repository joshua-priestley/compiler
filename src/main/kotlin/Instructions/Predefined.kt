package compiler.Instructions

import java.lang.StringBuilder

// Set of external functions that we are including in the program
class PredefinedFuncs(private val data: DataSegment) {
    private val funcSet: MutableSet<Predefined> = HashSet()

    // Add a predefined function to the set, return the string name
    fun addFunc(func: Predefined): String {
        // Add the data string for the given function to the data segment
        if (func.msg.isNotEmpty()) {
            data.addMessage(Message(func.msg))
        }
        // Add the function to the set of external functions
        funcSet.add(func)
        if (func is RuntimeError) {
            funcSet.add(ThrowRuntimeError())
        }
        return func.name
    }

    // Get the list of all instructions for external functions
    fun toInstructionList(): List<Instruction> =
        funcSet.toList()
            .map { it.getInstructions(data) }
            .flatten()
}

//TODO finish once arithmetic ops have been done
abstract class Predefined() {
    // Superclasses should use "by lazy" to get the msg label after it is added
    abstract val name: String
    abstract val msg: String

    abstract fun getInstructions(data: DataSegment) : List<Instruction>

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

abstract class RuntimeError() : Predefined()


class PrintLn() : Predefined() {
    override val name = "p_print_ln"
    override val msg = "\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
        listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Load(Register.R0, data.getLabel(msg)),
            Add(Register.R0, Register.R0, 4, false),
            Branch("puts", Conditions.L),
            Move(Register.R0, 0),
            Branch("fflush", Conditions.L),
            Pop(listOf(Register.PC))
        )
}

class PrintString() : Predefined() {
    override val name = "p_print_string"
    override val msg = "%.*s\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
        listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Load(Register.R1, Register.R0),
            Add(Register.R2, Register.R0, 4, false),
            Load(Register.R0, data.getLabel(msg)),
            Add(Register.R0, Register.R0, 4, false),
            Branch("printf", Conditions.L),
            Move(Register.R0, 0),
            Branch("fflush", Conditions.L),
            Pop(listOf(Register.PC))
        )
}

class PrintInt() : Predefined() {
    override val name = "p_print_int"
    override val msg = "%d\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
        listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Move(Register.R1, Register.R0),
            Load(Register.R0, data.getLabel(msg)),
            Add(Register.R0, Register.R0, 4, false),
            Branch("printf", Conditions.L),
            Move(Register.R0, 0),
            Branch("fflush", Conditions.L),
            Pop(listOf(Register.PC))
        )
}


class PrintBool() : Predefined() {
    override val name = "p_print_string"
    override val msg = "true\\0"
    val msg2 = "false\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> {
        data.addMessage(Message(msg2))
        return listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Compare(Register.R0, 0),
            Load(Register.R0, data.getLabel(msg), Conditions.NE),
            Load(Register.R0, data.getLabel(msg2), Conditions.EQ),
            Add(Register.R0, Register.R0, 4, false),
            Branch("printf", Conditions.L),
            Move(Register.R0, 0),
            Branch("fflush", Conditions.L),
            Pop(listOf(Register.PC))
        )
    }
}

class ReadInt() : Predefined() {
    override val name = "p_read_int"
    override val msg = "%d\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
        listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Move(Register.R1, Register.R0),
            Load(Register.R0, data.getLabel(msg)),
            Add(Register.R0, Register.R0, 4, false),
            Branch("scanf", Conditions.L),
            Pop(listOf(Register.PC))
        )
}

// TODO potentially combine with ReadInt to avoid duplication?
class ReadChar() : Predefined() {
    override val name = "p_read_char"
    override val msg = "%c\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
        listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Move(Register.R1, Register.R0),
            Load(Register.R0, data.getLabel(msg)),
            Add(Register.R0, Register.R0, 4, false),
            Branch("scanf", Conditions.L),
            Pop(listOf(Register.PC))
        )
}

class ThrowRuntimeError() : Predefined() {
    override val name = "p_throw_runtime_error"
    override val msg = ""

    override fun getInstructions(data: DataSegment): List<Instruction> =
        listOf(
            FunctionDeclaration(name),
            Move(Register.R0, -1),
            Branch("exit", Conditions.L)
        )
}

class DivideByZero() : RuntimeError() {
    override val name = "p_check_divide_by_zero"
    override val msg = "DivideByZeroError: divide or modulo by zero\\n\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
        listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Compare(Register.R1, 1),
            Load(Register.R0, data.getLabel(msg), Conditions.EQ),
            Branch(ThrowRuntimeError().name, Conditions.EQ), // TODO add link to Branch
            Pop(listOf(Register.PC))
        )
}

class Overflow() : RuntimeError() {
    override val name = "p_throw_overflow_error"
    override val msg = "OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
        listOf(
            FunctionDeclaration(name),
            Load(Register.R0, data.getLabel(msg)),
            Branch(ThrowRuntimeError().name, Conditions.EQ) // TODO add link to Branch
        )
}

class CheckNullPointer() : RuntimeError() {
    override val name = "p_check_null_pointer"
    override val msg = "NullReferenceError: dereference a null reference.\\n\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
        listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Compare(Register.R0, 0),
            Load(Register.R0, data.getLabel(msg)),
            Branch(ThrowRuntimeError().name, Conditions.EQ), // TODO add link to Branch
            Pop(listOf(Register.PC))
        )
}

class CheckArrayBounds() : RuntimeError() {
    override val name = "p_check_null_pointer"
    override val msg = "ArrayIndexOutOfBoundsError: negative index\\n\\0"
    val msg2 = "ArrayIndexOutOfBoundsError: index too large\\n\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> {
        data.addMessage(Message(msg2))
        return listOf(
            FunctionDeclaration(name),
            Push(listOf(Register.LR)),
            Compare(Register.R0, 0),
            Load(Register.R0, data.getLabel(msg), Conditions.LT),
            Branch(ThrowRuntimeError().name, Conditions.LT),
            Load(Register.R1, Register.R1),
            Compare(Register.R0, Register.R1),
            Load(Register.R0, data.getLabel(msg2), Conditions.CS),
            Branch(ThrowRuntimeError().name, Conditions.CS), // TODO add link to Branch
            Pop(listOf(Register.PC))
        )
    }
}
