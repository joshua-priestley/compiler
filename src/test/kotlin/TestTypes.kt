import AST.Types.PAIR_LITER
import antlr.WACCParser.*
import compiler.AST.Types.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class TestTypes {

    // Base types
    private val chr = BaseType(CHAR)
    private val bool = ArrayType(BaseType(BOOL))
    private val int = BaseType(INT)
    private val i1 = BaseType(INT)
    private val str = BaseType(STRING)
    private val s1 = BaseType(STRING)

    @Test
    fun testBaseEquals() {
        val s1 = BaseType(STRING)
        assertEquals(s1, str)
        assertNotEquals(chr, bool)
        assertNotEquals(int, chr)
        assertNotEquals(str, int)
    }

    // Non nested pair types are equal if pairwise types are the same
    @Test
    fun testPairEquals() {
        val t1 = PairType(str, int)
        val t2 = PairType(s1, i1)
        val t3 = PairType(str, s1)
        val t4 = PairType(int, str)
        assertEquals(t1, t2)
        assertEquals(t2, t3)
        assertEquals(t3, t4)
    }

    // Character arrays should be the same type as strings
    @Test
    fun testCharArrayStringEquals() {
        val t1 = ArrayType(chr)
        assertEquals(str, t1)
    }

    // Null pairs should match any pair type
    @Test
    fun testPairLiterEquals() {
        val p1 = BaseType(PAIR_LITER)
        val p2 = BaseType(PAIR_LITER)
        val p3 = PairType(int, int)
        assertEquals(p1, p2)
        assertEquals(p1, p3)
    }

    // Array types are equal if their element types are equal
    @Test
    fun testArrayEquals() {
        val t1 = ArrayType(str)
        val t2 = ArrayType(int)
        val t3 = ArrayType(s1)
        assertEquals(t1, t3)
        assertNotEquals(t1, t2)
    }

    // Nested array types are equal if their element types are equal
    @Test
    fun testNestedArrayEquals() {
        val t1 = ArrayType(ArrayType(str))
        val t2 = ArrayType(ArrayType(int))
        val t3 = ArrayType(ArrayType(s1))
        assertEquals(t1, t3)
        assertNotEquals(t1, t2)
    }

    // The inner types of a nested pair should not be considered when looking at equality
    @Test
    fun testNestedPairEquals() {
        val p1 = PairType(str, int)
        val p2 = PairType(s1, i1)
        val p3 = PairType(str, str)
        val np1 = PairType(p1, p1)
        val np2 = PairType(p1, p2)
        val np3 = PairType(p3, p3)
        assertEquals(np1, np2)
        assertEquals(np2, np3)
        assertEquals(np1, np3)
    }
}
