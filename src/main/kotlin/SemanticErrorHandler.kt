import kotlin.Int

class SemanticErrorHandler {
    private final val ERROR_CODE = 200
    private val errorList: MutableCollection<String> = mutableListOf()
    fun nullTypeAccess() {
        val errorMsg = "accessing element of null pair"
        errorList.add(errorMsg)
    }

    fun incompatibleType(type: Type) {
        val errorMsg = "Incompatible type " + type.toString()
        errorList.add(errorMsg)
    }

    fun undefinedVariable(ident: String) {
        val errorMsg = "Variable " + ident + " is not defined in this scope"
        errorList.add(errorMsg)
    }

    fun incompatibleTypes(expr: String, expected: Type, actual: Type) {
        val errorMsg = "Incompatible type at " + expr + " (expected: " + expected + ", actual: " + actual + ")"
        errorList.add(errorMsg)
    }

    fun returnFromGlobal(){
        val errorMsg = "Cannot return from the global scope."
        errorList.add(errorMsg)
    }

    fun errorLine() : Int{
        return 0;
    }

    fun printErrors() {
        val errorsIterator = errorList.iterator()
        while (errorsIterator.hasNext()){
            System.err.println(errorsIterator.next())
        }
        System.exit(ERROR_CODE)
    }

}