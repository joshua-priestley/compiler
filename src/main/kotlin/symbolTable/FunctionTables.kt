package symbolTable

import Type

class FunctionTables {

    private val functionTable: LinkedHashMap<String, SymbolTable> = linkedMapOf()

    fun addFunction(name: String, returnType: Type) {
        val functionST: SymbolTable = SymbolTable()
        functionST.addNode("\$Return", returnType)
        functionTable[name] = functionST
    }

    fun addVarToFunction(funcName: String, varName: String, varType:Type) {
        functionTable[funcName]?.addNode(varName, varType)
    }

    fun getReturnOfFunction(funcName: String): Type? {
        return functionTable[funcName]?.getNode("\$Return")
    }

    fun getVarType(funcName: String, varName: String): Type? {
        return functionTable[funcName]?.getNode(varName)
    }

    fun funcExists(funcName: String): Boolean {
        return functionTable.containsKey(funcName)
    }

    fun varExists(funcName: String, varName: String): Boolean {
        return functionTable.containsKey(funcName) && functionTable[funcName]!!.nodeExists(varName)
    }

}