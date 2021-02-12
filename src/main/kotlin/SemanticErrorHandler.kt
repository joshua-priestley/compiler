import antlr.WACCParser
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
    fun undefinedVariable(type: String, ident: String) {
        val line = errorLine()
        val char = errorChar()
        val msg = "$type $ident is not defined in this scope"
        val fullMsg = buildErrorMessage(msg,line,char)

        errorList.add(fullMsg)
    }

    fun mismatchedArgs(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "Number of function arguments does not match (expected: $expected, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun mismatchedParamTypes(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "Mismatched paramater types (expected: $expected, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun funRefBeforeAss(expr: String, ctx: ParserRuleContext) {
        val msg = "Function referenced before assignment at $expr"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun newPairFalse(pairIndex: String, ctx: ParserRuleContext) {
        val msg = "newpair expression $pairIndex is false"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun incompatibleExitCode(expr: String, actual: String, ctx: ParserRuleContext) {
        val msg = "Incompatible exit code at $expr (expected: INT, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    //Error message for redefining variable within same scope
    fun redefinedVariable(ident: String, ctx: ParserRuleContext) {
        val msg = "\"$ident\" is already defined in this scope"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun assigningFunction(ident: String, ctx: ParserRuleContext) {
        val msg = "\"$ident\" is a function, cannot assign a function a value"
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

    fun incompatibleTypeReturn(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "Incompatible return type (expected: $expected, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun incompatibleTypeFree(actual: String, ctx: ParserRuleContext) {
        val msg = "Incompatible return type (expected: Pair, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun readNotVariable(ctx: ParserRuleContext) {
        val msg = "Read must be stored in a variable"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun readTypeError(actual: String, ctx: ParserRuleContext) {
        val msg = "Incompatible type assignment at READ (expected: {INT, CHAR}, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun arrayIndex(index: String, expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "Array index at $index is invalid (expected: $expected, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,ctx.getStart().line,ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun binaryOpType(ctx: ParserRuleContext) {
        val msg = "Incompatible types for binary operator"
        val fullMsg = buildErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun indexStrings(ctx: ParserRuleContext) {
        val msg = "Strings cannot be indexed"
        val fullMsg = buildErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    fun conditionalBoolean(ctx: ParserRuleContext) {
        val msg = "Conditional expression must be of type boolean"
        val fullMsg = buildErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)

        errorList.add(fullMsg)
    }

    //Error message for returning from global scope
    fun returnFromGlobal(ctx: ParserRuleContext){
        val msg = "Cannot return from the global scope."
        val fullMsg = buildErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)

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