import compiler.Compiler
import antlr.WACCParser.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class TestTypes {

    // Base types
    private val chr = Type(CHAR)
    private val bool = Type(BOOL)
    private val int = Type(INT)
    private val i1 = Type(INT)
    private val str = Type(STRING)
    private val s1 = Type(STRING)

    @Test
    fun testBaseEquals() {
        val s1 = Type(STRING)
        assertEquals(s1, str)
        assertNotEquals(chr, bool)
        assertNotEquals(int, chr)
        assertNotEquals(str, int)
    }

    // Non nested pair types are equal if pairwise types are the same
    @Test
    fun testPairEquals() {
        val t1 = Type(str, int)
        val t2 = Type(s1, i1)
        val t3 = Type(str, s1)
        val t4 = Type(int, str)
        assertEquals(t1, t2)
        assertNotEquals(t2, t3)
        assertNotEquals(t3, t4)
    }

    // Character arrays should be the same type as strings
    @Test
    fun testCharArrayStringEquals() {
        val t1 = Type(chr)
        assertEquals(str, t1)
    }

    // Null pairs should match any pair type
    @Test
    fun testPairLiterEquals() {
        val p1 = Type(PAIR_LITER)
        val p2 = Type(PAIR_LITER)
        val p3 = Type(int, int)
        assertEquals(p1, p2)
        assertEquals(p1, p3)
    }

    // Array types are equal if their element types are equal
    @Test
    fun testArrayEquals() {
        val t1 = Type(str)
        val t2 = Type(int)
        val t3 = Type(s1)
        assertEquals(t1, t3)
        assertNotEquals(t1, t2)
    }

    // Nested array types are equal if their element types are equal
    @Test
    fun testNestedArrayEquals() {
        val t1 = Type(Type(str))
        val t2 = Type(Type(int))
        val t3 = Type(Type(s1))
        assertEquals(t1, t3)
        assertNotEquals(t1, t2)
    }

    // The inner types of a nested pair should not be considered when looking at equality
    @Test
    fun testNestedPairEquals() {
        val p1 = Type(str, int)
        val p2 = Type(s1, i1)
        val p3 = Type(str, str)
        val np1 = Type(p1, p1)
        val np2 = Type(p1, p2)
        val np3 = Type(p3, p3)
        assertEquals(np1, np2)
        assertEquals(np2, np3)
        assertEquals(np1, np3)
    }
}
