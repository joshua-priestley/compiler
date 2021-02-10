// Class to store the symbol table with a reference to the parent symbol table
class SymbolTable(var parentT: SymbolTable?) {
    // Table to store all variables and functions available
    private val table: LinkedHashMap<String, Type> = linkedMapOf()

    // List of the other children tables (of functions)
    private val childrenTables: MutableList<SymbolTable> = mutableListOf()

    init {
        parentT?.addChildTable(this)
    }

    fun addChildTable(child: SymbolTable) {
        childrenTables.add(child)
    }

    fun setParentTable(parentT: SymbolTable?) {
        this.parentT = parentT
    }

    fun addNode(name: String, type: Type) {
        table[name] = type
    }

    fun getNode(name: String): Type? {
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

    fun removeNode(name: String) {
        // Same idea as adding a node
        var currTable: SymbolTable? = this
        while (currTable != null) {
            if (currTable.table.containsKey(name)) {
                currTable.table.remove(name)
                return
            }
            currTable = currTable.parentT
        }
    }

    fun containsNode(name: String): Boolean {
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
}