import antlr.WACCParser
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import java.util.*
import kotlin.Int


class WACCErrorListener : BaseErrorListener() {
    override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
        val stack: List<String?> = (recognizer as WACCParser).getRuleInvocationStack()
        Collections.reverse(stack)
        System.err.println("rule stack: $stack")
        System.err.println("line " + line + ":" + charPositionInLine + " at " +
                offendingSymbol + ": " + msg)
    }
}