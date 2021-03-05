package compiler.Instructions

// Set of external functions that we are including in the program
class PredefinedFuncs(private val data: DataSegment) {
    // Use list to preserve order of functions
    private val funcSet: MutableList<Predefined> = ArrayList()

    // Add a predefined function to the set, return the string name
    fun addFunc(func: Predefined): String {
        // Add the data strings for the given function to the data segment
        if (func.msg.isNotEmpty()) {
            data.addMessage(Message(func.msg))
        }
        if (!func.msg2.isNullOrBlank()) {
            data.addMessage(Message(func.msg2!!))
        }
        // Add the function to the set of external functions
        if (!funcSet.contains(func)) {
            funcSet.add(func)
        }
        // Adds other necessary functions for runtime errors
        if (func is RuntimeError && !funcSet.contains(ThrowRuntimeError())) {
            funcSet.add(ThrowRuntimeError())
            addFunc(PrintString())
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
abstract class Predefined {
    // Superclasses should use "by lazy" to get the msg label after it is added
    abstract val name: String
    abstract val msg: String
    open val msg2: String? = null

    abstract fun getInstructions(data: DataSegment): List<Instruction>

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

abstract class RuntimeError : Predefined()


class PrintLn : Predefined() {
    override val name = "p_print_ln"
    override val msg = "\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Load(Register.r0, data.getLabel(msg)),
                    Add(Register.r0, Register.r0, ImmOp(4), false),
                    Branch("puts", true),
                    Move(Register.r0, ImmOp(0)),
                    Branch("fflush", true),
                    Pop(listOf(Register.pc))
            )
}

class PrintString : Predefined() {
    override val name = "p_print_string"
    override val msg = "%.*s\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Load(Register.r1, Register.r0),
                    Add(Register.r2, Register.r0, ImmOp(4), false),
                    Load(Register.r0, data.getLabel(msg)),
                    Add(Register.r0, Register.r0, ImmOp(4), false),
                    Branch("printf", true),
                    Move(Register.r0, ImmOp(0)),
                    Branch("fflush", true),
                    Pop(listOf(Register.pc))
            )
}

class PrintInt : Predefined() {
    override val name = "p_print_int"
    override val msg = "%d\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Move(Register.r1, Register.r0),
                    Load(Register.r0, data.getLabel(msg)),
                    Add(Register.r0, Register.r0, ImmOp(4), false),
                    Branch("printf", true),
                    Move(Register.r0, ImmOp(0)),
                    Branch("fflush", true),
                    Pop(listOf(Register.pc))
            )
}


class PrintBool : Predefined() {
    override val name = "p_print_bool"
    override val msg = "true\\0"
    override val msg2 = "false\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Compare(Register.r0, ImmOp(0)),
                    Load(Register.r0, data.getLabel(msg), Conditions.NE),
                    Load(Register.r0, data.getLabel(msg2), Conditions.EQ),
                    Add(Register.r0, Register.r0, ImmOp(4), false),
                    Branch("printf", true),
                    Move(Register.r0, ImmOp(0)),
                    Branch("fflush", true),
                    Pop(listOf(Register.pc))
            )
}

class PrintReference : Predefined() {
    override val name = "p_print_reference"
    override val msg = "%p\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Move(Register.r1, Register.r0),
                    Load(Register.r0, data.getLabel(msg)),
                    Add(Register.r0, Register.r0, ImmOp(4)),
                    Branch("printf", true),
                    Move(Register.r0, ImmOp(0)),
                    Branch("fflush", true),
                    Pop(listOf(Register.pc))
            )


}

class ReadInt : Predefined() {
    override val name = "p_read_int"
    override val msg = "%d\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Move(Register.r1, Register.r0),
                    Load(Register.r0, data.getLabel(msg)),
                    Add(Register.r0, Register.r0, ImmOp(4), false),
                    Branch("scanf", true),
                    Pop(listOf(Register.pc))
            )
}

// TODO potentially combine with ReadInt to avoid duplication?
class ReadChar : Predefined() {
    override val name = "p_read_char"
    override val msg = " %c\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Move(Register.r1, Register.r0),
                    Load(Register.r0, data.getLabel(msg)),
                    Add(Register.r0, Register.r0, ImmOp(4), false),
                    Branch("scanf", true),
                    Pop(listOf(Register.pc))
            )
}

class ThrowRuntimeError : Predefined() {
    override val name = "p_throw_runtime_error"
    override val msg = ""

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Branch(PrintString().name, true),
                    Move(Register.r0, ImmOp(-1)),
                    Branch("exit", true)
            )
}

class DivideByZero : RuntimeError() {
    override val name = "p_check_divide_by_zero"
    override val msg = "DivideByZeroError: divide or modulo by zero\\n\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Compare(Register.r1, ImmOp(0)),
                    Load(Register.r0, data.getLabel(msg), Conditions.EQ),
                    Branch(ThrowRuntimeError().name, true, Conditions.EQ),
                    Pop(listOf(Register.pc))
            )
}

class Overflow : RuntimeError() {
    override val name = "p_throw_overflow_error"
    override val msg = "OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Load(Register.r0, data.getLabel(msg)),
                    Branch(ThrowRuntimeError().name, true, Conditions.EQ)
            )
}

class CheckNullPointer : RuntimeError() {
    override val name = "p_check_null_pointer"
    override val msg = "NullReferenceError: dereference a null reference.\\n\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Compare(Register.r0, ImmOp(0)),
                    Load(Register.r0, data.getLabel(msg)),
                    Branch(ThrowRuntimeError().name, true, Conditions.EQ),
                    Pop(listOf(Register.pc))
            )
}

class CheckArrayBounds : RuntimeError() {
    override val name = "p_check_array_bounds"
    override val msg = "ArrayIndexOutOfBoundsError: negative index\\n\\0"
    override val msg2 = "ArrayIndexOutOfBoundsError: index too large\\n\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Compare(Register.r0, ImmOp(0)),
                    Load(Register.r0, data.getLabel(msg), Conditions.LT),
                    Branch(ThrowRuntimeError().name, true, Conditions.LT),
                    Load(Register.r1, Register.r1),
                    Compare(Register.r0, Register.r1),
                    Load(Register.r0, data.getLabel(msg2), Conditions.CS),
                    Branch(ThrowRuntimeError().name, true, Conditions.CS),
                    Pop(listOf(Register.pc))
            )
}

class Freepair : RuntimeError() {
    override val name = "p_free_pair"
    override val msg = "NullReferenceError: dereference a null reference\\n\\0"

    override fun getInstructions(data: DataSegment): List<Instruction> =
            listOf(
                    FunctionDeclaration(name),
                    Push(listOf(Register.lr)),
                    Compare(Register.r0, ImmOp(0)),
                    Load(Register.r0, data.getLabel(msg), Conditions.EQ),
                    Branch(ThrowRuntimeError().name, false, Conditions.EQ),
                    Push(listOf(Register.r0)),
                    Load(Register.r0, Register.r0),
                    Branch("free", true),
                    Load(Register.r0, Register.sp),
                    Load(Register.r0, Register.r0, 4),
                    Branch("free", true),
                    Pop(listOf(Register.r0)),
                    Branch("free", true),
                    Pop(listOf(Register.pc))
            )
}
