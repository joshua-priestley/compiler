
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import java.util.*
import kotlin.Int



class WACCErrorListener : BaseErrorListener() {

    private val syntaxErrorList = LinkedList<String>()

    override fun syntaxError(recognizer: Recognizer<*, *>?,
                             offendingSymbol: Any?,
                             line: Int, charPositionInLine: Int,
                             msg: String?, e: RecognitionException?) {

        syntaxErrorList.add("Syntactic Error at $line:$charPositionInLine: $msg")
    }

    fun hasSyntaxErrors() = syntaxErrorList.isNotEmpty()

    private fun errorPosition(ctx: ParserRuleContext): String {
        return "${ctx.getStart().line}:${ctx.getStart().charPositionInLine}"
    }

    fun addSyntaxError(ctx: ParserRuleContext, msg: String) {
        syntaxErrorList.add("Syntactic Error at ${errorPosition(ctx)}: $msg")
    }

    fun printSyntaxErrors() {
        println("================================================================")
        println("==================== SYNTACTIC ERRORS FOUND ====================")
        println("================================================================")

        syntaxErrorList.forEach { println(it) }

        println("\n\n ${syntaxErrorList.size} syntactic errors detected. No further compilation attempted.")
    }
}
