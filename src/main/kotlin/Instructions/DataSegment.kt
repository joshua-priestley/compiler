package compiler.Instructions

import java.lang.StringBuilder

class DataSegment : Instruction {
    private var msgCount : Int = 0
    private val data : MutableMap<Message, MessageLabel> = HashMap<Message, MessageLabel>()

    fun addMessage(message: Message) {
        // Only add a new message is necessary
        if (!data.containsKey(message)) {
            val label = MessageLabel(msgCount++)
            data[message] = label
        }
    }

    fun getLabel(value: String) : MessageLabel? {
        val message: Message = Message(value)
        return data[message]
    }

    override fun toString(): String {
        val sb = StringBuilder()
        if (data.isNotEmpty()) {
            sb.append(".data\n\n")
            for ((message, label) in data) {
                sb.append(label.toString())
                sb.append("\n\t")
                sb.append(".word ${message.toString().length}")
                sb.append("\n\t")
                sb.append(".ascii \"$message\"")
                sb.append("\n")
            }
        }
        return sb.toString()
    }
}
