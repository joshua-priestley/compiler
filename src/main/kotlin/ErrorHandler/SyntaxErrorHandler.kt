package ErrorHandler

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import kotlin.Int


class SyntaxErrorHandler : BaseErrorListener() {

    private val syntaxErrorList = mutableSetOf<String>()

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
        syntaxErrorList.forEach { println(it) }

        println("\n${syntaxErrorList.size} syntactic errors detected. No further compilation attempted.")
    }
}

