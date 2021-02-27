package compiler.Instructions

abstract class Labels(private val label:String): Instruction {
    override fun toString(): String {
        return label
    }
}

class GlobalLabel(label: String): Labels(".$label")

class LocalLabel(label: String): Labels("\t.$label")

class FunctionDeclaration(name: String): Labels("$name:")

class MessageLabel(value: Int): Labels(".msg_$value")