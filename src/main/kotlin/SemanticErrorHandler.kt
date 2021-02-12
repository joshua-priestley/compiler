import org.antlr.v4.runtime.ParserRuleContext

class SemanticErrorHandler {
    private val errorList: MutableSet<String> = mutableSetOf()

    fun hasSemanticErrors() = errorList.isNotEmpty()

    fun fstSndMustBePair(ctx: ParserRuleContext) {
        val msg = "when calling fst or snd, must be a pair variable"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun mismatchedArgs(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "number of function arguments does not match (expected: $expected, actual: $actual)"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun mismatchedParamTypes(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "mismatched paramater types (expected: $expected, actual: $actual)"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun funRefBeforeAss(expr: String, ctx: ParserRuleContext) {
        val msg = "function referenced before assignment at $expr"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun newPairFalse(pairIndex: String, ctx: ParserRuleContext) {
        val msg = "newpair expression $pairIndex is false"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun incompatibleExitCode(expr: String, actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible exit code at $expr (expected: INT, actual: $actual)"
        addErrorMessage(msg, errorPosition(ctx))
    }

    //Error message for redefining variable within same scope
    fun redefinedVariable(ident: String, ctx: ParserRuleContext) {
        val msg = "\"$ident\" is already defined in this scope"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun assigningFunction(ident: String, ctx: ParserRuleContext) {
        val msg = "\"$ident\" is a function, cannot assign a value to a function"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun undefinedVar(ident: String, ctx: ParserRuleContext) {
        val msg = "\"$ident\" is not defined in this scope"
        addErrorMessage(msg, errorPosition(ctx))
    }

    //Error message for incompatible types in an expression
    fun incompatibleTypeDecl(expr: String, expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible type declaration at $expr (expected: $expected, actual: $actual)"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun incompatibleTypeAss(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible type assignment (expected: $expected, actual: $actual)"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun incompatibleTypeReturn(expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible return type (expected: $expected, actual: $actual)"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun incompatibleTypeFree(actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible return type (expected: Pair, actual: $actual)"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun arrayDifferingTypes(array: String, ctx: ParserRuleContext) {
        val msg = "array $array has differing element types"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun readNotVariable(ctx: ParserRuleContext) {
        val msg = "read must be stored in a variable"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun readTypeError(actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible type assignment at READ (expected: {INT, CHAR}, actual: $actual)"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun arrayIndex(index: String, expected: String, actual: String, ctx: ParserRuleContext) {
        val msg = "array index at $index is invalid (expected: $expected, actual: $actual)"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun binaryOpType(ctx: ParserRuleContext) {
        val msg = "incompatible types for binary operator"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun indexStrings(ctx: ParserRuleContext) {
        val msg = "strings cannot be indexed"
        addErrorMessage(msg, errorPosition(ctx))
    }

    fun conditionalBoolean(actual: String, ctx: ParserRuleContext) {
        val msg = "incompatible type for conditional expression (expected: BOOL, actual: $actual)"
        addErrorMessage(msg, errorPosition(ctx))
    }

    //Error message for returning from global scope
    fun returnFromGlobal(ctx: ParserRuleContext) {
        val msg = "cannot return from the global scope"
        addErrorMessage(msg, errorPosition(ctx))
    }


    //Build a full error message given the message, line, and char of the error
    private fun addErrorMessage(msg: String, location: String) {
        errorList.add("Semantic Error at $location: $msg")
    }

    //Get line and character of error
    private fun errorPosition(ctx: ParserRuleContext): String {
        return "${ctx.getStart().line}:${ctx.getStart().charPositionInLine}"
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
