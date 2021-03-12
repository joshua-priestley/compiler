package compiler.AST.Types

import AST.Types.ARRAY
import AST.Types.Type

class ArrayType(override var arrType: BaseType): Type {

    override val type: Int = ARRAY
    override val pairFst: Type? = null
    override val pairSnd: Type? = null
    override var function: Boolean = false
    override var parameter: Boolean = false

    override var offsetInTable: Int = 0

    override fun getArray(): Boolean {
        return true
    }

    override fun getBaseType(): BaseType {
        return arrType
    }
}