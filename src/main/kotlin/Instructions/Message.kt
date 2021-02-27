package compiler.Instructions

//TODO do we need this or can we just represent messages as strings?
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