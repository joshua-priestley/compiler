import kotlin.Int
import kotlin.system.exitProcess

const val ERROR_CODE = 200
const val BASE_MSG = "Semantic Error at "
class SemanticErrorHandler {
    private val errorList: MutableCollection<String> = mutableListOf()

    //Error message for accessing element of null pair
    fun nullTypeAccess() {
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

    //Error message for incompatible types in an expression
    fun incompatibleTypes(expr: String, expected: Type, actual: Type) {
        val line = errorLine()
        val char = errorChar()
        val msg = "Incompatible type at $expr (expected: $expected, actual: $actual)"
        val fullMsg = buildErrorMessage(msg,line,char)

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
    fun printErrors() {
        val errorsIterator = errorList.iterator()
        while (errorsIterator.hasNext()){
            System.err.println(errorsIterator.next())
        }
        exitProcess(ERROR_CODE)
    }

}