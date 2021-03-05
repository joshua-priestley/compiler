package AST

import kotlin.Int

// Class to store the symbol table with a reference to the parent symbol table
class SymbolTable(var parentT: SymbolTable?, val ID: Int) {

    // Table to store all variables and functions available
    private val table: LinkedHashMap<String, Type> = linkedMapOf()

    // List of the other children tables (of functions)
    private val childrenTables: LinkedHashMap<Int, SymbolTable> = linkedMapOf()

    private var tableOffset = 0
    private var parameterOffset = 0

    init {
        parentT?.addChildTable(ID, this)
    }

    fun addChildTable(ID: Int, child: SymbolTable) {
        childrenTables[ID] = child
    }

    fun getChildTable(ID: Int): SymbolTable? {
        return childrenTables[ID]
    }

    fun addNode(name: String, type: Type) {
        if (!type.isFunction() && !type.isParameter()) {
            tableOffset += type.getTypeSize()
            table[name] = type.setOffset(tableOffset)
        } else if (type.isParameter()) {
            parameterOffset += type.getTypeSize()
            table[name] = type.setOffset(parameterOffset)
        } else {
            table[name] = type
        }
    }

    fun getNodeLocal(name: String): Type? {
        return table[name]
    }

    fun getNodeGlobal(name: String, setType: Type? = null): Type? {
        // Check the symbol table and each parent's table for an entry match
        var currTable: SymbolTable? = this
        while (currTable != null) {
            val type = currTable.table[name]
            if (type != null) {
                if(setType != null) {
                    currTable.table[name] = setType
                }
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

    fun localStackSize(): Int {
        return this.tableOffset
    }

    fun addToOffset(n: Int) {
        this.tableOffset += n
    }

    private fun offsetInTable(name: String): Int {
        val entry = getNodeGlobal(name)
        assert(entry != null && !entry.isFunction())
        return entry!!.getOffset() + (if (entry.getTypeSize() == 1 && entry.isParameter()) 3 else 0)
    }

    fun getStackOffset(name: String): Int {
        var offset = 0
        var scopeST = this

        // Get to the right scope
        while (!scopeST.containsNodeLocal(name)) {
            offset += scopeST.localStackSize()
            scopeST = scopeST.parentT!!
        }

        // Now in the scope that has the variable we want
        return offset + offsetInTable(name)
    }
}