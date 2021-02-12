import org.antlr.v4.runtime.ParserRuleContext
import kotlin.Int
import kotlin.system.exitProcess

const val ERROR_CODE = 200
const val BASE_MSG = "Semantic Error at "
class SemanticErrorHandler {
    private val errorList: MutableCollection<String> = mutableListOf()

    fun hasSemanticErrors() = errorList.isNotEmpty()

    //Error message for accessing element of null pair
    fun nullTypeAccess(line: Int, charPosition: Int) {
        val line = errorLine()
        val char = errorChar()
        val msg = "accessing element of null pair"
        val fullMsg = buildErrorMessage(msg,line,char)

        errorList.add(fullMsg)
    }

    //Error message for incompatibleType
    fun incompatibleType(type: Type) {
        val line = errorLine()
        val char = errorChar()
        val msg = "Incompatible type $type"
        val fullMsg = buildErrorMessage(msg,line,char)

        errorList.add(fullMsg)
    }

    //Error message for an undefined variable
    fun undefinedVariable(ident: String) {
        val line = errorLine()
        val char = errorChar()
        val msg = "Variable $ident is not defined in this scope"
        val fullMsg = buildErrorMessage(msg,line,char)

        errorList.add(fullMsg)
    }
    //Error message for redefining variable within same scope
    fun redefinedVariable(ident: String, ctx: ParserRuleContext) {
        val msg = "\"$ident\" is already defined in this scope"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun undefinedType(ident: String, ctx: ParserRuleContext) {
        val msg = "\"$ident\" is not defined in this scope"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    //Error message for incompatible types in an expression
    fun incompatibleTypeDecl(expr: String, expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "Incompatible type declaration at $expr (expected: $expected, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun incompatibleTypeAss(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "Incompatible type assignment (expected: $expected, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun readTypeError(actual: String, ctx: ParserRuleContext) {
        val msg = "Incompatible type assignment at READ (expected: {INT, CHAR}, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun arrayIndex(ctx: ParserRuleContext) {
        val msg = "Array index is not an integer"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    //Error message for returning from global scope
    fun returnFromGlobal(){
        val line = errorLine()
        val char = errorChar()
        val msg = "Cannot return from the global scope."
        val fullMsg = buildErrorMessage(msg,line,char)

        errorList.add(fullMsg)
    }


    //Build a full error message given the message, line, and char of the error
    private fun buildErrorMessage(msg : String, line : Int, char : Int) : String{
        val sb = StringBuilder(BASE_MSG)
        sb.append(line)
        sb.append(':')
        sb.append(char)
        sb.append(" -- ")
        sb.append(msg)
        return sb.toString()
    }

    //Get the line of the error
    private fun errorLine() : Int{
        return 0
    }

    //Get the character of the error
    private fun errorChar() : Int{
        return 0
    }

    //Iterate through the list of errors and print out the error and the line and character where it occurs
    fun printSemanticErrors() {
        println("================================================================")
        println("==================== SEMANTIC ERRORS FOUND =====================")
        println("================================================================")

        errorList.forEach { println(it) }

        println("\n\n ${errorList.size} syntactic errors detected. No further compilation attempted.")
    }

}