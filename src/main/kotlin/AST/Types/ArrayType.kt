package compiler.AST.Types

import antlr.WACCParser.*
import AST.Types.Type
import java.lang.StringBuilder
const val ARRAY = -2
open class ArrayType(var arrType: Type, override val typeInt: Int = ARRAY) : Type() {

    override var isParam = false
    override var offsetInTable: Int = 0

    override fun getArray(): Boolean {
        return true
    }

    override fun getBaseType(): Type {
        return arrType
    }

    override fun toString(): String {
        val sb = StringBuilder()
        //Return <AST.BaseType>[]
        sb.append(getBaseType().toString())
        sb.append("[]")
        return sb.toString()
    }

    override fun hashCode(): Int {
        var result = getType()
        result = 31 * result + arrType.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Type) return false
        return when {
            !other.getArray() && !getBaseType().getArray() && getBaseType().getType() == CHAR && other.getType() == STRING -> true
            other.getArray() -> other.getBaseType() == getBaseType()
            else -> false
        }
    }
}