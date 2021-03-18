package compiler.CodeGen.Instructions.External

import compiler.CodeGen.Instructions.Instruction

abstract class Labels(private val label:String): Instruction {
    override fun toString(): String {
        return label
    }
}

// Meta labels
class GlobalLabel(label: String): Labels(".$label")

// Labels for loops and conditionals
class LocalLabel(label: String): Labels("\t.$label")

// Labels for functions
class FunctionDeclaration(name: String): Labels("$name:")

// Labels for messages in data segments
class MessageLabel(value: Int): Labels("msg_$value")