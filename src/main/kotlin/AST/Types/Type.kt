package AST.Types

import antlr.WACCParser.*
import compiler.AST.Types.TypeArray
import compiler.AST.Types.TypeBase
import compiler.AST.Types.TypePair
import kotlin.Int

const val INVALID = -1
const val ARRAY = -2
const val ANY = -3
const val PAIR_LITER = -5
const val FUNCTION = -6
const val EMPTY_ARR = -7
const val STRUCT = -8

abstract class Type {
    open val typeInt: Int = ANY
    open var offsetInTable: Int = 0
    open var isParam: Boolean = false
    private var isReturn: Boolean = false

    fun isParameter(): Boolean {
        return isParam
    }

    fun setReturn(bool: Boolean): Type {
        this.isReturn = bool
        return this
    }

    fun isReturn(): Boolean {
        return this.isReturn
    }

    fun setParameter(bool: Boolean): Type {
        this.isParam = bool
        return this
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
        return (this is TypeArray)
    }

    fun getPair(): Boolean {
        return this is TypePair
    }

    open fun getTypeSize(): Int {
        return when (this.typeInt) {
            STRING -> 4
            INT -> 4
            BOOL -> 1
            CHAR -> 1
            ARRAY -> 4
            PAIR_LITER -> 4
            else -> 0
        }
    }

    companion object {
        // Get the type a binary operator produces
        fun binaryOpsProduces(operator: Int): Type {
            return when {
                //Tokens 1-5 are int operators
                operator <= 5 -> TypeBase(INT)
                //Tokens 6-13 are bool operators
                operator in 6..13 -> TypeBase(BOOL)
                else -> TypeBase(INVALID)
            }
        }

        // Get the type a binary operator requires
        fun binaryOpsRequires(operator: Int): List<Type> {
            return when {
                operator < 6 -> mutableListOf(TypeBase(INT))
                operator in 6..9 -> mutableListOf(TypeBase(INT), TypeBase(CHAR))
                operator in 10..11 -> mutableListOf(TypeBase(ANY)) // EQ and NEQ can take any type
                operator in 12..14 -> mutableListOf(TypeBase(BOOL))
                operator in 12..14 -> mutableListOf(TypeBase(BOOL))
                else -> mutableListOf(TypeBase(INVALID))
            }
        }

        // Get the type a unary operator produces
        fun unaryOpsProduces(operator: Int): Type {
            return when (operator) {
                NOT -> TypeBase(BOOL)
                LEN, ORD, MINUS -> TypeBase(INT)
                CHR -> TypeBase(CHAR)
                else -> TypeBase(INVALID)
            }
        }

        // Get the type a unary operator requires
        fun unaryOpsRequires(operator: Int): Type {
            return when (operator) {
                NOT -> TypeBase(BOOL)
                ORD -> TypeBase(CHAR)
                MINUS, CHR -> TypeBase(INT)
                LEN -> TypeBase(ARRAY)
                else -> TypeBase(INVALID)
            }
        }
    }


}