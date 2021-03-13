package compiler.AST.Types

import AST.Types.Type
import antlr.WACCParser.*
class BaseType(override val typeInt: Int) :  Type() {


    override var isParam = false
    override var offsetInTable: Int = 0

    override fun getBaseType(): Type {
        return this
    }

    override fun toString(): String {
        return VOCABULARY.getSymbolicName(getType())
    }

    override fun hashCode(): Int {
        return getType()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BaseType) return false
        return other.getType() == getType()
    }
}