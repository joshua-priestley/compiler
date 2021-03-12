package AST

import antlr.WACCParser.*
import kotlin.Int

const val INVALID = -1
const val ARRAY = -2
const val ANY = -3
const val EMPTY_ARR = -4
const val PAIR_LITER = -5

class Type {

    private val type: Int
    private val pairFst: Type?
    private val pairSnd: Type?
    private var arrType: Type?
    private var function: Boolean = false
    private var parameter: Boolean = false

    private var offsetInTable: Int = 0


    //Constructor for singleton types
    constructor(type: Int) {
        this.type = type
        this.pairFst = null
        this.pairSnd = null
        this.arrType = null
    }

    //Constructor for arrayTypes
    constructor(arrType: Type) {
        this.type = ARRAY
        this.arrType = arrType
        this.pairFst = null
        this.pairSnd = null
    }

    //Constructor for pair types
    constructor(type1: Type, type2: Type) {
        this.type = PAIR_LITER
        this.pairFst = type1
        this.pairSnd = type2
        this.arrType = null
    }

    fun setFunction(function: Boolean): Type {
        this.function = function
        return this
    }

    fun isFunction(): Boolean {
        return this.function
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
    fun getType(): Int {
        return type
    }

    //Get the type of the fst of a pair
    fun getPairFst(): Type? {
        return pairFst
    }

    //Get the type of the snd of a pair
    fun getPairSnd(): Type? {
        return pairSnd
    }

    fun getArray(): Boolean {
        return (this.type == ARRAY)
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
                operator <= 5 -> Type(INT)
                //Tokens 6-13 are bool operators
                operator in 6..13 -> Type(BOOL)
                operator in 22..23 -> Type(INT)
                else -> Type(INVALID)
            }
        }

        // Get the type a binary operator requires
        fun binaryOpsRequires(operator: Int): List<Type> {
            return when {
                operator < 6 -> mutableListOf(Type(INT))
                operator in 6..9 -> mutableListOf(Type(INT), Type(CHAR))
                operator in 10..11 -> mutableListOf(Type(ANY)) // EQ and NEQ can take any type
                operator in 12..14 -> mutableListOf(Type(BOOL))
                operator in 12..14 -> mutableListOf(Type(BOOL))
                operator in 22..23 -> mutableListOf(Type(INT))
                else -> mutableListOf(Type(INVALID))
            }
        }

        // Get the type a unary operator produces
        fun unaryOpsProduces(operator: Int): Type {
            return when (operator) {
                NOT -> Type(BOOL)
                LEN, ORD, MINUS -> Type(INT)
                CHR -> Type(CHAR)
                BITWISENOT -> Type(INT)
                else -> Type(INVALID)
            }
        }

        // Get the type a unary operator requires
        fun unaryOpsRequires(operator: Int): Type {
            return when (operator) {
                NOT -> Type(BOOL)
                ORD -> Type(CHAR)
                MINUS, CHR -> Type(INT)
                LEN -> Type(ARRAY)
                BITWISENOT -> Type(INT)
                else -> Type(INVALID)
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