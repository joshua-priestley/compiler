import antlr.WACCParser.*
import kotlin.Int

const val INVALID: Int = -1
class Type {

    private val array: Boolean
    private val pair: Boolean
    private val type : Int
    private val pairFst : Type?
    private val pairSnd : Type?
    private var arrType : Type?

    //Constructor for arrayTypes
    constructor(arrType : Type){
        this.type = arrType.getType()
        this.arrType = arrType
        this.array = true
        this.pairFst = null
        this.pairSnd = null
        this.pair = false
    }

    //Constructor for pair types
    constructor(type1 : Type, type2: Type) {
        this.type = PAIR
        this.pairFst = type1
        this.pairSnd = type2
        this.pair = true
        this.array = false
        this.arrType = null
    }

    //Constructor for singleton types
    constructor(type: Int) {
        this.type = type
        this.array = false
        this.pair = false
        this.pairFst = null
        this.pairSnd = null
        this.arrType = null
    }

    //Get the type of a binary operator
    fun binaryOps(operator: Int): Type {
        when {
            //Tokens 1-5 are int operators
            operator <= 5 -> return Type(INT)
            //Tokens 6-13 are bool operators
            operator in 6..13 -> return Type(BOOL)
        }
        return Type(INVALID)
    }

    //Get the type of a unary operator
    fun UnaryOps(operator: Int): Type {
        when (operator) {
            NOT -> return Type(BOOL)
            LEN, ORD, MINUS -> return Type(INT)
            CHR -> return Type(CHAR)
        }
        return Type(INVALID)
    }

    //Get the base type of an array
    fun getBaseType(): Type {
        return this.arrType!!
    }

    //Get the type value of a single type
    fun getType(): Int {
            return type
        }

    //Get the type of the fst of a pair
    fun getPairFst() : Type {
        return pairFst ?: Type(INVALID)
    }

    //Get the type of the snd of a pair
    fun getPairSnd() : Type {
        return pairSnd ?: Type(INVALID)
    }

    fun getArray() : Boolean {
        return this.array
    }

    fun getPair() : Boolean {
        return this.pair
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    //Equality for types for semantic checks
    override fun equals(other: Any?): Boolean {
        if (other is Type) {
            var compare: Type = other as Type
            return when {
                //Char array and string are equivalent
                getArray() && !getBaseType().getArray() && getBaseType().getType() == CHAR && compare.getType() == STRING -> true
                compare.getArray() && compare.getBaseType().getType() == CHAR && !compare.getBaseType().getArray() && getType() == STRING -> true

                //Check array base types
                compare.getArray() && getArray() && compare.getBaseType() == getBaseType() -> true

                //Check pair types
                compare.getPair() && getPair() && compare.getPairFst() == getPairFst() && compare.getPairSnd() == getPairSnd() -> true

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
        val symbolName = VOCABULARY.getSymbolicName(getType())
        val sb = StringBuilder()
        if (pair){
            //Return PAIR(<FstType>,<SndType>)
            sb.append(symbolName)
            sb.append('(')
            sb.append(getPairFst().toString())
            sb.append(',')
            sb.append(getPairSnd().toString())
            sb.append(')')
            return sb.toString()
        }
        if (array){
            //Return <BaseType>[]
            sb.append(getBaseType().toString())
            sb.append("[]")
            return sb.toString()
        }
        //Return <Type>
        return symbolName
    }


}