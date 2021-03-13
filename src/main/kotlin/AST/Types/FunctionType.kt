package compiler.AST.Types

import AST.Types.Type

class FunctionType(override var offsetInTable: Int, override val typeInt: Int, val params : List<Type>) : Type {

    override var isParam = false
    override fun equals(other: Any?): Boolean {
        return if (other is FunctionType){
            val equal = true
            if (params.size != other.params.size || typeInt != other.typeInt) return false
            this.params.zip(other.params).all {(x,y) -> x == y}
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

    init {
        for (parameter in params){
            parameter.setParameter(true)
        }
    }
}