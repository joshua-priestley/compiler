import AST.Types.PAIR_LITER
import antlr.WACCParser.*
import compiler.AST.Types.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class TestTypes {

    // Base types
    private val chr = TypeBase(CHAR)
    private val bool = TypeArray(TypeBase(BOOL))
    private val int = TypeBase(INT)
    private val i1 = TypeBase(INT)
    private val str = TypeBase(STRING)
    private val s1 = TypeBase(STRING)

    @Test
    fun testBaseEquals() {
        val s1 = TypeBase(STRING)
        assertEquals(s1, str)
        assertNotEquals(chr, bool)
        assertNotEquals(int, chr)
        assertNotEquals(str, int)
    }

    // Non nested pair types are equal if pairwise types are the same
    @Test
    fun testPairEquals() {
        val t1 = TypePair(str, int)
        val t2 = TypePair(s1, i1)
        val t3 = TypePair(str, s1)
        val t4 = TypePair(int, str)
        assertEquals(t1, t2)
        assertNotEquals(t2, t3)
        assertNotEquals(t3, t4)
    }

    // Character arrays should be the same type as strings
    @Test
    fun testCharArrayStringEquals() {
        val t1 = TypeArray(chr)
        assertEquals(str, t1)
    }

    // Null pairs should match any pair type
    @Test
    fun testPairLiterEquals() {
        val p1 = TypePair(null,null)
        val p2 = TypePair(null,null)
        val p3 = TypePair(int, int)
        assertEquals(p1, p2)
        assertEquals(p1, p3)
    }

    // Array types are equal if their element types are equal
    @Test
    fun testArrayEquals() {
        val t1 = TypeArray(str)
        val t2 = TypeArray(int)
        val t3 = TypeArray(s1)
        assertEquals(t1, t3)
        assertNotEquals(t1, t2)
    }

    // Nested array types are equal if their element types are equal
    @Test
    fun testNestedArrayEquals() {
        val t1 = TypeArray(TypeArray(str))
        val t2 = TypeArray(TypeArray(int))
        val t3 = TypeArray(TypeArray(s1))
        assertEquals(t1, t3)
        assertNotEquals(t1, t2)
    }

    // The inner types of a nested pair should not be considered when looking at equality
    @Test
    fun testNestedPairEquals() {
        val p1 = TypePair(str, int)
        val p2 = TypePair(s1, i1)
        val p3 = TypePair(str, str)
        val np1 = TypePair(p1, p1)
        val np2 = TypePair(p1, p2)
        val np3 = TypePair(p3, p3)
        assertEquals(np1, np2)
        assertNotEquals(np2, np3)
        assertNotEquals(np1, np3)
    }
}
