package compiler.AST.Types

import AST.Types.Type

class BaseType(override val type: Int) : Type {

    override val pairFst: Type? = null
    override val pairSnd: Type? = null
    override var arrType: Type? = null
    override var function: Boolean = false
    override var parameter: Boolean = false

    override var offsetInTable: Int = 0

    override fun getBaseType(): Type {
        return this
    }

}