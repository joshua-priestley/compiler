import org.antlr.v4.runtime.ParserRuleContext
import kotlin.Int

class SemanticErrorHandler {
    private val errorList: MutableCollection<String> = mutableListOf()

    fun hasSemanticErrors() = errorList.isNotEmpty()
    //Error message for trying to use fst or snd on a non-pair type
    fun fstSndMustBePair(ctx: ParserRuleContext) {
        val msg = "when calling fst or snd, must be a pair variable"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for incorrect number of function args
    fun mismatchedArgs(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "number of function arguments does not match (expected: $expected, actual: $actual)"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for incorrect parameter types
    fun mismatchedParamTypes(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "mismatched paramater types (expected: $expected, actual: $actual)"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for calling a function before assignment
    fun funRefBeforeAss(expr: String, ctx: ParserRuleContext) {
        val msg = "function referenced before assignment at $expr"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for incorrect expression passed to newpair
    fun newPairFalse(pairIndex: String, ctx: ParserRuleContext) {
        val msg = "newpair expression $pairIndex is false"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for non-int exit code
    fun incompatibleExitCode(expr: String, actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible exit code at $expr (expected: INT, actual: $actual)"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for redefining variable within same scope
    fun redefinedVariable(ident: String, ctx: ParserRuleContext) {
        val msg = "\"$ident\" is already defined in this scope"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for trying to assign a function to a value
    fun assigningFunction(ident: String, ctx: ParserRuleContext) {
        val msg = "\"$ident\" is a function, cannot assign a value to a function"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for undefined variable
    fun undefinedVar(ident: String, ctx: ParserRuleContext) {
        val msg = "\"$ident\" is not defined in this scope"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for incompatible types in an expression
    fun incompatibleTypeDecl(expr: String, expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible type declaration at $expr (expected: $expected, actual: $actual)"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for type mismatch in assignment
    fun incompatibleTypeAss(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible type assignment (expected: $expected, actual: $actual)"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for incorrect return type
    fun incompatibleTypeReturn(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible return type (expected: $expected, actual: $actual)"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for freeing non-pair type
    fun incompatibleTypeFree(actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible return type (expected: Pair, actual: $actual)"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for array with inconsistent types
    fun arrayDifferingTypes(array: String, ctx: ParserRuleContext) {
        val msg = "array $array has differing element types"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for not reading into a variable
    fun readNotVariable(ctx: ParserRuleContext) {
        val msg = "read must be stored in a variable"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for trying to read from incorrect type
    fun readTypeError(actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible type assignment at READ (expected: {INT, CHAR}, actual: $actual)"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for invalid array index
    fun arrayIndex(index: String, expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "array index at $index is invalid (expected: $expected, actual: $actual)"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for passing incorrect types to binary operator
    fun binaryOpType(ctx: ParserRuleContext) {
        val msg = "incompatible types for binary operator"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for indexing strings
    fun indexStrings(ctx: ParserRuleContext) {
        val msg = "strings cannot be indexed"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for none boolean condition
    fun conditionalBoolean(actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible type for conditional expression (expected: BOOL, actual: $actual)"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }

    //Error message for returning from global scope
    fun returnFromGlobal(ctx: ParserRuleContext) {
        val msg = "cannot return from the global scope"
        addErrorMessage(msg, ctx.getStart().line, ctx.getStart().charPositionInLine)
    }


    //Build a full error message given the message, line, and char of the error
    private fun addErrorMessage(msg: String, line: Int, char: Int) {
        errorList.add("Semantic Error at $line:$char: $msg")
    }

    //Iterate through the list of errors and print out the error and the line and character where it occurs
    fun printSemanticErrors() {
        println("================================================================")
        println("==================== SEMANTIC ERRORS FOUND =====================")
        println("================================================================")

        errorList.forEach { println(it) }

        println("\n\n ${errorList.size} semantic errors detected. No further compilation attempted.")
    }

}