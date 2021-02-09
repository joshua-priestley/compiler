import antlr.WACCParser
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import java.util.*
import kotlin.Int

enum class ErrorType {
    SYNTAX,
    SEMANTIC
}

class WACCErrorListener : BaseErrorListener() {

    val errorList = LinkedList<String>()

    override fun syntaxError(recognizer: Recognizer<*, *>?,
                             offendingSymbol: Any?,
                             line: Int, charPositionInLine: Int,
                             msg: String?, e: RecognitionException?) {

        errorList.add("Syntactic Error at $line:$charPositionInLine: $msg")
    }
}

