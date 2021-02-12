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

    //Get the base type of an array
    fun getBaseType(): Type {
        return this.arrType ?: Type(INVALID)
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

    // Returns true iff the type is a null pair
    private fun isNullPair():Boolean = getPair() && getPairFst() == null && getPairSnd() == null

    // Helper function for pairwise equality of pair types
    private fun comparePairElems(t1: Type?, t2: Type?): Boolean = when {
        t1 == null -> t2 == null || t2.getType() == PAIR_LITER
        t2 == null -> t1.getType() == PAIR_LITER
        t1.getPair() -> t2.getPair()
        else -> t1.getType() == t2.getType()
    }


    override fun hashCode(): Int {
        return super.hashCode()
    }

    //Equality for types for semantic checks
    override fun equals(other: Any?): Boolean {
//        println("this ${toString()}")
//        println("other ${other.toString()}")
        if (other is Type) {
            var compare: Type = other
//            println(compare)
            return when {
                //Char array and string are equivalent
                getArray() && !getBaseType().getArray() && getBaseType().getType() == CHAR && compare.getType() == STRING -> true
                compare.getArray() && compare.getBaseType().getType() == CHAR && !compare.getBaseType().getArray() && getType() == STRING -> true

                //Check array base types
                compare.getArray() && getArray() -> compare.getBaseType() == getBaseType()

                //compare.getType() == PAIR_LITER -> getType() == PAIR_LITER

                //Check pair types
                compare.getPair() && getPair() -> (comparePairElems(compare.getPairFst(), getPairFst()) &&
                                                   comparePairElems(compare.getPairSnd(), getPairSnd())) ||
                                                   isNullPair() || compare.isNullPair()

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
        val sb = StringBuilder()
        if (this.type == PAIR_LITER) {
            sb.append("PAIR_LITER")
            if (!isNullPair()) {
                sb.append('(')
                sb.append(getPairFst().toString())
                sb.append(',')
                sb.append(getPairSnd().toString())
                sb.append(')')
            }
            return sb.toString()
        }
        val symbolName = VOCABULARY.getSymbolicName(getType())
        /*if (getPair()) {
            //Return PAIR(<FstType>,<SndType>)
            sb.append(symbolName)
            if (!(getPairFst() == null && getPairSnd() == null)){
            sb.append('(')
            sb.append(getPairFst().toString())
            sb.append(',')
            sb.append(getPairSnd().toString())
            sb.append(')')}
            return sb.toString()
        }*/
        if (getArray()) {
            //Return <BaseType>[]
            sb.append(getBaseType().toString())
            sb.append("[]")
            return sb.toString()
        }
        //Return <Type>
        return symbolName
    }


}