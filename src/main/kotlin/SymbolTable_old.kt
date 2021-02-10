// Class to store the symbol table with a reference to the parent symbol table
class SymbolTable_old(var parentT: SymbolTable_old?) {
    // Table to store all variables and functions available
    private val table: LinkedHashMap<String, Node> = linkedMapOf()
    // List of the other children tables (of functions)
    private val childrenTableOlds: MutableList<SymbolTable_old> = mutableListOf()

    init {
        parentT?.addChildTable(this)
    }

    fun addChildTable(child: SymbolTable_old) {
        childrenTableOlds.add(child)
    }

    fun setParentTable(parentT: SymbolTable_old?) {
        this.parentT = parentT
    }

    fun addNode(name: String, node: Node) {
        table[name] = node
    }

    fun getNode(name: String): Node? {
        // Check the symbol table and each parent's table for an entry match
        var currTableOld: SymbolTable_old? = this
        while (currTableOld != null) {
            val node = currTableOld.table[name]
            if (node != null) {
                return node
            }

            currTableOld = currTableOld.parentT
        }

        // If we reach here then no match was found
        return null
    }

    fun removeNode(name: String) {
        // Same idea as adding a node
        var currTableOld: SymbolTable_old? = this
        while (currTableOld!= null) {
            if (currTableOld.table.containsKey(name)) {
                currTableOld.table.remove(name)
                return
            }
            currTableOld = currTableOld.parentT
        }
    }
}