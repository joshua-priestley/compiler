import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TestMain {
    @Test
    @DisplayName("Random test...")
    fun check() {
        val thing = Main()
        assert(thing.x == 3)
    }
}