import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

internal class ThingTest {
    private val thing = Thing()

    @Test
    fun getX() {
        assertEquals(thing.x, 3)
    }

    @Test
    fun setX() {
        assertEquals(thing.x, 3)
        thing.x = 4
        assertEquals(thing.x, 4)
    }
}