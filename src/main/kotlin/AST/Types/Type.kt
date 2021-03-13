package AST.Types

import antlr.WACCParser.*
import compiler.AST.Types.ArrayType
import compiler.AST.Types.BaseType
import compiler.AST.Types.PairType
import kotlin.Int

const val INVALID = -1
const val ARRAY = -2
const val ANY = -3
const val PAIR_LITER = -5

abstract class Type {
    open val typeInt : Int = ANY
    open var offsetInTable: Int = 0
    open var isParam: Boolean = false
    fun isParameter(): Boolean {
        return isParam
    }

    fun setParameter(bool: Boolean) {
        this.isParam = bool
    }

    fun getType(): Int {
        return this.typeInt
    }

    open fun getPairFst(): Type? {
        return null
    }

    open fun getPairSnd(): Type? {
        return null
    }


    fun isFunction(): Boolean {
        return false
    }

    fun setOffset(o: Int): Type {
        this.offsetInTable = o
        return this
    }

    fun getOffset(): Int {
        return this.offsetInTable
    }

    //Get the base type of an array
    open fun getBaseType(): Type {
        return this
    }

    //Get the type value of a single type

    //Get the type of the fst of a pair

    //Get the type of the snd of a pair

    open fun getArray(): Boolean {
        return (this is ArrayType)
    }

    fun getPair(): Boolean {
        return this is PairType
    }

    fun getTypeSize(): Int {
        return when (this.typeInt) {
            STRING -> 4
            INT -> 4
            BOOL -> 1
            CHAR -> 1
            ARRAY -> 4
            PAIR_LITER -> 4
            else -> -1
        }
    }

    companion object {
        // Get the type a binary operator produces
        fun binaryOpsProduces(operator: Int): Type {
            return when {
                //Tokens 1-5 are int operators
                operator <= 5 -> BaseType(INT)
                //Tokens 6-13 are bool operators
                operator in 6..13 -> BaseType(BOOL)
                else -> BaseType(INVALID)
            }
        }

        // Get the type a binary operator requires
        fun binaryOpsRequires(operator: Int): List<Type> {
            return when {
                operator < 6 -> mutableListOf(BaseType(INT))
                operator in 6..9 -> mutableListOf(BaseType(INT), BaseType(CHAR))
                operator in 10..11 -> mutableListOf(BaseType(ANY)) // EQ and NEQ can take any type
                operator in 12..14 -> mutableListOf(BaseType(BOOL))
                operator in 12..14 -> mutableListOf(BaseType(BOOL))
                else -> mutableListOf(BaseType(INVALID))
            }
        }

        // Get the type a unary operator produces
        fun unaryOpsProduces(operator: Int): Type {
            return when (operator) {
                NOT -> BaseType(BOOL)
                LEN, ORD, MINUS -> BaseType(INT)
                CHR -> BaseType(CHAR)
                else -> BaseType(INVALID)
            }
        }

        // Get the type a unary operator requires
        fun unaryOpsRequires(operator: Int): Type {
            return when (operator) {
                NOT -> BaseType(BOOL)
                ORD -> BaseType(CHAR)
                MINUS, CHR -> BaseType(INT)
                LEN -> BaseType(ARRAY)
                else -> BaseType(INVALID)
            }
        }
    }


}