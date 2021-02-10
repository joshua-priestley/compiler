package symbolTable

import Type

class SymbolTable {
    private val symbolTable: LinkedHashMap<String, Type> = linkedMapOf()

    fun addNode(name:String, type:Type) {
        symbolTable[name] = type
    }

    fun getNode(name: String): Type? {
        return symbolTable[name]
    }

    fun removeNode(name: String) {
        if (symbolTable.containsKey(name)) {
            symbolTable.remove(name)
        }
    }

    fun nodeExists(name: String): Boolean {
        return symbolTable.containsKey(name)
    }
}