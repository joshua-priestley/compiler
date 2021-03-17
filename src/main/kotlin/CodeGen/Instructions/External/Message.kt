package compiler.CodeGen.Instructions.External

import compiler.CodeGen.Instructions.Instruction

// Stores the value of a message in the data segment
data class Message(val value : String) : Instruction {

    override fun equals(other: Any?): Boolean {
        if (other is Message) {
            val otherMessage: Message = other
            return other.value == otherMessage.value
        }
        return false
    }

    override fun toString(): String {
        return value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}