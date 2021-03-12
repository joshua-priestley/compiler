package AST.Types

import antlr.WACCParser.*
import compiler.AST.Types.BaseType
import kotlin.Int

const val INVALID = -1
const val ARRAY = -2
const val ANY = -3
const val EMPTY_ARR = -4
const val PAIR_LITER = -5

interface Type {

    var offsetInTable: Int

    fun setFunction(function: Boolean): Type {
        this.function = function
        return this
    }

    fun isFunction(): Boolean {
        return false
    }

    fun setParameter(parameter: Boolean): Type {
        this.parameter = parameter
        return this
    }

    fun isParameter(): Boolean {
        return this.parameter
    }

    fun setOffset(o: Int): Type {
        this.offsetInTable = o
        return this
    }

    fun getOffset(): Int {
        return this.offsetInTable
    }

    //Get the base type of an array
    fun getBaseType(): Type {
        return if (this.arrType == null) this else this.arrType!!.getBaseType()
    }

    //Get the type value of a single type

    //Get the type of the fst of a pair

    //Get the type of the snd of a pair

    fun getArray(): Boolean {
        return false
    }

    fun getPair(): Boolean {
        return (this.type == PAIR_LITER)
    }

    fun getTypeSize(): Int {
        return when (this.type) {
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

    //Equality for types for semantic checks
    override fun equals(other: Any?): Boolean {
        if (other is Type) {
            val compare: Type = other
            return when {
                //Char array and string are equivalent
                getArray() && !getBaseType().getArray() && getBaseType().getType() == CHAR && compare.getType() == STRING -> true
                compare.getArray() && compare.getBaseType().getType() == CHAR && !compare.getBaseType()
                        .getArray() && getType() == STRING -> true

                //Check array base types
                compare.getArray() && getArray() -> compare.getBaseType() == getBaseType()


                compare.getPair() -> getPair()
                //Check pair types
                compare.getPair() && getPair() -> compare.getPairFst() == getPairFst() && compare.getPairSnd() == getPairSnd()

                compare.getPair() && getPair() -> ((compare.getPairFst() == null && compare.getPairSnd() == null) || (getPairFst() == null && getPairSnd() == null))

                //Check basic types
                compare.getType() == getType() -> true

                else -> false

            }
        } else {
            return false
        }
    }

    //Convert a type to a string for the printing of error messages
    override fun toString(): String {
        if (this.type == PAIR_LITER) {
            return "AST.PAIR_LITER"
        }
        val symbolName = VOCABULARY.getSymbolicName(getType())
        val sb = StringBuilder()
        if (getPair()) {
            //Return PAIR(<FstType>,<SndType>)
            sb.append(symbolName)
            if (!(getPairFst() == null && getPairSnd() == null)) {
                sb.append('(')
                sb.append(getPairFst().toString())
                sb.append(',')
                sb.append(getPairSnd().toString())
                sb.append(')')
            }
            return sb.toString()
        }
        if (getArray()) {
            //Return <AST.BaseType>[]
            sb.append(getBaseType().toString())
            sb.append("[]")
            return sb.toString()
        }
        //Return <Type>
        return symbolName
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + (pairFst?.hashCode() ?: 0)
        result = 31 * result + (pairSnd?.hashCode() ?: 0)
        result = 31 * result + (arrType?.hashCode() ?: 0)
        result = 31 * result + function.hashCode()
        return result
    }


}