package compiler.AST.Types

import AST.Types.*
import java.lang.StringBuilder

class TypeFunction(private val retType: Type?, val params: MutableCollection<Type>) : Type() {
    override val typeInt: Int = FUNCTION
    override var offsetInTable: Int = 0
    override var isParam = false

    fun getReturn(): Type? {
        return this.retType
    }

    override fun equals(other: Any?): Boolean {
        return if (other is TypeFunction) {
            if (params.size != other.params.size || typeInt != other.typeInt) return false
            this.params.zip(other.params).all { (x, y) -> x == y }
        } else {
            false
        }
    }


    override fun hashCode(): Int {
        var result = offsetInTable
        result = 31 * result + typeInt
        result = 31 * result + params.hashCode()
        return result
    }

    override fun toString(): String {
        return if (params.isEmpty()) "VOID" else params.joinToString(separator = "_").replace("[]","ARR")

    }

    init {
        for (parameter in params) {
            parameter.setParameter(true)
        }
    }
}