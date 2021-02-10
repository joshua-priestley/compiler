import antlr.WACCParser.*

class Type {
    public final var INVALID: kotlin.Int = -1
    private var array: Boolean = false
    private var pair: Boolean = false
    private var type1: kotlin.Int
    private var type2: kotlin.Int = 0


    constructor(type1: kotlin.Int, type2: kotlin.Int) {
        this.type1 = type1
        this.type2 = type2
        this.pair = true
    }

    constructor(type: kotlin.Int) {
        this.type1 = type
    }

    fun binaryOps(operator: kotlin.Int): Type {
        when {
            operator <= 5 -> return Type(INT)
            operator in 6..13 -> return Type(BOOL)
        }
        return Type(INVALID)
    }

    fun UnaryOps(operator: kotlin.Int): Type {
        when (operator) {
            NOT -> return Type(BOOL)
            LEN, ORD -> return Type(INT)
            CHR -> return Type(CHAR)
        }
        return Type(INVALID)
    }

    fun setArray(array: Boolean) {
        this.array = array;
    }

    fun getBaseType(): Type {
        val base = Type(this.type1, this.type2)
        base.array = false
        base.pair = this.pair
        return base
    }

    fun getType(): kotlin.Int {
        return if (pair) {
            PAIR
        } else {
            return type1
        }
    }

    fun getType2() : kotlin.Int {
        return type2
    }

    fun getArray() : Boolean {
        return this.array
    }

    fun getPair() : Boolean {
        return this.pair
    }

    override fun equals(other: Any?): Boolean {
        if (other is Type) {
            var compare: Type = other as Type
            return when {
                //Char array and string are equivalent
                compare.getArray() && compare.getBaseType().getType() == CHAR && getType() == STRING -> true
                //Check array base types
                compare.getArray() && getArray() && compare.getBaseType() == getBaseType() -> true
                //Check basic types
                !compare.getPair() && !getPair() && compare.getType() == getType() -> true
                //Check pair types
                compare.getPair() && getPair() && compare.getType() == getType() && compare.getType2() == getType2() -> true
                else -> false

            }
        } else {
            return false
        }
    }

    override fun toString(): String {
        return VOCABULARY.getSymbolicName(getType())
    }


}