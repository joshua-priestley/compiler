package compiler.Instructions

import java.lang.StringBuilder

class DataSegment : Instruction {
    private var msgCount : Int = 0
    private val data : MutableMap<Message, MessageLabel> = HashMap()

    fun addMessage(message: Message) {
        // Only add a new message is necessary
        if (!data.containsKey(message)) {
            val label = MessageLabel(msgCount++)
            data[message] = label
        }
    }

    fun getLabel(value: String) : String {
        val message: Message = Message(value)
        return data[message].toString()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        val sortedMap = data.toList().sortedBy { (_, v) -> v.toString() }.toMap()
        if (data.isNotEmpty()) {
            sb.append(".data\n\n")
            for ((message, label) in sortedMap) {
                sb.append("${label.toString()}:")
                sb.append("\n\t")
                sb.append(".word ${message.toString().filter{ it != '\\'}.length}")
                sb.append("\n\t")
                sb.append(".ascii \"$message\"")
                sb.append("\n")
            }
        }
        return sb.toString()
    }
}
