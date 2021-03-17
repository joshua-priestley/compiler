package compiler.CodeGen.Instructions.External

import compiler.CodeGen.Instructions.Instruction
import java.lang.StringBuilder

// Stores mapping of messages to labels for the data segment
class DataSegment : Instruction {
    // number of next message to include
    private var msgCount : Int = 0
    private val data : MutableMap<Message, MessageLabel> = HashMap()

    fun addMessage(message: Message) {
        // Only add a new message if the string isn't currently included
        if (!data.containsKey(message)) {
            val label = MessageLabel(msgCount++)
            data[message] = label
        }
    }

    // Retrun the string of the message label
    fun getLabel(value: String) : String {
        val message: Message = Message(value)
        return data[message].toString()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        // Output data segment items in order of message labels
        val sortedMap = data.toList().sortedBy { (_, v) -> v.toString() }.toMap()
        if (data.isNotEmpty()) {
            sb.append(".data\n\n")
            for ((message, label) in sortedMap) {
                sb.append("$label:")
                sb.append("\n\t")
                // Ignore backslashes for escape characters when counting message length
                sb.append(".word ${message.toString().filter{ it != '\\'}.length}")
                sb.append("\n\t")
                sb.append(".ascii \"$message\"")
                sb.append("\n")
            }
        }
        return sb.toString()
    }
}
