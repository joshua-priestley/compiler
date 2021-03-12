package compiler.AST.Types

import AST.Types.PAIR_LITER
import AST.Types.Type

class PairType(type1: Type, type2: Type) : Type {


    private val pairFst: Type = type1
    private val pairSnd: Type = type2

    init {
        this.type = PAIR_LITER
        this.arrType = null
    }
}