package compiler.AST.Types

import AST.Types.Type
import antlr.WACCParser.*
import java.lang.StringBuilder

class TypePair(type1: Type?, type2: Type?) : Type() {


    private val pairFst: Type? = type1
    private val pairSnd: Type? = type2
    override var isParam = false

    override fun getPairFst(): Type? {
        return this.pairFst
    }

    override fun getPairSnd(): Type? {
        return this.pairSnd
    }

    override fun getTypeSize(): Int {
        return 4
    }

    override fun toString(): String {
        if (getPairFst() == null && getPairSnd() == null) {
            return "PAIR"
        } else {
            val sb = StringBuilder()
            //Return PAIR(<FstType>,<SndType>)
            sb.append("PAIR")
            if (!(getPairFst() == null && getPairSnd() == null)) {
                sb.append('(')
                sb.append(getPairFst().toString())
                sb.append(',')
                sb.append(getPairSnd().toString())
                sb.append(')')
            }
            return sb.toString()
        }
    }

    override fun hashCode(): Int {
        var result = getType()
        result = 31 * result + (getPairFst()?.hashCode() ?: 0)
        result = 31 * result + (getPairSnd()?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TypePair) return false
        return when {
            other.getPair() && getPair() -> (other.getPairFst() == getPairFst() && other.getPairSnd() == getPairSnd()) ||
                    ((other.getPairFst() == null && other.getPairSnd() == null) || (getPairFst() == null && getPairSnd() == null))
            else -> false
        }
    }
}