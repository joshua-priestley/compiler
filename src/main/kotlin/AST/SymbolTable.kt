package AST

// Class to store the symbol table with a reference to the parent symbol table
class SymbolTable(var parentT: SymbolTable?, val ID: kotlin.Int) {

    // Table to store all variables and functions available
    private val table: LinkedHashMap<String, Type> = linkedMapOf()

    // List of the other children tables (of functions)
    private val childrenTables: LinkedHashMap<kotlin.Int, SymbolTable> = linkedMapOf()

    init {
        parentT?.addChildTable(ID, this)
    }

    fun addChildTable(ID: kotlin.Int, child: SymbolTable) {
        childrenTables[ID] = child
    }

    fun addNode(name: String, type: Type) {
        table[name] = type
    }

    fun getNodeLocal(name: String): Type? {
        return table[name]
    }

    fun getNodeGlobal(name: String): Type? {
        // Check the symbol table and each parent's table for an entry match
        var currTable: SymbolTable? = this
        while (currTable != null) {
            val type = currTable.table[name]
            if (type != null) {
                return type
            }

            currTable = currTable.parentT
        }
        // If we reach here then no match was found
        return null
    }

    fun containsNodeLocal(name: String): Boolean {
        return table.containsKey(name)
    }

    fun containsNodeGlobal(name: String): Boolean {
        // Same as getting a node but just returning true or false
        var currTable: SymbolTable? = this
        while (currTable != null) {
            val type = currTable.table[name]
            if (type != null) {
                return true
            }

            currTable = currTable.parentT
        }
        return false
    }

    fun printChildTables() {
        println(childrenTables)
        for (key in childrenTables.keys) {
            childrenTables[key]?.printChildTables()
        }
    }

}